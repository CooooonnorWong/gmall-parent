package com.atguigu.gmall.seckill.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.common.utils.AuthUtils;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.seckill.service.CacheService;
import com.atguigu.gmall.seckill.util.BizUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Connor
 * @date 2022/9/20
 */
@Service
public class CacheServiceImpl implements CacheService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private Map<Long, SeckillGoods> localCache = new ConcurrentHashMap<>();

    @Override
    public void cacheSeckillGoods(List<SeckillGoods> goods) {
        String date = DateUtil.formatDate(new Date());
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SysRedisConst.CACHE_SECKILL_GOODS + date);
        hashOps.expire(2L, TimeUnit.DAYS);
        goods.stream()
                .parallel()
                .forEach(item -> {
                    //缓存到redis
                    hashOps.put(item.getSkuId().toString(), Jsons.toStr(item));
                    //秒杀商品库存独立缓存到redis
                    String stockCacheKey = SysRedisConst.CACHE_SECKILL_GOODS_STOCK + item.getSkuId();
                    redisTemplate.opsForValue().setIfAbsent(stockCacheKey, item.getStockCount().toString(), 1, TimeUnit.DAYS);
                    //缓存到本地内存
                    localCache.put(item.getSkuId(), item);
                });
    }

    @Override
    public void clearLocalCache() {
        localCache.clear();
    }

    @Override
    public List<SeckillGoods> getCachedSeckillGoods() {
        //先从本地内存中获取
        List<SeckillGoods> list = localCache.values()
                .stream()
                .sorted(Comparator.comparing(SeckillGoods::getStartTime))
                .collect(Collectors.toList());
        if (list != null && list.size() > 0) {
            return list;
        }
        //如果本地内存中没有,将redis中的缓存同步到本地内存中
        this.syncSeckillGoodsCache();
        //再从本地内存中拿一遍
        return localCache.values()
                .stream()
                .sorted(Comparator.comparing(SeckillGoods::getStartTime))
                .collect(Collectors.toList());
    }

    @Override
    public void syncSeckillGoodsCache() {
        this.getSeckillGoodsCacheFromRedis()
                .stream()
                .forEach(goods -> localCache.put(goods.getSkuId(), goods));
    }

    @Override
    public List<SeckillGoods> getSeckillGoodsCacheFromRedis() {
        String cacheKey = SysRedisConst.CACHE_SECKILL_GOODS + DateUtil.formatDate(new Date());
        return redisTemplate.opsForHash().values(cacheKey)
                .stream()
                .map(json -> Jsons.toObj(json.toString(), SeckillGoods.class))
                .collect(Collectors.toList());
    }

    @Override
    public SeckillGoods getSeckillGoodsInfo(Long skuId) {
        SeckillGoods goods = localCache.get(skuId);
        if (goods == null) {
            this.syncSeckillGoodsCache();
            goods = localCache.get(skuId);
        }
        return goods;
    }

    @Override
    public void cacheSeckillCode(String code) {
        String key = SysRedisConst.CACHE_SECKILL_CODE + code;
        if (!redisTemplate.hasKey(key)) {
            redisTemplate.opsForValue().set(key, "1", 1L, TimeUnit.DAYS);
        }
    }

    @Override
    public boolean checkSeckillCode(Long skuId, String seckillCode) {
        String currentCode = BizUtil.generateSeckillCode(skuId, AuthUtils.currentAuthInfo().getUserId(), DateUtil.formatDate(new Date()));
        return currentCode.equals(seckillCode) && redisTemplate.hasKey(SysRedisConst.CACHE_SECKILL_CODE + seckillCode);
    }

    @Override
    public Long increaseRequestCount(String code) {
        return redisTemplate.opsForValue().increment(SysRedisConst.CACHE_SECKILL_CODE + code);
    }

    @Override
    public Long decreaseSeckillGoodsStock(Long skuId) {
        return redisTemplate.opsForValue().decrement(SysRedisConst.CACHE_SECKILL_GOODS_STOCK + skuId);
    }

    @Override
    public void cacheOrderInfo(OrderInfo order, String code) {
        redisTemplate.opsForValue().set(SysRedisConst.CACHE_SECKILL_ORDER + code, Jsons.toStr(order), 1L, TimeUnit.DAYS);
    }

    @Override
    public void successToDeduceStock(String key) {
        OrderInfo order = Jsons.toObj(redisTemplate.opsForValue().get(key), OrderInfo.class);
        order.setOperateTime(new Date());
        redisTemplate.opsForValue().set(key, Jsons.toStr(order), 1L, TimeUnit.DAYS);
    }

    @Override
    public void failToDeduceStock(String key) {
        redisTemplate.opsForValue().set(key, "X");
    }
}
