package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.common.utils.AuthUtils;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.to.mq.OrderMsg;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Connor
 * @description 针对表【order_info(订单表 订单表)】的数据库操作Service实现
 * @createDate 2022-09-13 09:52:43
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
        implements OrderInfoService {
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveOrder(OrderSubmitVo orderSubmitVo, String tradeNo) {
        OrderInfo orderInfo = prepareOrderInfo(orderSubmitVo, tradeNo);
        //保存订单信息
        this.save(orderInfo);
        List<OrderDetail> orderDetail = prepareOrderDetail(orderSubmitVo, orderInfo);
        //保存订单明细
        orderDetailService.saveBatch(orderDetail);
        //订单插入完成
        //向消息队列发送消息 -> 用户一定时间内未支付就进行关单操作
        rabbitTemplate.convertAndSend(MqConst.EXCHANGE_ORDER_EVENT,
                MqConst.RK_ORDER_CREATED,
                Jsons.toStr(new OrderMsg(orderInfo.getId(), orderInfo.getUserId())));
        return orderInfo.getId();
    }

    @Override
    public void changeOrderStatus(Long orderId, Long userId, ProcessStatus status, List<ProcessStatus> expected) {
        String orderStatus = status.getOrderStatus().name();
        String processStatus = status.name();
        //CAS 先比较再修改
        List<String> expects = expected.stream()
                .map(ProcessStatus::name)
                .collect(Collectors.toList());
        //幂等性修改
        baseMapper.updateOrderStatus(orderId, userId, orderStatus, processStatus, expects);
    }

    private List<OrderDetail> prepareOrderDetail(OrderSubmitVo vo, OrderInfo info) {
        return vo.getOrderDetailList().stream()
                .map(cartInfoVo -> {
                    OrderDetail detail = new OrderDetail();
                    detail.setOrderId(info.getId());
                    detail.setSkuId(cartInfoVo.getSkuId());
                    detail.setUserId(info.getUserId());
                    detail.setSkuName(cartInfoVo.getSkuName());
                    detail.setImgUrl(cartInfoVo.getImgUrl());
                    detail.setOrderPrice(cartInfoVo.getOrderPrice());
                    detail.setSkuNum(cartInfoVo.getSkuNum());
                    detail.setHasStock(cartInfoVo.getHasStock());
                    detail.setCreateTime(info.getCreateTime());
                    detail.setSplitTotalAmount(cartInfoVo.getOrderPrice().multiply(new BigDecimal(cartInfoVo.getSkuNum().toString())));
                    detail.setSplitActivityAmount(new BigDecimal("0"));
                    detail.setSplitCouponAmount(new BigDecimal("0"));
                    detail.setId(0L);
                    return detail;
                })
                .collect(Collectors.toList());
    }

    private OrderInfo prepareOrderInfo(OrderSubmitVo orderSubmitVo, String tradeNo) {
        OrderInfo info = new OrderInfo();
        //收货人
        info.setConsignee(orderSubmitVo.getConsignee());
        //收货电话
        info.setConsigneeTel(orderSubmitVo.getConsigneeTel());
        //支付方式
        info.setPaymentWay(orderSubmitVo.getPaymentWay());
        //送货地址
        info.setDeliveryAddress(orderSubmitVo.getDeliveryAddress());
        //订单备注
        info.setOrderComment(orderSubmitVo.getOrderComment());
        //用户ID
        info.setUserId(AuthUtils.currentAuthInfo().getUserId());
        //交易流水号(对外交易号)
        info.setOutTradeNo(tradeNo);
        //交易体。 拿到这个订单中购买的第一个商品的名字，作为订单的体
        info.setTradeBody(orderSubmitVo.getOrderDetailList().get(0).getSkuName());
        //创建时间
        info.setCreateTime(new Date());
        //过期时间。订单多久没支付以后就过期。过期未支付：订单就会成为已关闭状态；
        info.setExpireTime(new Date(System.currentTimeMillis() + 1000 * SysRedisConst.ORDER_CLOSE_TTL));
        //订单状态
        info.setOrderStatus(OrderStatus.UNPAID.name());
        //订单处理状态
        info.setProcessStatus(ProcessStatus.UNPAID.name());
        //物流单号(暂不处理)
        info.setTrackingNo("");

        //父订单id（拆单场景）
        info.setParentOrderId(0L);
        //订单的图片(默认第一个商品的图片)
        info.setImgUrl(orderSubmitVo.getOrderDetailList().get(0).getImgUrl());
        //当前单被优惠活动减掉的金额
        info.setActivityReduceAmount(new BigDecimal("0"));
        //当前单被优惠券减掉的金额
        info.setCouponAmount(new BigDecimal("0"));
        BigDecimal totalAmount = orderSubmitVo.getOrderDetailList().stream()
                .map(cartInfoVo -> cartInfoVo.getOrderPrice().multiply(new BigDecimal(cartInfoVo.getSkuNum().toString())))
                .reduce(BigDecimal::add)
                .get();
        //订单总额 = OriginalTotalAmount - ActivityReduceAmount - CouponAmount
        info.setTotalAmount(totalAmount);
        //原始金额
        info.setOriginalTotalAmount(totalAmount);
        info.setRefundableTime(new Date(System.currentTimeMillis() + SysRedisConst.ORDER_REFUND_TTL * 1000));
        //运费 第三方物流平台，动态计算运费
        info.setFeightFee(new BigDecimal("0"));
        //操作时间
        info.setOperateTime(new Date());
        return info;
    }
}




