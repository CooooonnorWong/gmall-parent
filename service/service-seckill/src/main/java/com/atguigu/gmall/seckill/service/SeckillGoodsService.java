package com.atguigu.gmall.seckill.service;


import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Connor
 * @description 针对表【seckill_goods】的数据库操作Service
 * @createDate 2022-08-25 22:04:36
 */
public interface SeckillGoodsService extends IService<SeckillGoods> {
    /**
     * 从数据库中获取当天参加秒杀的商品列表
     *
     * @return
     */
    List<SeckillGoods> getTodaySeckillGoodsList();

    /**
     * 从缓存中获取当天参加秒杀的商品列表
     *
     * @return
     */
    List<SeckillGoods> getCurrentSeckillGoodsCache();

    /**
     * 获取商品详情
     *
     * @param skuId
     * @return
     */
    SeckillGoods getGoodsInfo(Long skuId);

    /**
     * 减少秒杀商品数据库库存
     *
     * @param skuId
     */
    void deduceStock(Long skuId);
}
