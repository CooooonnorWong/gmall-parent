package com.atguigu.gmall.order.mq;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.rabbit.constant.MqConst;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Connor
 * @date 2022/9/15
 */
@Configuration
public class OrderEventMqConfig {

    /**
     * 订单微服务交换机
     *
     * @return
     */
    @Bean
    public Exchange orderEventExchange() {
        return new TopicExchange(MqConst.EXCHANGE_ORDER_EVENT, true, false);
    }

    /**
     * 订单延迟队列
     *
     * @return
     */
    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", SysRedisConst.ORDER_CLOSE_TTL * 1000);
//        args.put("x-message-ttl", 15L * 1000);
        args.put("x-dead-letter-exchange", MqConst.EXCHANGE_ORDER_EVENT);
        args.put("x-dead-letter-routing-key", MqConst.RK_ORDER_DEAD);
        return new Queue(MqConst.QUEUE_ORDER_DELAY, true, false, false, args);
    }

    /**
     * 订单死单队列
     *
     * @return
     */
    @Bean
    public Queue orderDeadQueue() {
        return new Queue(MqConst.QUEUE_ORDER_DEAD, true, false, false);
    }

    /**
     * 订单延迟队列绑定订单交换机
     *
     * @return
     */
    @Bean
    public Binding orderDelayQueueBindingOrderEventExchange() {
        /**
         * String destination, 目的地
         * DestinationType destinationType, 目的地类型
         * String exchange, 交换机
         * String routingKey, 路由键
         * @Nullable Map<String, Object> arguments 参数项
         *
         * 这个exchange交换机和这个destinationType类型的目的地（destination）
         * 使用routingKey进行绑定，
         */
        return new Binding(MqConst.QUEUE_ORDER_DELAY,
                Binding.DestinationType.QUEUE,
                MqConst.EXCHANGE_ORDER_EVENT,
                MqConst.RK_ORDER_CREATED,
                null);
    }

    /**
     * 订单死单队列绑定订单交换机
     *
     * @return
     */
    @Bean
    public Binding orderDeadQueueBindingOrderEventExchange() {
        return new Binding(MqConst.QUEUE_ORDER_DEAD,
                Binding.DestinationType.QUEUE,
                MqConst.EXCHANGE_ORDER_EVENT,
                MqConst.RK_ORDER_DEAD,
                null);
    }
}
