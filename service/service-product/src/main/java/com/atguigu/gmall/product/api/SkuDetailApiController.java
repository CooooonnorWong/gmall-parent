package com.atguigu.gmall.product.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.service.SkuInfoService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Connor
 * @date 2022/8/26
 */
@RestController
@Api(tags = "商品详情页API")
@RequestMapping("/api/inner/rpc/product")
public class SkuDetailApiController {
    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("/skudetail/{skuId}")
    public Result<SkuDetailTo> getSkuDetailTo(@PathVariable Long skuId) {
        SkuDetailTo to = skuInfoService.getSkuDetailToBySkuId(skuId);
        return Result.ok(to);
    }
}
