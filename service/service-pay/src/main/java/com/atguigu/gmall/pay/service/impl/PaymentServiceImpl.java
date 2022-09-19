package com.atguigu.gmall.pay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.pay.config.AlipayProperties;
import com.atguigu.gmall.pay.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Connor
 * @date 2022/9/16
 */
@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private AlipayProperties alipayProperties;

    @Override
    public String getAlipayHtmlPage(Long orderId) throws AlipayApiException {
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderId).getData();
        if (orderInfo.getExpireTime().before(new Date())) {
            throw new GmallException(ResultCodeEnum.ORDER_EXPIRED);
        }

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayProperties.getNotifyUrl());
        request.setReturnUrl(alipayProperties.getReturnUrl());

        Map<String, String> bizContent = new HashMap<>();
        bizContent.put("out_trade_no", orderInfo.getOutTradeNo());
        bizContent.put("total_amount", orderInfo.getTotalAmount().toString());
        bizContent.put("subject", "尚品汇订单-" + orderInfo.getOutTradeNo());
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        bizContent.put("body", orderInfo.getTradeBody());
        bizContent.put("time_expire", DateUtil.formatDate(orderInfo.getExpireTime(), "yyyy-MM-dd HH:mm:ss"));
        request.setBizContent(Jsons.toStr(bizContent));

        return alipayClient.pageExecute(request).getBody();
    }

    @Override
    public boolean rsaCheck(Map<String, String> params) throws AlipayApiException {
        return AlipaySignature.rsaCheckV1(params,
                alipayProperties.getAlipayPublicKey(),
                alipayProperties.getCharset(),
                alipayProperties.getSignType());
    }
}
