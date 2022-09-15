package com.atguigu.gmall.order.service;


import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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

    /**
     * 修改订单状态
     *
     * @param orderId  订单ID
     * @param userId   用户ID
     * @param status   目标状态
     * @param expected 当前状态
     */
    void changeOrderStatus(Long orderId, Long userId, ProcessStatus status, List<ProcessStatus> expected);
}
