package com.atguigu.gmall.seckill.service.impl;


import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.seckill.service.CacheService;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Connor
 * @description 针对表【seckill_goods】的数据库操作Service实现
 * @createDate 2022-08-25 22:04:36
 */
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods>
        implements SeckillGoodsService {

    @Autowired
    private CacheService cacheService;

    @Override
    public List<SeckillGoods> getTodaySeckillGoodsList() {
        return baseMapper.getCurrentSeckillGoodsCache(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public List<SeckillGoods> getCurrentSeckillGoodsCache() {
        return cacheService.getCachedSeckillGoods();
    }

    @Override
    public SeckillGoods getGoodsInfo(Long skuId) {
        return cacheService.getSeckillGoodsInfo(skuId);
    }
}




