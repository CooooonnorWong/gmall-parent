package com.atguigu.gmall.pay.service;

import com.alipay.api.AlipayApiException;

import java.util.Map;

/**
 * @author Connor
 * @date 2022/9/16
 */
public interface PaymentService {
    /**
     * 获取Alipay的html自动提交表单进行支付页面的跳转
     *
     * @param orderId
     * @return
     */
    String getAlipayHtmlPage(Long orderId) throws AlipayApiException;

    /**
     * 验证发来的数据是否是Alipay的原始数据
     *
     * @param params
     * @return
     */
    boolean rsaCheck(Map<String, String> params) throws AlipayApiException;

    /**
     * 发送订单已支付消息到队列
     *
     * @param params
     */
    void sendOrderPaidMsg(Map<String, String> params);
}
