package com.atguigu.gmall.search.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.search.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import com.atguigu.gmall.search.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Connor
 * @date 2022/9/5
 */
@RestController
@RequestMapping("/api/inner/rpc/search")
public class SearchApiController {

    @Autowired
    private GoodsService searchService;

    @PostMapping("/forSale")
    public Result<Object> forSale(@RequestBody Goods goods) {
        searchService.sale(goods);
        return Result.ok();
    }

    @PostMapping("/notForSale")
    public Result<Object> notForSale(@RequestBody Goods goods) {
        searchService.notForSale(goods);
        return Result.ok();
    }

    @PostMapping("/search")
    public Result<SearchResponseVo> search(@RequestBody SearchParamVo searchParamVo) {
        SearchResponseVo vo = searchService.search(searchParamVo);
        return Result.ok(vo);
    }

}
