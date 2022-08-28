package com.atguigu.gmall.item.service.impl;

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
public class SkuDetailServiceImpl implements SkuDetailService {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private ExecutorService executor;

    @Deprecated
    @Override
    public SkuDetailTo getSkuDetailTo(Long skuId) {
        Result<SkuDetailTo> result = productFeignClient.getSkuDetailTo(skuId);
        if (!result.isOk()) {
            throw new GmallException(ResultCodeEnum.SERVICE_ERROR);
        }
        return result.getData();
    }

    @Override
    public SkuDetailTo getSkuDetailToAsync(Long skuId) {
        SkuDetailTo detail = new SkuDetailTo();
        CompletableFuture<SkuInfo> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            Result<SkuInfo> result = productFeignClient.getSkuInfo(skuId);
            detail.setSkuInfo(result.getData());
            return result.getData();
        }, executor);
        CompletableFuture<Void> skuImageFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<List<SkuImage>> result = productFeignClient.getSkuImageList(skuId);
            skuInfo.setSkuImageList(result.getData());
        }, executor);
        CompletableFuture<Void> priceFuture = CompletableFuture.runAsync(() -> {
            Result<BigDecimal> result = productFeignClient.getRealTimePrice(skuId);
            detail.setPrice(result.getData());
        }, executor);
        CompletableFuture<Void> attrListFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<List<SpuSaleAttr>> result = productFeignClient.getSpuSaleAttrList(skuInfo.getSpuId(), skuId);
            detail.setSpuSaleAttrList(result.getData());
        }, executor);
        CompletableFuture<Void> skuJsonFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<String> result = productFeignClient.getValueSkuJson(skuInfo.getSpuId());
            detail.setValueSkuJson(result.getData());
        }, executor);
        CompletableFuture<Void> viewToFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            Result<CategoryViewTo> result = productFeignClient.getCategoryViewTo(skuInfo.getCategory3Id());
            detail.setCategoryViewTo(result.getData());
        }, executor);
        CompletableFuture.allOf(skuImageFuture, priceFuture, attrListFuture, skuJsonFuture, viewToFuture).join();
        return detail;
    }


}
