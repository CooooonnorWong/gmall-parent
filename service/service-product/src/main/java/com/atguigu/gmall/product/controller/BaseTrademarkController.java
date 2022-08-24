package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
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
@Api(tags = "品牌")
@RequestMapping("/admin/product/baseTrademark")
public class BaseTrademarkController {
    @Autowired
    private BaseTrademarkService baseTrademarkService;

    @GetMapping("/getTrademarkList")
    @ApiOperation("获取品牌属性")
    public Result<List<BaseTrademark>> getTrademarkList() {
        List<BaseTrademark> list = baseTrademarkService.list();
        return Result.ok(list);
    }

    @GetMapping("/{page}/{limit}")
    @ApiOperation("获取品牌分页列表")
    public Result<Page<BaseTrademark>> getPage(@PathVariable Integer page,
                                               @PathVariable Integer limit) {
        Page<BaseTrademark> pages = new Page<>(page, limit);
        baseTrademarkService.page(pages);
        return Result.ok(pages);
    }

    @PostMapping("/save")
    @ApiOperation("添加品牌")
    public Result<Object> save(@RequestBody BaseTrademark baseTrademark) {
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    @PutMapping("/update")
    @ApiOperation("修改品牌")
    public Result<Object> update(@RequestBody BaseTrademark baseTrademark) {
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    @DeleteMapping("/remove/{id}")
    @ApiOperation("删除品牌")
    public Result<Object> remove(@PathVariable Integer id) {
        baseTrademarkService.removeById(id);
        return Result.ok();
    }

    @GetMapping("/get/{id}")
    @ApiOperation("根据Id获取品牌")
    public Result<BaseTrademark> get(@PathVariable Integer id) {
        BaseTrademark baseTrademark = baseTrademarkService.getById(id);
        return Result.ok(baseTrademark);
    }
}
