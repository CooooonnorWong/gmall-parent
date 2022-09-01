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
    @Deprecated
    SkuDetailTo getSkuDetailTo(Long skuId);

    /**
     * 异步获取商品详情页信息
     *
     * @param skuId
     * @return
     */
    @Deprecated
    SkuDetailTo getSkuDetailToAsync(Long skuId);

    /**
     * 切面环绕异步获取商品详情页信息
     *
     * @param skuId
     * @return
     */
    SkuDetailTo getSkuDetailToAsyncAspect(Long skuId);

}
