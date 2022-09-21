package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.to.mq.SeckillTempOrderMsg;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.atguigu.gmall.rabbit.service.MqService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Connor
 * @date 2022/9/21
 */
@Component
@Slf4j
public class SeckillOrderListener {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private MqService mqService;
    @Autowired
    private ExecutorService executor;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MqConst.QUEUE_ORDER_SECKILL_ORDER_CREATED, durable = "true", exclusive = "false", autoDelete = "false"),
                    exchange = @Exchange(value = MqConst.EXCHANGE_ORDER_EVENT, type = ExchangeTypes.TOPIC, durable = "true", autoDelete = "false"),
                    key = MqConst.RK_ORDER_SECKILL_CREATED
            )
    )
    public void seckillOrderListener(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        SeckillTempOrderMsg msg = Jsons.toObj(message, SeckillTempOrderMsg.class);
        log.info("监听到秒杀成功消息....{},开始将订单插入数据库", msg);
        try {
            String orderCacheKey = SysRedisConst.CACHE_SECKILL_ORDER + msg.getSkuCode();
            String jsonStr = redisTemplate.opsForValue().get(orderCacheKey);
            OrderInfo orderInfo = Jsons.toObj(jsonStr, OrderInfo.class);
            this.fillOrderInfo(orderInfo);
            orderInfoService.save(orderInfo);
            log.info("订单插入完成");
            orderInfo.getOrderDetailList().stream().forEach(orderDetail -> orderDetail.setOrderId(orderInfo.getId()));
            orderDetailService.saveBatch(orderInfo.getOrderDetailList());
            log.info("订单明细插入完成");
            executor.submit(() -> redisTemplate.opsForValue().set(orderCacheKey, Jsons.toStr(orderInfo), 1L, TimeUnit.DAYS));
            channel.basicAck(tag, false);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("业务失败:{}", e.getMessage());
            mqService.reConsumeMsg(10L, SysRedisConst.MQ_RETRY_SECKILL_ORDER_CREATED + msg.getSkuCode(), tag, channel);
        }
    }

    private void fillOrderInfo(OrderInfo orderInfo) {
        orderInfo.setOrderStatus(ProcessStatus.UNPAID.name());
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
        orderInfo.setPaymentWay("ONLINE");
        orderInfo.setCreateTime(new Date());
        orderInfo.setExpireTime(new Date(System.currentTimeMillis() + 1000 * SysRedisConst.ORDER_CLOSE_TTL));
        orderInfo.setOutTradeNo(System.currentTimeMillis() + "_" + orderInfo.getUserId() + "_" + UUID.randomUUID().toString().replace("-", ""));
        orderInfo.setRefundableTime(new Date(System.currentTimeMillis() + SysRedisConst.ORDER_REFUND_TTL * 1000));
//        UserAddress userAddress = userFeignClient.getDefaultUserAddress(orderInfo.getUserId()).getData();
//        orderInfo.setConsignee(userAddress.getConsignee());
//        orderInfo.setConsigneeTel(userAddress.getPhoneNum());
//        orderInfo.setDeliveryAddress(userAddress.getUserAddress());
    }
}
