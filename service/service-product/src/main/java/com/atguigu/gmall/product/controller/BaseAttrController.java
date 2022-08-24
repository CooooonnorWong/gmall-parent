package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
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
@Api(tags = "平台属性")
@RequestMapping("/admin/product")
public class BaseAttrController {
    @Autowired
    private BaseAttrInfoService baseAttrInfoService;
    @Autowired
    private BaseAttrValueService baseAttrValueService;

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

}
