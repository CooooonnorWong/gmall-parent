package com.atguigu.gmall.order.service;


import com.atguigu.gmall.model.payment.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @author Connor
 * @description 针对表【payment_info(支付信息表)】的数据库操作Service
 * @createDate 2022-09-13 09:52:43
 */
public interface PaymentInfoService extends IService<PaymentInfo> {

    /**
     * 保存支付信息
     * 幂等操作
     *
     * @param params
     * @return
     */
    PaymentInfo savePaymentInfo(Map<String, String> params);
}
