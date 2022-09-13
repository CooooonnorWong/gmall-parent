package com.atguigu.gmall.order.business;

import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;

/**
 * @author Connor
 * @date 2022/9/13
 */
public interface BusinessService {
    /**
     * 获取购物车结算商品信息
     *
     * @return
     */
    OrderConfirmDataVo getOrderConfirmData();

    /**
     * 生成交易流水号
     *
     * @return
     */
    String generateTradeNo();
}
