package com.atguigu.gmall.product.mapper;


import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.ValueSkuJsonTo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Connor
 * @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Mapper
 * @createDate 2022-08-23 20:34:13
 * @Entity com.atguigu.gmall.product.domain.SpuSaleAttr
 */
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    /**
     * 根据spuId获取销售属性
     *
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrList(@Param("spuId") Long spuId);

    /**
     * 获取销售属性,标记选择版本
     *
     * @param spuId
     * @param skuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrListAndMarkCheck(@Param("spuId") Long spuId, @Param("skuId") Long skuId);

    /**
     * 获取valueSkuJson字符串
     *
     * @param spuId
     * @return
     */
    List<ValueSkuJsonTo> getValueSkuJson(@Param("spuId") Long spuId);
}




