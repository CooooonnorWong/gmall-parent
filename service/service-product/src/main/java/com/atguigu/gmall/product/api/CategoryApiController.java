package com.atguigu.gmall.product.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.product.service.CategoryTreeToService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Connor
 * @date 2022/8/26
 */
@RestController
@Api(tags = "三级分类菜单API")
@RequestMapping("/api/inner/rpc/product")
public class CategoryApiController {
    @Autowired
    private CategoryTreeToService categoryTreeToService;

    @GetMapping("/category/tree")
    @ApiOperation("获取三级分类的树形结构集合")
    public Result<List<CategoryTreeTo>> getCategoryTree() {
        List<CategoryTreeTo> list = categoryTreeToService.getCategoryTree();
        return Result.ok(list);
    }
}
