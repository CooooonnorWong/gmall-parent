package com.atguigu.gmall.feign.search;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.search.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Connor
 * @date 2022/9/5
 */
@FeignClient("service-search")
@RequestMapping("/api/inner/rpc/search")
public interface SearchFeignClient {
    /**
     * 远程调用
     * 上架商品
     *
     * @param goods
     * @return
     */
    @PostMapping("/forSale")
    Result<Object> forSale(@RequestBody Goods goods);

    /**
     * 远程调用
     * 下架商品
     *
     * @param goods
     * @return
     */
    @PostMapping("/notForSale")
    Result<Object> notForSale(@RequestBody Goods goods);

    /**
     * 远程调用
     * 查询商品
     *
     * @param searchParamVo
     * @return
     */
    @PostMapping("/search")
    Result<SearchResponseVo> search(@RequestBody SearchParamVo searchParamVo);
}
