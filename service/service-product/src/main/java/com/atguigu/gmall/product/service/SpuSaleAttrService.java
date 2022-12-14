package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Connor
 * @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service
 * @createDate 2022-08-23 20:34:13
 */
public interface SpuSaleAttrService extends IService<SpuSaleAttr> {

    /**
     * 根据spuId获取销售属性
     *
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrList(Long spuId);

    /**
     * 获取valueSkuJson字符串
     *
     * @param spuId
     * @return
     */
    String getValueSkuJson(Long spuId);

    /**
     * 获取销售属性,标记选择版本
     *
     * @param spuId
     * @param skuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrListAndMarkCheck(Long spuId, Long skuId);
}
