package com.atguigu.gmall.item.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
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

    /**
     * 根据skuId获取skuInfo
     *
     * @param id
     * @return
     */
    @GetMapping("/skuInfo/{id}")
    Result<SkuInfo> getSkuInfo(@PathVariable Long id);

    /**
     * 根据category3Id获取categoryViewTo
     *
     * @param category3Id
     * @return
     */
    @GetMapping("/categoryViewTo/{category3Id}")
    Result<CategoryViewTo> getCategoryViewTo(@PathVariable Long category3Id);

    /**
     * 根据spuId,skuId获取spuSaleAttr集合
     *
     * @param spuId
     * @param skuId
     * @return
     */
    @GetMapping("/spuSaleAttrList/{spuId}/{skuId}")
    Result<List<SpuSaleAttr>> getSpuSaleAttrList(@PathVariable Long spuId, @PathVariable Long skuId);

    /**
     * 根据spuId获取{'spuSaleAttrValueId1|spuSaleAttrValueId2':skuId,...}字符串
     *
     * @param spuId
     * @return
     */
    @GetMapping("/valueSkuJson/{spuId}")
    Result<String> getValueSkuJson(@PathVariable Long spuId);

    /**
     * 根据skuIid获取实时价格
     *
     * @param skuId
     * @return
     */
    @GetMapping("/realTimePrice/{skuId}")
    Result<BigDecimal> getRealTimePrice(@PathVariable Long skuId);

    /**
     * 根据skuId获取skuImage集合
     *
     * @param skuId
     * @return
     */
    @GetMapping("/skuImageList/{skuId}")
    Result<List<SkuImage>> getSkuImageList(@PathVariable Long skuId);

}
