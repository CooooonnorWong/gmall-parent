package com.atguigu.gmall.weball.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.weball.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 * @author Connor
 * @date 2022/9/8
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartFeignClient cartFeignClient;

    @Override
    public boolean addToCart(Long skuId, Integer skuNum, Model model) {
        Result<Object> result = cartFeignClient.addToCart(skuId, skuNum);
        if (result.isOk()) {
            model.addAttribute("skuInfo", result.getData());
            model.addAttribute("skuNum", skuNum);
            return true;
        } else {
            model.addAttribute("msg", result.getData());
            return false;
        }
    }

    @Override
    public void deleteChecked() {
        cartFeignClient.deleteChecked();
    }
}
