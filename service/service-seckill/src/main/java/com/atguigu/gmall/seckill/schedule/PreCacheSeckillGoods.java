package com.atguigu.gmall.seckill.schedule;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.service.CacheService;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Connor
 * @date 2022/9/20
 */
@Component
@Slf4j
public class PreCacheSeckillGoods {
    @Resource
    private SeckillGoodsService seckillGoodsService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private CacheService cacheService;

    @Scheduled(cron = "0 * * * * ?")
    public void preCacheSeckillGoods() {
        List<SeckillGoods> goods = seckillGoodsService.getTodaySeckillGoodsList();
        cacheService.cacheSeckillGoods(goods);
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void clearCache() {
        cacheService.clearLocalCache();
    }
}
