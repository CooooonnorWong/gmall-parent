package com.atguigu.gmall.order.business.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.utils.AuthUtils;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.product.ProductFeignClient;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.feign.ware.WareFeignClient;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.order.CartInfoVo;
import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.business.BusinessService;
import com.atguigu.gmall.order.service.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Connor
 * @date 2022/9/13
 */
@Service
@Slf4j
public class BusinessServiceImpl implements BusinessService {
    @Autowired
    private CartFeignClient cartFeignClient;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private WareFeignClient wareFeignClient;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ExecutorService executor;
    @Autowired
    private OrderInfoService orderInfoService;

    @Override
    public OrderConfirmDataVo getOrderConfirmData() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        OrderConfirmDataVo confirmDataVo = new OrderConfirmDataVo();
        CompletableFuture<List<CartInfoVo>> cartInfoVoFuture = CompletableFuture.supplyAsync(() -> {
            RequestContextHolder.setRequestAttributes(attributes);
            List<CartInfoVo> voList = cartFeignClient.getChecked().getData().stream()
                    .parallel()
                    .map(cartInfo -> {
                        RequestContextHolder.setRequestAttributes(attributes);
                        CartInfoVo vo = new CartInfoVo();
                        vo.setSkuId(cartInfo.getSkuId());
                        vo.setImgUrl(cartInfo.getImgUrl());
                        vo.setSkuName(cartInfo.getSkuName());
                        vo.setOrderPrice(cartInfo.getSkuPrice());
                        vo.setSkuNum(cartInfo.getSkuNum());
                        vo.setHasStock(wareFeignClient.hasStock(vo.getSkuId(), vo.getSkuNum()));
                        RequestContextHolder.resetRequestAttributes();
                        return vo;
                    }).collect(Collectors.toList());
            confirmDataVo.setDetailArrayList(voList);
            RequestContextHolder.resetRequestAttributes();
            return voList;
        }, executor);
        CompletableFuture<Void> totalNumFuture = cartInfoVoFuture.thenAcceptAsync(voList -> confirmDataVo.setTotalNum(voList.stream()
                .map(CartInfoVo::getSkuNum)
                .reduce(Integer::sum)
                .get()));
        CompletableFuture<Void> totalAmountFuture = cartInfoVoFuture.thenAcceptAsync(voList -> confirmDataVo.setTotalAmount(voList.stream()
                .map(vo -> vo.getOrderPrice().multiply(new BigDecimal(vo.getSkuNum().toString())))
                .reduce(BigDecimal::add)
                .get()));
        CompletableFuture<Void> userAddressFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(attributes);
            List<UserAddress> userAddressList = userFeignClient.getUserAddress().getData();
            confirmDataVo.setUserAddressList(userAddressList);
            RequestContextHolder.resetRequestAttributes();
        });
        CompletableFuture<Void> tradeNoFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(attributes);
            confirmDataVo.setTradeNo(generateTradeNo());
            RequestContextHolder.resetRequestAttributes();
        }, executor);
        CompletableFuture.allOf(cartInfoVoFuture, totalNumFuture, totalAmountFuture, userAddressFuture, tradeNoFuture).join();
        return confirmDataVo;
    }

    @Override
    public String generateTradeNo() {
        String tradeNo = System.currentTimeMillis() + "" + AuthUtils.currentAuthInfo().getUserId() + UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(SysRedisConst.ORDER_TEMP_TOKEN + tradeNo, "1", 15, TimeUnit.MINUTES);
        return tradeNo;
    }

    @Override
    public Long submitOrder(OrderSubmitVo orderSubmitVo, String tradeNo) {
        //1.查令牌
        //2.验证令牌
        if (!checkToken(tradeNo)) {
            throw new GmallException(ResultCodeEnum.TOKEN_INVALID);
        }
        log.info("令牌验证通过: {}", tradeNo);
        //3.验证库存
        List<String> noStockSkus = checkStock(orderSubmitVo.getOrderDetailList());
        if (noStockSkus.size() > 0) {
            throw new GmallException(ResultCodeEnum.ORDER_NO_STOCK.getMessage() + "\n" + noStockSkus.stream()
                    .reduce((s1, s2) -> s1 + " | " + s2)
                    .get(),
                    ResultCodeEnum.ORDER_NO_STOCK.getCode());
        }
        log.info("库存验证通过");
        //4.验证价格
        List<String> priceChangedSkus = checkPrice(orderSubmitVo.getOrderDetailList());
        if (priceChangedSkus.size() > 0) {
            throw new GmallException(
                    ResultCodeEnum.ORDER_PRICE_CHANGED.getMessage() + "\n" + priceChangedSkus.stream()
                            .reduce((k, v) -> k + " | " + v),
                    ResultCodeEnum.ORDER_PRICE_CHANGED.getCode());
        }
        log.info("价格验证通过");
        //5.保存订单
        Long orderId = orderInfoService.saveOrder(orderSubmitVo, tradeNo);
        cartFeignClient.deleteChecked();
        return orderId;
    }

    @Override
    public List<String> checkPrice(List<CartInfoVo> orderDetailList) {
//        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return orderDetailList.stream()
//                .parallel()
                .filter(cartInfoVo -> {
//                    RequestContextHolder.setRequestAttributes(attributes);
                    BigDecimal realPrice = productFeignClient.getRealTimePrice(cartInfoVo.getSkuId()).getData();
//                    RequestContextHolder.resetRequestAttributes();
                    double diff = cartInfoVo.getOrderPrice().subtract(realPrice).doubleValue();
                    return diff > 0.0001 || diff < -0.0001;
                })
                .map(CartInfoVo::getSkuName)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> checkStock(List<CartInfoVo> orderDetailList) {
//        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return orderDetailList.stream()
//                .parallel()
                .filter(cartInfoVo -> {
//                    RequestContextHolder.setRequestAttributes(attributes);
                    String hasStock = wareFeignClient.hasStock(cartInfoVo.getSkuId(), cartInfoVo.getSkuNum());
//                    RequestContextHolder.resetRequestAttributes();
                    return "0".equals(hasStock);
                })
                .map(CartInfoVo::getSkuName)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkToken(String tradeNo) {
        //1、先看有没有，如果有就是正确令牌, 1, 0 。脚本校验令牌
        String lua = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then " +
                "return redis.call(\"del\",KEYS[1]) " +
                "else " +
                "return 0 " +
                "end";
        Long execute = redisTemplate.execute(new DefaultRedisScript<Long>(lua, Long.class),
                Arrays.asList(SysRedisConst.ORDER_TEMP_TOKEN + tradeNo),
                new String[]{"1"});

        if (execute > 0) {
            //令牌正确，并且已经删除
            return true;
        }
        return false;
    }
}
