package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.order.OrderInfo;

import java.util.List;

/**
 * @author Connor
 * @date 2022/9/20
 */
public interface CacheService {
    /**
     * 预缓存秒杀商品
     *
     * @param goods
     */
    void cacheSeckillGoods(List<SeckillGoods> goods);

    /**
     * 清空本地秒杀商品缓存
     */
    void clearLocalCache();

    /**
     * 从缓存中获取秒杀商品
     *
     * @return
     */
    List<SeckillGoods> getCachedSeckillGoods();

    /**
     * 将redis中的秒杀商品缓存同步到本地内存中
     */
    void syncSeckillGoodsCache();

    /**
     * 从redis中获取秒杀全部商品
     *
     * @return
     */
    List<SeckillGoods> getSeckillGoodsCacheFromRedis();

    /**
     * 获取秒杀商品详情
     *
     * @param skuId
     * @return
     */
    SeckillGoods getSeckillGoodsInfo(Long skuId);

    /**
     * 将生成的唯一秒杀码缓存到redis中
     *
     * @param code
     */
    void cacheSeckillCode(String code);

    /**
     * 检查秒杀码正确性
     *
     * @param skuId
     * @param seckillCode
     * @return
     */
    boolean checkSeckillCode(Long skuId, String seckillCode);

    /**
     * 用户抢单次数自增1
     *
     * @param code
     * @return
     */
    Long increaseRequestCount(String code);

    /**
     * 秒杀商品库存自减1
     *
     * @param skuId
     * @return
     */
    Long decreaseSeckillGoodsStock(Long skuId);

    /**
     * 将订单缓存到redis中
     *
     * @param order
     */
    void cacheOrderInfo(OrderInfo order, String code);

    /**
     * 库存扣减成功,修改redis中的的订单标志位
     *
     * @param key
     */
    void successToDeduceStock(String key);

    /**
     * 库存扣减失败,用户秒杀失败,删除临时订单
     *
     * @param key
     */
    void failToDeduceStock(String key);
}
