package com.atguigu.gmall.cart.api;

import com.atguigu.gmall.cart.cart.CartService;
import com.atguigu.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Connor
 * @date 2022/9/8
 */
@RestController
@RequestMapping("/api/inner/rpc/cart")
public class CartApiController {
    @Autowired
    private CartService cartService;

    @GetMapping("/addCart")
    public Result<Object> addToCart(@RequestParam("skuId") Long skuId,
                                    @RequestParam("skuNum") Integer skuNum) {
        cartService.addCart(skuId, skuNum);
        return Result.ok();
    }

}
