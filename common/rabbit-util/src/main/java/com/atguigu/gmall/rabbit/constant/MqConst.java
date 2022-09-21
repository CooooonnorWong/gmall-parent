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

    /**
     * 库存交换机名称
     */
    String EXCHANGE_WARE_EVENT = "exchange.direct.ware.stock";

    /**
     *
     */
    String EXCHANGE_WARE_ORDER = "exchange.direct.ware.order";
    /**
     * 秒杀交换机
     */
    String EXCHANGE_SECKILL_EVENT = "seckill-event-exchange";
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

    /**
     * 订单支付队列
     */
    String QUEUE_ORDER_PAID = "order-paid-queue";

    /**
     *
     */
    String QUEUE_WARE_ORDER = "queue.ware.order";
    /**
     * 等待扣减库存的秒杀订单队列
     */
    String QUEUE_SECKILL_ORDERWAIT = "seckill-orderwait-queue";
    /**
     * 秒杀成功订单创建队列
     */
    String QUEUE_ORDER_SECKILL_ORDER_CREATED = "order-seckill-created-queue";
//=====================Routing Key======================

    /**
     * 订单死信路由键
     */
    String RK_ORDER_DEAD = "order.dead";

    /**
     * 订单新建路由键
     */
    String RK_ORDER_CREATED = "order.created";
    /**
     * 订单支付路由键
     */
    String RK_ORDER_PAID = "order.paid";
    /**
     * 库存路由键
     */
    String RK_WARE_STOCK = "ware.stock";

    /**
     *
     */
    String RK_WARE_ORDER = "ware.order";
    /**
     * 等待扣减库存的秒杀路由键
     */
    String RK_SECKILL_ORDERWAIT = "seckill.order.wait";
    /**
     * 秒杀商品订单创建路由键
     */
    String RK_ORDER_SECKILL_CREATED = "order.seckill.created";
}
