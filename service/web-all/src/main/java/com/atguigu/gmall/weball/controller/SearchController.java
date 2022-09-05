package com.atguigu.gmall.weball.controller;

import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.weball.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Connor
 * @date 2022/9/6
 */
@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/list.html")
    public String searchIndex(SearchParamVo searchParamVo, Model model) {
        searchService.loadModel(searchParamVo, model);
        return "list/index";
    }
}
