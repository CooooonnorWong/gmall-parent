package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.model.to.mq.WareDeduceMsg;
import com.atguigu.gmall.model.to.mq.WareDeduceSkuInfo;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.service.PaymentInfoService;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @author Connor
 * @date 2022/9/19
 */
@Component
@Slf4j
public class OrderPaidListener {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private PaymentInfoService paymentInfoService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private MqService mqService;
    @Autowired
    private ExecutorService executor;

    @Transactional(rollbackFor = Exception.class)
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MqConst.QUEUE_ORDER_PAID, durable = "true", exclusive = "false", autoDelete = "false"),
                    exchange = @Exchange(value = MqConst.EXCHANGE_ORDER_EVENT, type = ExchangeTypes.TOPIC, durable = "true", autoDelete = "false"),
                    key = MqConst.RK_ORDER_PAID
            ))
    public void orderPaidListener(Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        Map<String, String> params = Jsons.toObj(new String(message.getBody()), Map.class);
        try {
            //幂等操作,防止消息消费失败重复执行
            //保存支付信息
            PaymentInfo paymentInfo = paymentInfoService.savePaymentInfo(params);
            log.info("订单支付信息保存成功,订单ID:{}", paymentInfo.getOrderId());
            //修改订单状态,幂等操作
            orderInfoService.changeOrderStatus(paymentInfo.getOrderId(),
                    paymentInfo.getUserId(),
                    ProcessStatus.PAID,
                    Arrays.asList(ProcessStatus.UNPAID, ProcessStatus.CLOSED));
            log.info("订单状态修改成功,订单ID:{},处理状态:{},订单状态:{}", paymentInfo.getOrderId(), ProcessStatus.PAID.name(), ProcessStatus.PAID.getOrderStatus().name());
            WareDeduceMsg wareDeduceMsg = prepareWareDeduceMsg(paymentInfo);
            //向库存交换机发消息,减库存
            rabbitTemplate.convertAndSend(MqConst.EXCHANGE_WARE_EVENT,
                    MqConst.RK_WARE_STOCK,
                    Jsons.toStr(wareDeduceMsg));
            log.info("减库存消息发送成功,订单ID:{}", paymentInfo.getOrderId());
            channel.basicAck(tag, false);
        } catch (Exception e) {
            e.printStackTrace();
            String uniqueKey = SysRedisConst.MQ_RETRY_ORDER_PAID + params.get("trade_no");
            mqService.reConsumeMsg(10L, uniqueKey, tag, channel);
        }

    }

    private WareDeduceMsg prepareWareDeduceMsg(PaymentInfo paymentInfo) {
        WareDeduceMsg msg = new WareDeduceMsg();
        CompletableFuture<Void> part1 = CompletableFuture.runAsync(() -> {
            OrderInfo orderInfo = orderInfoService.getOne(new LambdaQueryWrapper<OrderInfo>()
                    .select(OrderInfo::getId)
                    .eq(OrderInfo::getOutTradeNo, paymentInfo.getOutTradeNo())
                    .eq(OrderInfo::getUserId, paymentInfo.getUserId()));
            msg.setOrderId(paymentInfo.getOrderId());
            msg.setConsignee(orderInfo.getConsignee());
            msg.setConsigneeTel(orderInfo.getConsigneeTel());
            msg.setOrderComment(orderInfo.getOrderComment());
            msg.setOrderBody(orderInfo.getTradeBody());
            msg.setDeliveryAddress(orderInfo.getDeliveryAddress());
            msg.setPaymentWay("1");
        }, executor);
        CompletableFuture<Void> part2 = CompletableFuture.runAsync(() -> {
            msg.setDetails(orderDetailService.list(new LambdaQueryWrapper<OrderDetail>()
                            .eq(OrderDetail::getUserId, paymentInfo.getUserId())
                            .eq(OrderDetail::getOrderId, paymentInfo.getOrderId()))
                    .stream()
                    .map(orderDetail -> new WareDeduceSkuInfo(orderDetail.getSkuId(), orderDetail.getSkuNum(), orderDetail.getSkuName()))
                    .collect(Collectors.toList()));
        }, executor);
        CompletableFuture.allOf(part1, part2).join();
        return msg;
    }
}
