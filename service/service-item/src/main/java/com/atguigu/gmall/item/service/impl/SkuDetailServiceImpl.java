package com.atguigu.gmall.item.service.impl;


import com.atguigu.cache.annotation.GmallCache;
import com.atguigu.cache.constant.SysRedisConst;
import com.atguigu.cache.service.CacheOpsService;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.item.rpc.ProductFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author Connor
 * @date 2022/8/26
 */
@Service
@Slf4j
public class SkuDetailServiceImpl implements SkuDetailService {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private ExecutorService executor;
    @Autowired
    private CacheOpsService cacheOpsService;

    @GmallCache(cacheKey = SysRedisConst.SKU_INFO_PREFIX + "#{#params[0]}",
            bloomName = SysRedisConst.BLOOM_SKUID,
            bloomValue = "#{#params[0]}",
            lockName = SysRedisConst.LOCK_SKU_DETAIL + "#{#params[0]}")
    @Override
    public SkuDetailTo getSkuDetailToAsyncAspect(Long skuId) {
        return getDetailRpc(skuId);
    }

    private SkuDetailTo getDetailRpc(Long skuId) {
        SkuDetailTo detail = new SkuDetailTo();
        CompletableFuture<SkuInfo> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            Result<SkuInfo> result = productFeignClient.getSkuInfo(skuId);
            detail.setSkuInfo(result.getData());
            return result.getData();
        }, executor);
        CompletableFuture<Void> skuImageFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo != null) {
                Result<List<SkuImage>> result = productFeignClient.getSkuImageList(skuId);
                skuInfo.setSkuImageList(result.getData());
            }
        }, executor);
        CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> {
            Result<BigDecimal> result = productFeignClient.getRealTimePrice(skuId);
            detail.setPrice(result.getData());
        }, executor);
        CompletableFuture<Void> attrListFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo != null) {
                Result<List<SpuSaleAttr>> result = productFeignClient.getSpuSaleAttrList(skuInfo.getSpuId(), skuId);
                detail.setSpuSaleAttrList(result.getData());
            }
        }, executor);
        CompletableFuture<Void> skuJsonFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo != null) {
                Result<String> result = productFeignClient.getValueSkuJson(skuInfo.getSpuId());
                detail.setValueSkuJson(result.getData());
            }
        }, executor);
        CompletableFuture<Void> viewToFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            if (skuInfo != null) {
                Result<CategoryViewTo> result = productFeignClient.getCategoryViewTo(skuInfo.getCategory3Id());
                detail.setCategoryViewTo(result.getData());
            }
        }, executor);
        CompletableFuture.allOf(skuImageFuture, priceFuture, attrListFuture, skuJsonFuture, viewToFuture).join();
        return detail;
    }

    @Deprecated
    @Override
    public SkuDetailTo getSkuDetailTo(Long skuId) {
        Result<SkuDetailTo> result = productFeignClient.getSkuDetailTo(skuId);
        if (!result.isOk()) {
            throw new GmallException(ResultCodeEnum.SERVICE_ERROR);
        }
        return result.getData();
    }

    @Deprecated
    @Override
    public SkuDetailTo getSkuDetailToAsync(Long skuId) {
        String cacheKey = SysRedisConst.SKU_INFO_PREFIX + skuId;
        SkuDetailTo skuDetailTo = cacheOpsService.getCacheData(cacheKey, SkuDetailTo.class);
        if (skuDetailTo == null) {
            if (!cacheOpsService.isBloomContains(SysRedisConst.BLOOM_SKUID, skuId)) {
                log.info("[{}]商品 -布隆未命中 --检测到隐藏的攻击风险....", skuId);
                return null;
            }
            if (cacheOpsService.tryLock(SysRedisConst.LOCK_SKU_DETAIL + skuId)) {
                log.info("[{}]商品 -缓存未命中 -布隆命中 --准备回源.....", skuId);
                SkuDetailTo detail = getDetailRpc(skuId);
                cacheOpsService.saveData(cacheKey, detail);
                cacheOpsService.unlock(SysRedisConst.LOCK_SKU_DETAIL + skuId);
                return detail;
            }
            try {
                Thread.sleep(1000);
                return cacheOpsService.getCacheData(cacheKey, SkuDetailTo.class);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return skuDetailTo;
    }


}
