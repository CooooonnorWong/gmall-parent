package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.model.activity.SeckillGoods;

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
}
