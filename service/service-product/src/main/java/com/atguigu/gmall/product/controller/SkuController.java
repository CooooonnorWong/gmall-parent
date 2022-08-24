package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Connor
 * @date 2022/8/24
 */
@RestController
@Api(tags = "SKU")
@RequestMapping("/admin/product")
public class SkuController {
    @Autowired
    private SpuSaleAttrService spuSaleAttrService;
    @Autowired
    private SpuImageService spuImageService;
    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("/spuImageList/{spuId}")
    @ApiOperation("根据spuId获取图片列表")
    public Result<List<SpuImage>> getSpuImageList(@PathVariable Long spuId) {
        List<SpuImage> spuImageList = spuImageService.list(new LambdaQueryWrapper<SpuImage>().eq(SpuImage::getSpuId, spuId));
        return Result.ok(spuImageList);
    }

    @GetMapping("/spuSaleAttrList/{spuId}")
    @ApiOperation("根据spuId获取销售属性")
    public Result<List<SpuSaleAttr>> getSpuSaleAttrList(@PathVariable Long spuId) {
        List<SpuSaleAttr> list = spuSaleAttrService.getSpuSaleAttrList(spuId);
        return Result.ok(list);
    }

    @PostMapping("/saveSkuInfo")
    @ApiOperation("添加sku")
    public Result<Object> saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        skuInfoService.saveSkuInfo(skuInfo);
//        System.out.println(new Json(skuInfo.toString()).toString());
        return Result.ok();
    }

    @GetMapping("/list/{page}/{limit}")
    @ApiOperation("获取Sku分页列表")
    public Result<Page<SkuInfo>> getSkuPages(@PathVariable Integer page,
                                             @PathVariable Integer limit) {
        Page<SkuInfo> pages = new Page<>(page, limit);
        skuInfoService.page(pages);
        return Result.ok(pages);
    }

    @GetMapping("/onSale/{skuId}")
    @ApiOperation("上架")
    public Result<Object> onSale(@PathVariable Long skuId) {

        return Result.ok();
    }

    @GetMapping("/cancelSale/{skuId}")
    @ApiOperation("下架")
    public Result<Object> cancelSale(@PathVariable Long skuId) {

        return Result.ok();
    }
}
