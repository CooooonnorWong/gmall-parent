package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.baomidou.mybatisplus.extension.service.IService;

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
    SkuDetailTo getSkuDetailToBySkuId(Long skuId);
}
