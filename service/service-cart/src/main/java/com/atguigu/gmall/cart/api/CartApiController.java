package com.atguigu.gmall.cart.api;

import com.atguigu.gmall.cart.cart.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public Result<SkuInfo> addToCart(@RequestParam("skuId") Long skuId,
                                     @RequestParam("skuNum") Integer skuNum) {
        SkuInfo skuInfo = cartService.addToCart(skuId, skuNum);
        return Result.ok(skuInfo);
    }

    @GetMapping("/deleteChecked")
    public Result<Object> deleteChecked() {
        cartService.deleteChecked(cartService.buildCartKey());
        return Result.ok();
    }

    @GetMapping("/getChecked")
    public Result<List<CartInfo>> getChecked() {
        String cartKey = cartService.buildCartKey();
        List<CartInfo> checked = cartService.getChecked(cartKey);
        return Result.ok(checked);
    }

}
