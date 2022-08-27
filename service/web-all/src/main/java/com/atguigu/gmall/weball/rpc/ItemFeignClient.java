package com.atguigu.gmall.weball.rpc;

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
@FeignClient("service-item")
@RequestMapping("/api/inner/rpc/item")
public interface ItemFeignClient {
    /**
     * 远程调用service-item获取三级分类的树形结构集合
     *
     * @return
     */
    @GetMapping("/category/tree")
    Result<List<CategoryTreeTo>> getCategoryTree();

    /**
     * 远程调用service-item获取SkuDetailTo
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skudetail/{skuId}")
    Result<SkuDetailTo> getSkuDetailTo(@PathVariable Long skuId);
}
