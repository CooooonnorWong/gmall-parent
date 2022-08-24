package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Connor
 * @date 2022/8/23
 */
@RestController
@Api(tags = "SPU")
@RequestMapping("/admin/product")
public class BaseSaleController {
    @Autowired
    private SpuInfoService spuInfoService;
    @Autowired
    private BaseSaleAttrService baseSaleAttrService;
    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("/{page}/{limit}")
    @ApiOperation("获取spu分页列表")
    public Result<Page<SpuInfo>> getSpuPage(@PathVariable Integer page,
                                            @PathVariable Integer limit,
                                            @RequestParam Long category3Id) {
        Page<SpuInfo> pages = new Page<>(page, limit);
        spuInfoService.page(pages, new LambdaQueryWrapper<SpuInfo>().eq(SpuInfo::getCategory3Id, category3Id));
        return Result.ok(pages);
    }

    @GetMapping("/baseSaleAttrList")
    @ApiOperation("获取销售属性")
    public Result<List<BaseSaleAttr>> baseSaleAttrList() {
        List<BaseSaleAttr> list = baseSaleAttrService.list();
        return Result.ok(list);
    }

    @GetMapping("/list/{page}/{limit}")
    @ApiOperation("获取Sku分页列表")
    public Result<Page<SkuInfo>> getSkuPages(@PathVariable Integer page,
                                             @PathVariable Integer limit) {
        Page<SkuInfo> pages = new Page<>(page, limit);
        skuInfoService.page(pages);
        return Result.ok(pages);
    }
}
