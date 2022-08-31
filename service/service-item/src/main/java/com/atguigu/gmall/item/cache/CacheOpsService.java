package com.atguigu.gmall.item.cache;

import com.atguigu.gmall.model.to.SkuDetailTo;

/**
 * @author Connor
 * @date 2022/8/31
 */
public interface CacheOpsService {
    /**
     * 给指定商品加锁
     *
     * @param skuId
     * @return
     */
    boolean tryLock(Long skuId);

    /**
     * 给指定商品解锁
     *
     * @param skuId
     */
    void unlock(Long skuId);

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
     * 保存商品数据到缓存
     *
     * @param cacheKey
     * @param skuDetailTo
     */
    void saveData(String cacheKey, SkuDetailTo skuDetailTo);

    /**
     * 布隆过滤器判断商品是否存在
     *
     * @param skuId
     * @return
     */
    boolean isBloomContains(Long skuId);
}
