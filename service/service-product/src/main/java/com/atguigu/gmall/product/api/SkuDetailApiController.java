package com.atguigu.gmall.product.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.service.SkuInfoService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

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

    @Deprecated
    @GetMapping("/skudetail/{skuId}")
    public Result<SkuDetailTo> getSkuDetailTo(@PathVariable Long skuId) {
        SkuDetailTo to = skuInfoService.getSkuDetailToBySkuId(skuId);
        return Result.ok(to);
    }

    @GetMapping("/skuInfo/{id}")
    public Result<SkuInfo> getSkuInfo(@PathVariable Long id) {
        SkuInfo skuInfo = skuInfoService.getSkuInfo(id);
        return Result.ok(skuInfo);
    }

    @GetMapping("/categoryViewTo/{category3Id}")
    public Result<CategoryViewTo> getCategoryViewTo(@PathVariable Long category3Id) {
        CategoryViewTo categoryViewTo = skuInfoService.getCategoryViewTo(category3Id);
        return Result.ok(categoryViewTo);
    }

    @GetMapping("/spuSaleAttrList/{spuId}/{skuId}")
    public Result<List<SpuSaleAttr>> getSpuSaleAttrList(@PathVariable Long spuId, @PathVariable Long skuId) {
        List<SpuSaleAttr> spuSaleAttrList = skuInfoService.getSpuSaleAttrList(spuId, skuId);
        return Result.ok(spuSaleAttrList);
    }

    @GetMapping("/valueSkuJson/{spuId}")
    public Result<String> getValueSkuJson(@PathVariable Long spuId) {
        String valueSkuJson = skuInfoService.getValueSkuJson(spuId);
        return Result.ok(valueSkuJson);
    }

    @GetMapping("/realTimePrice/{skuId}")
    public Result<BigDecimal> getRealTimePrice(@PathVariable Long skuId) {
        BigDecimal price = skuInfoService.getRealTimePrice(skuId);
        return Result.ok(price);
    }

    @GetMapping("/skuImageList/{skuId}")
    public Result<List<SkuImage>> getSkuImageList(@PathVariable Long skuId) {
        List<SkuImage> skuImageList = skuInfoService.getSkuImageList(skuId);
        return Result.ok(skuImageList);
    }

//    @GetMapping("/skuIdList")
//    public Result<List<Long>> getSkuIdList() {
//        List<Long> list = skuInfoService.getSkuIdList();
//        return Result.ok(list);
//    }
}
