package com.atguigu.gmall.item.service;

import com.atguigu.gmall.model.to.SkuDetailTo;

/**
 * @author Connor
 * @date 2022/8/26
 */
public interface SkuDetailService {
    /**
     * 获取商品详情页信息
     *
     * @param skuId
     * @return
     */
    SkuDetailTo getSkuDetailTo(Long skuId);
}
