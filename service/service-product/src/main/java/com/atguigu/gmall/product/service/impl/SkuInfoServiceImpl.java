package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Connor
 * @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
 * @createDate 2022-08-23 20:34:13
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
        implements SkuInfoService {

    @Autowired
    private SkuImageService skuImageService;
    @Autowired
    private SkuAttrValueService skuAttrValueService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private BaseCategory3Mapper baseCategory3Mapper;
    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;

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
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateSale(Long skuId, int isSale) {
        skuInfoMapper.updateSale(skuId, isSale);
        // TODO: 2022/8/25 从ElasticSearch中更新商品出售情况
    }

    @Override
    public SkuDetailTo getSkuDetailToBySkuId(Long skuId) {
        SkuDetailTo skuDetailTo = new SkuDetailTo();
        //1.获取skuInfo
        SkuInfo skuInfo = this.getById(skuId);
        List<SkuImage> skuImageList = skuImageService.list(new LambdaQueryWrapper<SkuImage>().eq(SkuImage::getSkuId, skuId));
        skuInfo.setSkuImageList(skuImageList);
        skuDetailTo.setSkuInfo(skuInfo);
        //2.获取categoryView
        CategoryViewTo categoryViewTo = baseCategory3Mapper.getCategoryView(skuInfo.getCategory3Id());
        skuDetailTo.setCategoryViewTo(categoryViewTo);
        //3.获取spuSaleAttrList
        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrMapper.getSpuSaleAttrListAndMarkCheck(skuInfo.getSpuId(), skuId);
        skuDetailTo.setSpuSaleAttrList(spuSaleAttrList);
        skuDetailTo.setPrice(skuInfo.getPrice());

        return skuDetailTo;
    }
}




