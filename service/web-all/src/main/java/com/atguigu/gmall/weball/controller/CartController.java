package com.atguigu.gmall.weball.controller;

import com.atguigu.gmall.weball.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Connor
 * @date 2022/9/8
 */
@Controller
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/cart.html")
    public String cart() {
        return "cart/index";
    }

    @GetMapping("/addCart.html")
    public String addCart(@RequestParam("skuId") Long skuId,
                          @RequestParam("skuNum") Integer skuNum) {
        cartService.addToCart(skuId, skuNum);
        return "cart/addCart";
    }
}
