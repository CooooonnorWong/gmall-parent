package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.vo.search.SearchAttr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Connor
 * @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Service
 * @createDate 2022-08-23 20:34:13
 */
public interface SkuAttrValueService extends IService<SkuAttrValue> {

    /**
     * 根据skuId获取searchAttr集合
     *
     * @param skuId
     * @return
     */
    List<SearchAttr> getSearchAttrList(Long skuId);
}
