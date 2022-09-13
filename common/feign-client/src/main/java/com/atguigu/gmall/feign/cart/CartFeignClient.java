package com.atguigu.gmall.feign.cart;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Connor
 * @date 2022/9/8
 */
@FeignClient("service-cart")
@RequestMapping("/api/inner/rpc/cart")
public interface CartFeignClient {

    /**
     * 将商品添加到购物车
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    @GetMapping("/addCart")
    Result<Object> addToCart(@RequestParam("skuId") Long skuId,
                             @RequestParam("skuNum") Integer skuNum);

    /**
     * 删除选中的商品
     *
     * @return
     */
    @GetMapping("/deleteChecked")
    Result<Object> deleteChecked();

    /**
     * 获取购物车中选中的商品
     *
     * @return
     */
    @GetMapping("/getChecked")
    Result<List<CartInfo>> getChecked();
}
