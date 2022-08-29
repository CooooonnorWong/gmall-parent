package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.rpc.ProductFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.google.common.hash.BloomFilter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Connor
 * @date 2022/8/26
 */
@Service
public class SkuDetailServiceImpl implements SkuDetailService {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private ExecutorService executor;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private BloomFilter<Long> filter;
    private static final String PROTECTED_CACHE = "t";

    @Deprecated
    @Override
    public SkuDetailTo getSkuDetailTo(Long skuId) {
        Result<SkuDetailTo> result = productFeignClient.getSkuDetailTo(skuId);
        if (!result.isOk()) {
            throw new GmallException(ResultCodeEnum.SERVICE_ERROR);
        }
        return result.getData();
    }

    @PostConstruct
    public void init() {
        Result<List<Long>> skuIdList = productFeignClient.getSkuIdList();
        if (skuIdList.isOk() && skuIdList.getData() != null && skuIdList.getData().size() > 0) {
            skuIdList.getData().forEach(id -> filter.put(id));
        }
    }

    @Override
    public SkuDetailTo getSkuDetailToAsync(Long skuId) {
        if (!filter.mightContain(skuId)) {
            return null;
        }
        String skuStr = redisTemplate.opsForValue().get("sku:info:" + skuId);
        if (PROTECTED_CACHE.equals(skuStr)) {
            return null;
        }
        if (StringUtils.isEmpty(skuStr)) {
            SkuDetailTo detail = getDetailRpc(skuId);
            if (detail.getSkuInfo() != null) {
                filter.put(skuId);
                redisTemplate.opsForValue().set("sku:info:" + skuId, Jsons.toStr(detail), 1L, TimeUnit.DAYS);
            } else {
                redisTemplate.opsForValue().set("sku:info:" + skuId, PROTECTED_CACHE, 30, TimeUnit.MINUTES);
            }
            return detail;
        }
        filter.put(skuId);
        return Jsons.toObj(skuStr, SkuDetailTo.class);
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


}
