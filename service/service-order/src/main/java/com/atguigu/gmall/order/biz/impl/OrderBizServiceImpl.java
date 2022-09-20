package com.atguigu.gmall.order.biz.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.common.utils.AuthUtils;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.product.ProductFeignClient;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.feign.ware.WareFeignClient;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.order.*;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
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
public class OrderBizServiceImpl implements OrderBizService {
    @Autowired
    private CartFeignClient cartFeignClient;
    @Autowired
    private UserFeignClient userFeignClient;
    @Qualifier("com.atguigu.gmall.feign.ware.WareFeignClient")
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
    @Autowired
    private OrderDetailService orderDetailService;

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
                .get()), executor);
        CompletableFuture<Void> totalAmountFuture = cartInfoVoFuture.thenAcceptAsync(voList -> confirmDataVo.setTotalAmount(voList.stream()
                .map(vo -> vo.getOrderPrice().multiply(new BigDecimal(vo.getSkuNum().toString())))
                .reduce(BigDecimal::add)
                .get()), executor);
        CompletableFuture<Void> userAddressFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(attributes);
            List<UserAddress> userAddressList = userFeignClient.getUserAddress().getData();
            confirmDataVo.setUserAddressList(userAddressList);
            RequestContextHolder.resetRequestAttributes();
        }, executor);
        CompletableFuture<Void> tradeNoFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(attributes);
            confirmDataVo.setTradeNo(generateOutTradeNo());
            RequestContextHolder.resetRequestAttributes();
        }, executor);
        CompletableFuture.allOf(cartInfoVoFuture, totalNumFuture, totalAmountFuture, userAddressFuture, tradeNoFuture).join();
        return confirmDataVo;
    }

    @Override
    public String generateOutTradeNo() {
        String tradeNo = System.currentTimeMillis() + "_" + AuthUtils.currentAuthInfo().getUserId() + "_" + UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(SysRedisConst.ORDER_TEMP_TOKEN + tradeNo, "1", 15, TimeUnit.MINUTES);
        return tradeNo;
    }

    @Override
    public Long submitOrder(OrderSubmitVo orderSubmitVo, String outTradeNo) {
        //1.查令牌
        //2.验证令牌
        if (!checkToken(outTradeNo)) {
            throw new GmallException(ResultCodeEnum.TOKEN_INVALID);
        }
        log.info("令牌验证通过: {}", outTradeNo);
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
        //6.发送延迟关单消息,用户规定时间内未支付就关闭订单
        Long orderId = orderInfoService.saveOrder(orderSubmitVo, outTradeNo);
        //7.清除购物车中已经提交订单的商品
        cartFeignClient.deleteChecked();
        return orderId;
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

    @Override
    public void closeOrder(Long orderId, Long userId) {
        List<ProcessStatus> expected = Arrays.asList(ProcessStatus.UNPAID, ProcessStatus.FINISHED);
        ProcessStatus status = ProcessStatus.CLOSED;
        //CAS 先比较再修改
        orderInfoService.changeOrderStatus(orderId, userId, status, expected);
    }

    @Override
    public List<WareChildOrderVo> splitOrder(OrderWareMapVo vo) {
        Long orderId = vo.getOrderId();
        OrderInfo originOrder = orderInfoService.getById(orderId);
        originOrder.setOrderDetailList(orderDetailService.getOrderDetails(orderId, originOrder.getUserId()));

        List<WareMapItem> wareMapItems = Jsons.toObj(vo.getWareSkuMap(), new TypeReference<List<WareMapItem>>() {
        });
        List<OrderInfo> splitOrders = wareMapItems.stream()
                .parallel()
                .map(item -> saveChildOrderInfo(item, originOrder))
                .collect(Collectors.toList());
        orderInfoService.changeOrderStatus(originOrder.getId(), originOrder.getUserId(), ProcessStatus.SPLIT, Arrays.asList(ProcessStatus.PAID));

        return splitOrders2WareChildOrderVo(splitOrders);
    }

    private List<WareChildOrderVo> splitOrders2WareChildOrderVo(List<OrderInfo> splitOrders) {
        return splitOrders.stream()
                .parallel()
                .map(orderInfo -> {
                    WareChildOrderVo childOrderVo = new WareChildOrderVo();
                    childOrderVo.setOrderId(orderInfo.getId());
                    childOrderVo.setConsignee(orderInfo.getConsignee());
                    childOrderVo.setConsigneeTel(orderInfo.getConsigneeTel());
                    childOrderVo.setOrderComment(orderInfo.getOrderComment());
                    childOrderVo.setOrderBody(orderInfo.getTradeBody());
                    childOrderVo.setDeliveryAddress(orderInfo.getDeliveryAddress());
                    childOrderVo.setPaymentWay(orderInfo.getPaymentWay());
                    childOrderVo.setWareId(orderInfo.getWareId());
                    childOrderVo.setDetails(orderInfo.getOrderDetailList()
                            .stream()
                            .parallel()
                            .map(orderDetail -> {
                                WareChildOrderDetailItemVo childOrderDetailItemVo = new WareChildOrderDetailItemVo();
                                childOrderDetailItemVo.setSkuId(orderDetail.getSkuId());
                                childOrderDetailItemVo.setSkuNum(orderDetail.getSkuNum());
                                childOrderDetailItemVo.setSkuName(orderDetail.getSkuName());
                                return childOrderDetailItemVo;
                            })
                            .collect(Collectors.toList()));

                    return childOrderVo;
                })
                .collect(Collectors.toList());
    }

    private OrderInfo saveChildOrderInfo(WareMapItem item, OrderInfo originOrder) {
        List<Long> skuIds = item.getSkuIds();
        Long wareId = item.getWareId();
        List<OrderDetail> currentOrderDetails = originOrder.getOrderDetailList()
                .stream()
                .filter(orderDetail -> skuIds.contains(orderDetail.getSkuId()))
                .collect(Collectors.toList());

        OrderInfo childOrder = new OrderInfo();
        childOrder.setConsignee(originOrder.getConsignee());
        childOrder.setConsigneeTel(originOrder.getConsigneeTel());
        childOrder.setTotalAmount(currentOrderDetails.stream()
                .map(orderDetail -> orderDetail.getOrderPrice().multiply(new BigDecimal(orderDetail.getSkuNum().toString())))
                .reduce(BigDecimal::add)
                .get());
        childOrder.setOrderStatus(originOrder.getOrderStatus());
        childOrder.setUserId(originOrder.getUserId());
        childOrder.setPaymentWay(originOrder.getPaymentWay());
        childOrder.setDeliveryAddress(originOrder.getDeliveryAddress());
        childOrder.setOrderComment(originOrder.getOrderComment());
        childOrder.setOutTradeNo(originOrder.getOutTradeNo());
        childOrder.setTradeBody(currentOrderDetails.get(0).getSkuName());
        childOrder.setCreateTime(new Date());
        childOrder.setExpireTime(originOrder.getExpireTime());
        childOrder.setProcessStatus(originOrder.getProcessStatus());
        //对接物流
        childOrder.setTrackingNo("");
        childOrder.setParentOrderId(originOrder.getId());
        childOrder.setImgUrl(currentOrderDetails.get(0).getImgUrl());
        childOrder.setOrderDetailList(currentOrderDetails);
        childOrder.setWareId(wareId.toString());
        childOrder.setProvinceId(0L);
        childOrder.setActivityReduceAmount(new BigDecimal("0"));
        childOrder.setCouponAmount(new BigDecimal("0"));
        childOrder.setOriginalTotalAmount(new BigDecimal("0"));
        childOrder.setRefundableTime(originOrder.getRefundableTime());
        childOrder.setFeightFee(originOrder.getFeightFee());
        childOrder.setOperateTime(new Date());
        orderInfoService.save(childOrder);
        childOrder.getOrderDetailList().stream().parallel().forEach(orderDetail -> orderDetail.setOrderId(childOrder.getId()));
        orderDetailService.saveBatch(childOrder.getOrderDetailList());
        return childOrder;
    }
}
