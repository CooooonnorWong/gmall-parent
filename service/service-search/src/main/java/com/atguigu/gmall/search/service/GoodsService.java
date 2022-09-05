package com.atguigu.gmall.search.service;

import com.atguigu.gmall.model.vo.search.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;

/**
 * @author Connor
 * @date 2022/9/5
 */
public interface GoodsService {

    /**
     * 上架商品
     *
     * @param goods
     */
    void sale(Goods goods);

    /**
     * 下架商品
     *
     * @param goods
     */
    void notForSale(Goods goods);

    /**
     * es根据条件查询商品信息
     *
     * @param searchParamVo
     * @return
     */
    SearchResponseVo search(SearchParamVo searchParamVo);
}
