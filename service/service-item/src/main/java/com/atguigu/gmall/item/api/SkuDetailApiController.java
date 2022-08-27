package com.atguigu.gmall.item.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.to.SkuDetailTo;
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
@RequestMapping("/api/inner/rpc/item")
public class SkuDetailApiController {
    @Autowired
    private SkuDetailService skuDetailService;

    @GetMapping("/skudetail/{skuId}")
    public Result<SkuDetailTo> getSkuDetailTo(@PathVariable Long skuId) {
        SkuDetailTo skuDetailTo = skuDetailService.getSkuDetailTo(skuId);
        return Result.ok(skuDetailTo);
    }
}
