package com.atguigu.gmall.order.business;

import com.atguigu.gmall.model.vo.order.CartInfoVo;
import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;

import java.util.List;

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

    /**
     * 检查商品价格是否有变化
     *
     * @param orderDetailList
     * @return
     */
    List<String> checkPrice(List<CartInfoVo> orderDetailList);

    /**
     * 检查库存是否足够
     *
     * @param orderDetailList
     * @return 库存不足的商品名称
     */
    List<String> checkStock(List<CartInfoVo> orderDetailList);

    /**
     * 验证交易流水号(令牌)
     *
     * @param tradeNo
     * @return
     */
    boolean checkToken(String tradeNo);

    /**
     * 关闭超时订单
     *
     * @param orderId
     * @param userId
     */
    void closeOrder(Long orderId, Long userId);
}
