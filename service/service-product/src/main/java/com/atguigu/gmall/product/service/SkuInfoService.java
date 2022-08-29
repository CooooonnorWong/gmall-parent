package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Connor
 * @description 针对表【sku_info(库存单元表)】的数据库操作Service
 * @createDate 2022-08-23 20:34:13
 */
public interface SkuInfoService extends IService<SkuInfo> {

    /**
     * 添加sku
     *
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 更新在售情况
     *
     * @param skuId
     * @param isSale
     */
    void updateSale(Long skuId, int isSale);

    /**
     * 根据skuId获取SkuDetailTo
     *
     * @param skuId
     * @return
     */
    @Deprecated
    SkuDetailTo getSkuDetailToBySkuId(Long skuId);

    /**
     * 根据category3Id获取CategoryViewTo
     *
     * @param category3Id
     * @return
     */
    CategoryViewTo getCategoryViewTo(Long category3Id);

    /**
     * 根据id获取SkuInfo
     *
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfo(Long skuId);

    /**
     * 根据skuId获取skuImage集合
     *
     * @param skuId
     * @return
     */
    List<SkuImage> getSkuImageList(Long skuId);

    /**
     * 根据spuId,skuId获取平台销售属性集合
     *
     * @param spuId
     * @param skuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrList(Long spuId, Long skuId);

    /**
     * 根据spuId获取{'spuSaleAttrValueId1|spuSaleAttrValueId2':skuId,...}字符串
     *
     * @param spuId
     * @return
     */
    String getValueSkuJson(Long spuId);

    /**
     * 获取实时价格
     *
     * @param skuId
     * @return
     */
    BigDecimal getRealTimePrice(Long skuId);

    /**
     * 获取商品id集合
     *
     * @return
     */
    List<Long> getSkuIdList();
}
