package com.atguigu.gmall.model.to;

import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Connor
 * @date 2022/8/26
 */
@Data
public class SkuDetailTo {
    private CategoryViewTo categoryViewTo;
    private SkuInfo skuInfo;
    private List<SpuSaleAttr> spuSaleAttrList;
    private BigDecimal price;
    private String valueSkuJson;
}
