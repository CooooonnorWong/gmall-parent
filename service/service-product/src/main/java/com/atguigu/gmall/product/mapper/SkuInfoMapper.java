package com.atguigu.gmall.product.mapper;


import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * @author Connor
 * @description 针对表【sku_info(库存单元表)】的数据库操作Mapper
 * @createDate 2022-08-23 20:34:13
 * @Entity com.atguigu.gmall.product.domain.SkuInfo
 */
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    /**
     * 更新商品售卖情况
     *
     * @param skuId
     * @param isSale
     */
    void updateSale(@Param("skuId") Long skuId, @Param("isSale") int isSale);

    /**
     * 获取实时价格
     *
     * @param skuId
     * @return
     */
    BigDecimal getRealTimePrice(@Param("skuId") Long skuId);
}




