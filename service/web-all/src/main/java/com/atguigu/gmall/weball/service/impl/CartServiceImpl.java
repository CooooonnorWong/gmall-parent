package com.atguigu.gmall.weball.service.impl;

import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.weball.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Connor
 * @date 2022/9/8
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartFeignClient cartFeignClient;

    @Override
    public void addToCart(Long skuId, Integer skuNum) {
        cartFeignClient.addToCart(skuId, skuNum);
    }
}
