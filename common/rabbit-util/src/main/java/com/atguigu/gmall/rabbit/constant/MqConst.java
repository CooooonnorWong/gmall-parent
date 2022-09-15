package com.atguigu.gmall.rabbit.constant;

/**
 * @author Connor
 * @date 2022/9/15
 */
public interface MqConst {
//=======================Exchange=======================

    /**
     * 订单交换机名称
     */
    String EXCHANGE_ORDER_EVENT = "order-event-exchange";

//========================Queue=========================

    /**
     * 订单延迟队列
     */
    String QUEUE_ORDER_DELAY = "order-delay-queue";
    /**
     * 订单死信队列
     * 存放死单信息
     */
    String QUEUE_ORDER_DEAD = "order-dead-queue";

//=====================Routing Key======================

    /**
     * 订单死信路由键
     */
    String RK_ORDER_DEAD = "order.dead";

    /**
     * 订单新建路由键
     */
    String RK_ORDER_CREATED = "order.created";
}
