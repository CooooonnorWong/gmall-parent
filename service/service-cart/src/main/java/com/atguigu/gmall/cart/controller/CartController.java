package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Connor
 * @date 2022/9/9
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/cartList")
    public Result<List<CartInfo>> cartList() {
        String cartKey = cartService.buildCartKey();
        cartService.tryMergeTempCart();
        List<CartInfo> list = cartService.getCartList(cartKey);
        return Result.ok(list);
    }

    @GetMapping("/checkCart/{skuId}/{isChecked}")
    public Result<Object> checkout(@PathVariable("skuId") Long skuId,
                                   @PathVariable("isChecked") Integer isChecked) {
        cartService.checkCart(skuId, isChecked, cartService.buildCartKey());
        return Result.ok();
    }

    @PostMapping("/addToCart/{skuId}/{increment}")
    public Result<Object> addToCart(@PathVariable("skuId") Long skuId,
                                    @PathVariable("increment") Integer increment) {
        cartService.increaseItemNum(skuId, increment, cartService.buildCartKey());
        return Result.ok();
    }

    @DeleteMapping("/deleteCart/{skuId}")
    public Result<Object> deleteCart(@PathVariable("skuId") Long skuId) {
        cartService.deleteCart(skuId, cartService.buildCartKey());
        return Result.ok();
    }

}
