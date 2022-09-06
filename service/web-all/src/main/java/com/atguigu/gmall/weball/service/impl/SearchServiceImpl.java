package com.atguigu.gmall.weball.service.impl;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import com.atguigu.gmall.weball.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 * @author Connor
 * @date 2022/9/6
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SearchFeignClient searchFeignClient;

    @Override
    public void loadModel(SearchParamVo searchParamVo, Model model) {
        Result<SearchResponseVo> data = searchFeignClient.search(searchParamVo);
        if (!data.isOk()) {
            throw new GmallException(ResultCodeEnum.SERVICE_ERROR);
        }
        SearchResponseVo vo = data.getData();
        model.addAttribute("searchParamVo", vo.getSearchParamVo());
        model.addAttribute("trademarkParam", vo.getTrademarkParam());
        model.addAttribute("urlParam", vo.getUrlParam());
        model.addAttribute("propsParamList", vo.getPropsParamList());
        model.addAttribute("trademarkList", vo.getTrademarkList());
        model.addAttribute("attrsList", vo.getAttrsList());
        model.addAttribute("orderMap", vo.getOrderMap());
        model.addAttribute("goodsList", vo.getGoodsList());
        model.addAttribute("pageNo", vo.getPageNo());
        model.addAttribute("totalPages", vo.getTotalPages());
    }
}
