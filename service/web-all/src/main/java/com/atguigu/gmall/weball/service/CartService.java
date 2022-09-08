package com.atguigu.gmall.weball.service;

/**
 * @author Connor
 * @date 2022/9/8
 */
public interface CartService {
    /**
     * 将商品添加到购物车
     *
     * @param skuId
     * @param skuNum
     */
    void addToCart(Long skuId, Integer skuNum);
}
