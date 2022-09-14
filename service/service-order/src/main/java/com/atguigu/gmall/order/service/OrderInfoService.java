package com.atguigu.gmall.order.service;


import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Connor
 * @description 针对表【order_info(订单表 订单表)】的数据库操作Service
 * @createDate 2022-09-13 09:52:43
 */
public interface OrderInfoService extends IService<OrderInfo> {

    /**
     * 保存订单信息
     *
     * @param orderSubmitVo
     * @param tradeNo
     * @return
     */
    Long saveOrder(OrderSubmitVo orderSubmitVo, String tradeNo);
}
