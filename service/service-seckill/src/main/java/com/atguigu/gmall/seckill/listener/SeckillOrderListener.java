package com.atguigu.gmall.seckill.listener;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.to.mq.SeckillTempOrderMsg;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.atguigu.gmall.rabbit.service.MqService;
import com.atguigu.gmall.seckill.service.CacheService;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Connor
 * @date 2022/9/21
 */
@Component
@Slf4j
public class SeckillOrderListener {

    @Autowired
    private SeckillGoodsService seckillGoodsService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private MqService mqService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MqConst.QUEUE_SECKILL_ORDERWAIT, durable = "true", exclusive = "false", autoDelete = "false"),
                    exchange = @Exchange(value = MqConst.EXCHANGE_SECKILL_EVENT, type = ExchangeTypes.TOPIC, durable = "true", autoDelete = "false"),
                    key = MqConst.RK_SECKILL_ORDERWAIT
            )
    )
    public void seckillOrderListener(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        SeckillTempOrderMsg msg = Jsons.toObj(message, SeckillTempOrderMsg.class);
        log.info("监听到秒杀扣库存消息....{}", msg);
        try {
            //数据库stock_count字段为无符号,小于0会报错
            seckillGoodsService.deduceStock(msg.getSkuId());
            rabbitTemplate.convertAndSend(MqConst.EXCHANGE_ORDER_EVENT,
                    MqConst.RK_ORDER_SECKILL_CREATED,
                    Jsons.toStr(msg));
            //redis修改一下标志位
            cacheService.successToDeduceStock(SysRedisConst.CACHE_SECKILL_ORDER + msg.getSkuCode());
            channel.basicAck(tag, false);
        } catch (DataIntegrityViolationException e) {
            log.error("扣库存失败:{}", e.getMessage());
            //扣库存失败。redis临时单改成 x 错误标志。
            cacheService.failToDeduceStock(SysRedisConst.CACHE_SECKILL_ORDER + msg.getSkuCode());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("业务失败:{}", e.getMessage());
            mqService.reConsumeMsg(10L, SysRedisConst.MQ_RETRY_SECKILL_STOCK_DEDUCE + msg.getSkuCode(), tag, channel);
        }


    }
}
