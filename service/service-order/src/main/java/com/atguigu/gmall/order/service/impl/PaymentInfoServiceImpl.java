package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.mapper.PaymentInfoMapper;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.service.PaymentInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author Connor
 * @description 针对表【payment_info(支付信息表)】的数据库操作Service实现
 * @createDate 2022-09-13 09:52:43
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo>
        implements PaymentInfoService {

    @Autowired
    private OrderInfoService orderInfoService;

    @Override
    public PaymentInfo savePaymentInfo(Map<String, String> params) {
        String outTradeNo = params.get("out_trade_no");
        long userId = Long.parseLong(outTradeNo.split("_")[1]);
        //先查数据库是否已经存在该支付信息,防止重复插入
        PaymentInfo paymentInfo = this.getOne(new LambdaQueryWrapper<PaymentInfo>()
                .eq(PaymentInfo::getOutTradeNo, outTradeNo)
                .eq(PaymentInfo::getUserId, userId));
        if (paymentInfo != null) {
            return paymentInfo;
        }
        paymentInfo = preparePaymentInfo(params);
        this.save(paymentInfo);
        return paymentInfo;
    }

    /**
     * 根据支付宝的回调参数生成支付信息
     *
     * @param params
     * @return
     */
    private PaymentInfo preparePaymentInfo(Map<String, String> params) {
        PaymentInfo paymentInfo = new PaymentInfo();
        String outTradeNo = params.get("out_trade_no");
        long userId = Long.parseLong(outTradeNo.split("_")[1]);
        Long orderId = orderInfoService.getObj(new LambdaQueryWrapper<OrderInfo>()
                        .select(OrderInfo::getId)
                        .eq(OrderInfo::getOutTradeNo, outTradeNo)
                        .eq(OrderInfo::getUserId, userId),
                o -> Long.parseLong(o.toString()));
        paymentInfo.setOutTradeNo(outTradeNo);

        paymentInfo.setOrderId(orderId);
        paymentInfo.setUserId(userId);

        paymentInfo.setPaymentType("ALIPAY");
        paymentInfo.setTradeNo(params.get("trade_no"));
        paymentInfo.setTotalAmount(new BigDecimal(params.get("total_amount")));
        paymentInfo.setSubject(params.get("subject"));
        paymentInfo.setPaymentStatus(params.get("trade_status"));
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setCallbackTime(DateUtil.parseDate(params.get("notify_time"), "yyyy-MM-dd HH:mm:ss"));
        paymentInfo.setCallbackContent(Jsons.toStr(params));

        return paymentInfo;
    }
}




