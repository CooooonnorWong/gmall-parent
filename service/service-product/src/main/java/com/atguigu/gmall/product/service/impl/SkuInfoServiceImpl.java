package com.atguigu.gmall.product.service.impl;


import com.atguigu.cache.constant.SysRedisConst;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.model.vo.search.Goods;
import com.atguigu.gmall.model.vo.search.SearchAttr;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author Connor
 * @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
 * @createDate 2022-08-23 20:34:13
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
        implements SkuInfoService {
    @Autowired
    private ExecutorService executor;

    @Autowired
    private SkuImageService skuImageService;
    @Autowired
    private SkuAttrValueService skuAttrValueService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private SpuSaleAttrService spuSaleAttrService;
    @Autowired
    private BaseCategory3Service baseCategory3Service;
    @Autowired
    private BaseTrademarkService baseTrademarkService;
    @Autowired
    private SearchFeignClient searchFeignClient;
    @Autowired
    private RedissonClient redissonClient;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        this.save(skuInfo);
        if (skuImageList != null && skuImageList.size() > 0) {
            skuImageList.forEach(skuImage -> skuImage.setSkuId(skuInfo.getId()));
            skuImageService.saveBatch(skuImageList);
        }
        if (skuAttrValueList != null && skuAttrValueList.size() > 0) {
            skuAttrValueList.forEach(skuAttrValue -> skuAttrValue.setSkuId(skuInfo.getId()));
            skuAttrValueService.saveBatch(skuAttrValueList);
        }
        if (skuSaleAttrValueList != null && skuSaleAttrValueList.size() > 0) {
            skuSaleAttrValueList.forEach(skuSaleAttrValue -> {
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
            });
            skuSaleAttrValueService.saveBatch(skuSaleAttrValueList);
        }
        redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID).add(skuInfo.getId());

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateSale(Long skuId, int isSale) {
        baseMapper.updateSale(skuId, isSale);
        Goods goods = getGoodsBySkuId(skuId);
        //isSale:1 上架 / 0 下架
        if (isSale == 1) {
            //上架
            searchFeignClient.forSale(goods);
        } else {
            //下架
            searchFeignClient.notForSale(goods);
        }

    }

    private Goods getGoodsBySkuId(Long skuId) {
        Goods goods = new Goods();
        CompletableFuture<SkuInfo> future = CompletableFuture.supplyAsync(() -> this.getById(skuId), executor);
        CompletableFuture<Void> part1 = future.thenAcceptAsync(skuInfo -> {
            goods.setId(skuId);
            goods.setDefaultImg(skuInfo.getSkuDefaultImg());
            goods.setTitle(skuInfo.getSkuName());
            goods.setPrice(skuInfo.getPrice().doubleValue());
            goods.setCreateTime(new Date());
            goods.setTmId(skuInfo.getTmId());
        }, executor);
        CompletableFuture<Void> part2 = future.thenAcceptAsync(skuInfo -> {
            BaseTrademark trademark = baseTrademarkService.getById(skuInfo.getTmId());
            goods.setTmName(trademark.getTmName());
            goods.setTmLogoUrl(trademark.getLogoUrl());
        }, executor);
        CompletableFuture<Void> part3 = future.thenAcceptAsync(skuInfo -> {
            CategoryViewTo categoryView = baseCategory3Service.getCategoryView(skuInfo.getCategory3Id());
            goods.setCategory1Id(categoryView.getCategory1Id());
            goods.setCategory1Name(categoryView.getCategory1Name());
            goods.setCategory2Id(categoryView.getCategory2Id());
            goods.setCategory2Name(categoryView.getCategory2Name());
            goods.setCategory3Id(categoryView.getCategory3Id());
            goods.setCategory3Name(categoryView.getCategory3Name());
            goods.setHotScore(0L);
        }, executor);
        CompletableFuture<Void> part4 = CompletableFuture.runAsync(()->{
            List<SearchAttr> attrs = skuAttrValueService.getSearchAttrList(skuId);
            goods.setAttrs(attrs);
        }, executor);
        CompletableFuture.allOf(part1, part2, part3, part4).join();
        return goods;
    }

    @Deprecated
    @Override
    public SkuDetailTo getSkuDetailToBySkuId(Long skuId) {
        SkuDetailTo skuDetailTo = new SkuDetailTo();
        //1.获取skuInfo
        SkuInfo skuInfo = this.getById(skuId);
        List<SkuImage> skuImageList = skuImageService.list(new LambdaQueryWrapper<SkuImage>().eq(SkuImage::getSkuId, skuId));
        skuInfo.setSkuImageList(skuImageList);
        skuDetailTo.setSkuInfo(skuInfo);
        //2.获取categoryView
        CategoryViewTo categoryViewTo = baseCategory3Service.getCategoryView(skuInfo.getCategory3Id());
        skuDetailTo.setCategoryViewTo(categoryViewTo);
        //3.获取spuSaleAttrList
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrService.getSpuSaleAttrListAndMarkCheck(skuInfo.getSpuId(), skuId);
        skuDetailTo.setSpuSaleAttrList(spuSaleAttrList);
        skuDetailTo.setPrice(skuInfo.getPrice());
        //4.获取valueSkuJson
        skuDetailTo.setValueSkuJson(spuSaleAttrService.getValueSkuJson(skuInfo.getSpuId()));
        return skuDetailTo;
    }

    @Override
    public CategoryViewTo getCategoryViewTo(Long category3Id) {
        return baseCategory3Service.getCategoryView(category3Id);
    }

    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        return this.getById(skuId);
    }

    @Override
    public List<SkuImage> getSkuImageList(Long skuId) {
        return skuImageService.list(new LambdaQueryWrapper<SkuImage>().eq(SkuImage::getSkuId, skuId));
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId, Long skuId) {
        return spuSaleAttrService.getSpuSaleAttrListAndMarkCheck(spuId, skuId);
    }

    @Override
    public String getValueSkuJson(Long spuId) {
        return spuSaleAttrService.getValueSkuJson(spuId);
    }

    @Override
    public BigDecimal getRealTimePrice(Long skuId) {
        return baseMapper.getRealTimePrice(skuId);
    }

    @Override
    public List<Long> getSkuIdList() {
        return baseMapper.getSkuIdList();
    }


}




