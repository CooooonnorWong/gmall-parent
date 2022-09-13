package com.atguigu.gmall.order.business;

import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;

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

    /**
     * 提交订单
     *
     * @param orderSubmitVo
     * @param tradeNo
     * @return
     */
    Long submitOrder(OrderSubmitVo orderSubmitVo, String tradeNo);

    boolean checkToken(String tradeNo);
}
