package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.to.mq.WareDeduceStatusMsg;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.atguigu.gmall.rabbit.service.MqService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Connor
 * @date 2022/9/20
 */
@Component
@Slf4j
public class OrderStockDeduceListener {
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private MqService mqService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MqConst.QUEUE_WARE_ORDER, durable = "true", exclusive = "false", autoDelete = "false"),
                    exchange = @Exchange(value = MqConst.EXCHANGE_WARE_ORDER, type = ExchangeTypes.DIRECT, durable = "true", autoDelete = "false"),
                    key = MqConst.RK_WARE_ORDER
            )
    )
    public void orderStockDeduceListener(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        WareDeduceStatusMsg statusMsg = Jsons.toObj(message, WareDeduceStatusMsg.class);
        try {
            log.info("监听到库存扣减结果消息,deliveryTag:{}, statusMsg:{}", tag, statusMsg);
            Long userId = orderInfoService.getObj(new LambdaQueryWrapper<OrderInfo>()
                            .select(OrderInfo::getUserId)
                            .eq(OrderInfo::getId, statusMsg.getOrderId())
                    , o -> Long.parseLong(o.toString()));
            ProcessStatus processStatus = null;
            switch (statusMsg.getStatus()) {
                case "DEDUCTED":
                    processStatus = ProcessStatus.WAITING_DELEVER;
                    break;
                case "OUT_OF_STOCK":
                    processStatus = ProcessStatus.STOCK_EXCEPTION;
                    break;
                default:
                    processStatus = ProcessStatus.PAID;
            }
            orderInfoService.changeOrderStatus(statusMsg.getOrderId(), userId, processStatus, Arrays.asList(ProcessStatus.PAID));
            channel.basicAck(tag, false);
        } catch (IOException e) {
            String key = SysRedisConst.MQ_RETRY_WARE_STOCK_DEDUCE + statusMsg.getOrderId();
            mqService.reConsumeMsg(10L, key, tag, channel);
        }
    }
}
