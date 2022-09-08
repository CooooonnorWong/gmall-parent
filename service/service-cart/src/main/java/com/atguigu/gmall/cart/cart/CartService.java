package com.atguigu.gmall.cart.cart;

/**
 * @author Connor
 * @date 2022/9/8
 */
public interface CartService {
    /**
     * 添加到购物车
     *
     * @param skuId
     * @param skuNum
     */
    void addCart(Long skuId, Integer skuNum);
}
