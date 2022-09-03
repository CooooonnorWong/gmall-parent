package com.atguigu.cache.service;

import java.lang.reflect.Type;

/**
 * @author Connor
 * @date 2022/8/31
 */
public interface CacheOpsService {

    /**
     * 给指定商品加锁
     *
     * @param lockName
     * @return
     */
    boolean tryLock(String lockName);

    /**
     * 给指定商品解锁
     *
     * @param lockName
     */
    void unlock(String lockName);

    /**
     * 获取缓存商品数据
     *
     * @param cacheKey
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T getCacheData(String cacheKey, Class<T> clazz);

    /**
     * 获取缓存商品数据
     *
     * @param cacheKey
     * @param type
     * @return
     */
    Object getCacheData(String cacheKey, Type type);


    /**
     * 保存商品数据到缓存
     *
     * @param cacheKey
     * @param data
     */
    void saveData(String cacheKey, Object data,Long cacheTTL);

    /**
     * 布隆过滤器判断商品是否存在
     *
     * @param bloomName
     * @param bloomValue
     * @return
     */
    boolean isBloomContains(String bloomName, Object bloomValue);

}
