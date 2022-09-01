package com.atguigu.cache.service.impl;


import com.atguigu.cache.constant.SysRedisConst;
import com.atguigu.cache.service.CacheOpsService;
import com.atguigu.cache.util.Jsons;
import com.fasterxml.jackson.core.type.TypeReference;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
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
    public boolean tryLock(String lockName) {
        return redissonClient.getLock(lockName).tryLock();
    }

    @Override
    public void unlock(String lockName) {
        RLock lock = redissonClient.getLock(lockName);
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
    public Object getCacheData(String cacheKey, Type type) {
        String jsonStr = redisTemplate.opsForValue().get(cacheKey);
        if (SysRedisConst.NULL_VAL.equals(jsonStr) || StringUtils.isEmpty(jsonStr)) {
            return null;
        }
        return Jsons.toObj(jsonStr, new TypeReference<Object>() {
            @Override
            public Type getType() {
                return type;
            }
        });
    }

    @Override
    public void saveData(String cacheKey, Object data) {
        if (data != null) {
            redisTemplate.opsForValue().set(cacheKey, Jsons.toStr(data), SysRedisConst.SKUDETAIL_TTL, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(cacheKey, SysRedisConst.NULL_VAL, SysRedisConst.NULL_VAL_TTL, TimeUnit.SECONDS);
        }
    }


    @Override
    public boolean isBloomContains(String bloomName, Object bloomValue) {
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(bloomName);

        return filter.contains(bloomValue);
    }
}
