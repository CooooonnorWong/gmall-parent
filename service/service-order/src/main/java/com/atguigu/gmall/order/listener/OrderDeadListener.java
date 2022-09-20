package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.to.mq.OrderMsg;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.atguigu.gmall.rabbit.service.MqService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
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
    private OrderBizService orderBizService;
    @Autowired
    private MqService mqService;

    @RabbitListener(queues = MqConst.QUEUE_ORDER_DEAD)
    public void closeOrder(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        OrderMsg orderMsg = Jsons.toObj(message, OrderMsg.class);
        try {
            log.info("监听到超时订单:{},正在关闭", orderMsg);
            orderBizService.closeOrder(orderMsg.getOrderId(), orderMsg.getUserId());
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            log.error("订单关闭业务失败,消息:{}，失败原因:{}", orderMsg, e);
            String uniqueKey = SysRedisConst.MQ_RETRY_CLOSE_ORDER + orderMsg.getOrderId();
            mqService.reConsumeMsg(10L, uniqueKey, deliveryTag, channel);
        }
    }
}
