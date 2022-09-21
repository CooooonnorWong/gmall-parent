package com.atguigu.gmall.seckill.biz.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.common.utils.AuthUtils;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.to.mq.SeckillTempOrderMsg;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.seckill.SeckillOrderConfirmVo;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.atguigu.gmall.seckill.biz.SeckillBizService;
import com.atguigu.gmall.seckill.service.CacheService;
import com.atguigu.gmall.seckill.util.BizUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Connor
 * @date 2022/9/21
 */
@Service
public class SeckillBizServiceImpl implements SeckillBizService {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserFeignClient userFeignClient;

    @Override
    public String generateSeckillCode(Long skuId) {
        SeckillGoods goods = cacheService.getSeckillGoodsInfo(skuId);
        if (goods == null) {
            throw new GmallException(ResultCodeEnum.SECKILL_ILLEGAL);
        }
        if (new Date().before(goods.getStartTime())) {
            throw new GmallException(ResultCodeEnum.SECKILL_NO_START);
        }
        if (new Date().after(goods.getEndTime())) {
            throw new GmallException(ResultCodeEnum.SECKILL_END);
        }
        if (goods.getStockCount() <= 0) {
            throw new GmallException(ResultCodeEnum.SECKILL_FINISH);
        }

        return generateCode(skuId, AuthUtils.currentAuthInfo().getUserId(), DateUtil.formatDate(new Date()));
    }

    @Override
    public ResultCodeEnum seckillOrder(Long skuId, String skuIdStr) {
        //检查秒杀码
        if (!cacheService.checkSeckillCode(skuId, skuIdStr)) {
            return ResultCodeEnum.SECKILL_ILLEGAL;
        }
        SeckillGoods goods = cacheService.getSeckillGoodsInfo(skuId);
        if (goods == null) {
            return ResultCodeEnum.SECKILL_ILLEGAL;
        }
        if (new Date().before(goods.getStartTime())) {
            return ResultCodeEnum.SECKILL_NO_START;
        }
        if (new Date().after(goods.getEndTime())) {
            return ResultCodeEnum.SECKILL_END;
        }
        if (goods.getStockCount() <= 0) {
            return ResultCodeEnum.SECKILL_FINISH;
        }
        //如果大于2 说明用户之前已经抢到了
        Long requestCount = cacheService.increaseRequestCount(skuIdStr);
        if (requestCount > 2L) {
            return ResultCodeEnum.SUCCESS;
        }
        //开始秒杀
        Long stock = cacheService.decreaseSeckillGoodsStock(skuId);
        if (stock < 0L) {
            return ResultCodeEnum.SECKILL_FINISH;
        }
        //本地内存库存自减1
        goods.setStockCount(goods.getStockCount() - 1);
        //保存订单到redis中
        OrderInfo order = prepareOrderInfo(skuId);
        cacheService.cacheOrderInfo(order, skuIdStr);
        rabbitTemplate.convertAndSend(MqConst.EXCHANGE_SECKILL_EVENT,
                MqConst.RK_SECKILL_ORDERWAIT,
                Jsons.toStr(new SeckillTempOrderMsg(order.getUserId(), skuId, skuIdStr)));
        return ResultCodeEnum.SUCCESS;
    }

    @Override
    public ResultCodeEnum checkSeckillOrderStatus(Long skuId) {
        Long userId = AuthUtils.currentAuthInfo().getUserId();
        String code = BizUtil.generateSeckillCode(skuId, userId, DateUtil.formatDate(new Date()));
        //订单状态检查

        String json = redisTemplate.opsForValue().get(SysRedisConst.CACHE_SECKILL_ORDER + code);
        if (json == null) {
            return ResultCodeEnum.SECKILL_RUN;
        }
        if ("X".equals(json)) {
            return ResultCodeEnum.SECKILL_FINISH;
        }

        OrderInfo order = Jsons.toObj(json, OrderInfo.class);

        //1、是否已经下过单.
        if (order.getId() != null && order.getId() > 0) {
            List<String> statusList = Arrays.asList(ProcessStatus.PAID.name(), ProcessStatus.WAITING_DELEVER.name(), ProcessStatus.DELEVERED.name(), ProcessStatus.FINISHED.name(), ProcessStatus.CLOSED.name());
            if (statusList.contains(order.getProcessStatus())) {
                return ResultCodeEnum.SECKILL_ORDER_SUCCESS;
            }
            return ResultCodeEnum.SECKILL_SUCCESS;
        }

        //2、是否是抢单成功
        if (order.getOperateTime() != null) {
            return ResultCodeEnum.SECKILL_SUCCESS;
        }

        //只要是成功状态就会继续查询最终状态
        return ResultCodeEnum.SUCCESS;
    }

    @Override
    public SeckillOrderConfirmVo getSeckillOrderConfirmVo(Long skuId) {
        SeckillOrderConfirmVo confirmVo = null;

        Long userId = AuthUtils.currentAuthInfo().getUserId();
        String code = BizUtil.generateSeckillCode(skuId, userId, DateUtil.formatDate(new Date()));

        String json = redisTemplate.opsForValue().get(SysRedisConst.CACHE_SECKILL_ORDER + code);
        if (!StringUtils.isEmpty(json) && !"X".equals(json)) { //x
            OrderInfo info = Jsons.toObj(json, OrderInfo.class);
            confirmVo = new SeckillOrderConfirmVo();

            confirmVo.setTempOrder(info);
            confirmVo.setTotalNum(info.getOrderDetailList().size());
            confirmVo.setTotalAmount(info.getTotalAmount());
            //用户的收货地址
            Result<List<UserAddress>> addressList = userFeignClient.getUserAddress();
            confirmVo.setUserAddressList(addressList.getData());
        }

        return confirmVo;
    }

    private OrderInfo prepareOrderInfo(Long skuId) {
        SeckillGoods goods = cacheService.getSeckillGoodsInfo(skuId);
        Long userId = AuthUtils.currentAuthInfo().getUserId();

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setTotalAmount(goods.getCostPrice());
        orderInfo.setUserId(userId);
        orderInfo.setTradeBody(goods.getSkuName());
        orderInfo.setImgUrl(goods.getSkuDefaultImg());

        //订单详情：
        OrderDetail item = new OrderDetail();
        item.setSkuId(skuId);
        item.setUserId(userId);
        item.setSkuName(goods.getSkuName());
        item.setImgUrl(goods.getSkuDefaultImg());
        item.setOrderPrice(goods.getPrice());
        item.setSkuNum(1);
        item.setHasStock("1");
        item.setSplitTotalAmount(goods.getCostPrice());
        item.setSplitCouponAmount(goods.getPrice().subtract(goods.getCostPrice()));

        orderInfo.setOrderDetailList(Arrays.asList(item));

        return orderInfo;
    }

    private String generateCode(Long skuId, Long userId, String date) {
        String code = BizUtil.generateSeckillCode(skuId, userId, date);
        cacheService.cacheSeckillCode(code);
        return code;
    }
}
