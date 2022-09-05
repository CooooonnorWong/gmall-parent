package com.atguigu.gmall.weball.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
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
        Result<SearchParamVo> data = searchFeignClient.search(searchParamVo);
    }
}
