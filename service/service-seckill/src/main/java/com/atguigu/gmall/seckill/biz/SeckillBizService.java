package com.atguigu.gmall.seckill.biz;

import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.vo.seckill.SeckillOrderConfirmVo;

/**
 * @author Connor
 * @date 2022/9/21
 */
public interface SeckillBizService {
    /**
     * 生成当天当前用户当前商品唯一秒杀码
     *
     * @param skuId
     * @return
     */
    String generateSeckillCode(Long skuId);

    /**
     * 秒杀下单
     *
     * @param skuId
     * @param skuIdStr
     * @return
     */
    ResultCodeEnum seckillOrder(Long skuId, String skuIdStr);

    /**
     * 检查秒杀订单状态
     *
     * @param skuId
     * @return
     */
    ResultCodeEnum checkSeckillOrderStatus(Long skuId);

    /**
     * 获取秒杀订单确认页详情
     *
     * @param skuId
     * @return
     */
    SeckillOrderConfirmVo getSeckillOrderConfirmVo(Long skuId);
}
