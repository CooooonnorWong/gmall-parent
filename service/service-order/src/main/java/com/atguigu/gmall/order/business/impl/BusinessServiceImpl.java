package com.atguigu.gmall.order.business.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.utils.AuthUtils;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.feign.ware.WareFeignClient;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.order.CartInfoVo;
import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import com.atguigu.gmall.order.business.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
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
public class BusinessServiceImpl implements BusinessService {
    @Autowired
    private CartFeignClient cartFeignClient;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private WareFeignClient wareFeignClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ExecutorService executor;

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
}
