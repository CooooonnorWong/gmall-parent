package com.atguigu.gmall.item.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Connor
 * @date 2022/8/26
 */
@FeignClient("service-product")
@RequestMapping("/api/inner/rpc/product")
public interface ProductFeignClient {
    /**
     * 远程调用service-product
     * 查询三级分类树形结构数据
     *
     * @return
     */
    @GetMapping("/category/tree")
    Result<List<CategoryTreeTo>> getCategoryTree();

    /**
     * 获取商品详情页信息
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/{skuId}")
    Result<SkuDetailTo> getSkuDetailTo(@PathVariable Long skuId);
}
