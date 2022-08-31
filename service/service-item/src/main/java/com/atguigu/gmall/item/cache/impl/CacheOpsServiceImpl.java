package com.atguigu.gmall.item.cache.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.cache.CacheOpsService;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author Connor
 * @date 2022/8/31
 */
@Service
public class CacheOpsServiceImpl implements CacheOpsService {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean tryLock(Long skuId) {
        RLock lock = redissonClient.getLock(SysRedisConst.LOCK_SKU_DETAIL + skuId);
        return lock.tryLock();
    }

    @Override
    public void unlock(Long skuId) {
        RLock lock = redissonClient.getLock(SysRedisConst.LOCK_SKU_DETAIL + skuId);
        lock.unlock();
    }

    @Override
    public <T> T getCacheData(String cacheKey, Class<T> clazz) {
        String jsonStr = redisTemplate.opsForValue().get(cacheKey);
        if (SysRedisConst.NULL_VAL.equals(jsonStr) || StringUtils.isEmpty(jsonStr)) {
            return null;
        }
        return Jsons.toObj(jsonStr, clazz);
    }

    @Override
    public void saveData(String cacheKey, SkuDetailTo skuDetailTo) {
        if (skuDetailTo == null || skuDetailTo.getSkuInfo() != null) {
            redisTemplate.opsForValue().set(cacheKey, Jsons.toStr(skuDetailTo), SysRedisConst.NULL_VAL_TTL, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(cacheKey, SysRedisConst.NULL_VAL, SysRedisConst.SKUDETAIL_TTL, TimeUnit.SECONDS);
        }
    }

    @Override
    public boolean isBloomContains(Long skuId) {
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);
        return filter.contains(skuId);
    }
}
