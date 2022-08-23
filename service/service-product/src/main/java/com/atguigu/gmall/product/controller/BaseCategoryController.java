package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Connor
 * @date 2022/8/22
 */
@RestController
@Api(tags = "产品分类")
@RequestMapping("/admin/product")
public class BaseCategoryController {

    @Autowired
    private BaseCategory1Service baseCategory1Service;
    @Autowired
    private BaseCategory2Service baseCategory2Service;
    @Autowired
    private BaseCategory3Service baseCategory3Service;
    @Autowired
    private BaseAttrInfoService baseAttrInfoService;
    @Autowired
    private BaseAttrValueService baseAttrValueService;
    @Autowired
    private SpuInfoService spuInfoService;
    @GetMapping("/getCategory1")
    @ApiOperation("一级产品分类列表")
    public Result<List<BaseCategory1>> getCategory1() {
        List<BaseCategory1> list = baseCategory1Service.getBaseCategory1List();
        return Result.ok(list);
    }

    @GetMapping("/getCategory2/{category1Id}")
    @ApiOperation("二级分类列表")
    public Result<List<BaseCategory2>> getCategory2(@PathVariable Long category1Id) {
        List<BaseCategory2> list = baseCategory2Service.getCategory2List(category1Id);
        return Result.ok(list);
    }

    @GetMapping("/getCategory3/{category2Id}")
    @ApiOperation("三级分类列表")
    public Result<List<BaseCategory3>> getCategory3(@PathVariable Long category2Id) {
        List<BaseCategory3> list = baseCategory3Service.getCategory3List(category2Id);
        return Result.ok(list);
    }

    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    @ApiOperation("根据分类id获取平台属性")
    public Result<List<BaseAttrInfo>> attrInfoList(@PathVariable Long category1Id,
                                                   @PathVariable Long category2Id,
                                                   @PathVariable Long category3Id) {
        List<BaseAttrInfo> list = baseAttrInfoService.attrInfoList(category1Id, category2Id, category3Id);
        return Result.ok(list);
    }

    @PostMapping("/saveAttrInfo")
    @ApiOperation("添加/修改平台属性")
    public Result<Object> saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        baseAttrInfoService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    @GetMapping("/getAttrValueList/{attrId}")
    @ApiOperation("根据平台属性ID获取平台属性对象数据")
    public Result<List<BaseAttrValue>> getAttrValueList(@PathVariable Long attrId) {
        List<BaseAttrValue> list = baseAttrValueService.getAttrValueList(attrId);
        return Result.ok(list);
    }

    @GetMapping("/{page}/{limit}")
    @ApiOperation("获取spu分页列表")
    public Result<Page<SpuInfo>> getSpuPage(@PathVariable Integer page,
                                            @PathVariable Integer limit,
                                            @RequestParam Long category3Id) {
        Page<SpuInfo> pages = spuInfoService.getSpuPage(page, limit, category3Id);
//        System.out.println("page: " + page + " limit: " + limit + " category3Id: " + category3Id);
        return Result.ok(pages);
    }
}
