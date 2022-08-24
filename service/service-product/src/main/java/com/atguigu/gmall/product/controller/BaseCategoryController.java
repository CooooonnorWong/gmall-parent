package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
