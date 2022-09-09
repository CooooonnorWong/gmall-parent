package com.atguigu.gmall.weball.service;

import org.springframework.ui.Model;

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
     * @param model
     */
    boolean addToCart(Long skuId, Integer skuNum, Model model);

    /**
     * 删除选中的物品
     */
    void deleteChecked();
}
