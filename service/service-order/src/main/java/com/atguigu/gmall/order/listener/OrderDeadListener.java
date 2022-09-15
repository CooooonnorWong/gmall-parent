package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.to.mq.OrderMsg;
import com.atguigu.gmall.order.business.BusinessService;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Connor
 * @date 2022/9/15
 */
@Component
@Slf4j
public class OrderDeadListener {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private BusinessService businessService;

    @RabbitListener(queues = MqConst.QUEUE_ORDER_DEAD)
    public void closeOrder(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        OrderMsg orderMsg = Jsons.toObj(message, OrderMsg.class);
        try {
            log.info("监听到超时订单:{},正在关闭", orderMsg);
            businessService.closeOrder(orderMsg.getOrderId(), orderMsg.getUserId());
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            log.error("订单关闭业务失败,消息:{}，失败原因:{}", orderMsg, e);
            Long retry = redisTemplate.opsForValue().increment(SysRedisConst.MQ_RETRY_CLOSE_ORDER + orderMsg.getOrderId());
            if (retry > 10L) {
                //重试次数到达限制,不入队
                log.info("关单重试失败,订单ID:{},写入数据库.",orderMsg.getOrderId());
                // TODO: 2022/9/16 关单失败,写入数据库
                channel.basicNack(deliveryTag, false, false);
                redisTemplate.delete(SysRedisConst.MQ_RETRY_CLOSE_ORDER + orderMsg.getOrderId());
            } else {
                channel.basicNack(deliveryTag, false, true);
            }
        }
    }
}
