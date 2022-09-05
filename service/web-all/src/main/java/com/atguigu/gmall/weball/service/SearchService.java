package com.atguigu.gmall.weball.service;

import com.atguigu.gmall.model.vo.search.SearchParamVo;
import org.springframework.ui.Model;

/**
 * @author Connor
 * @date 2022/9/6
 */
public interface SearchService {
    /**
     * 加载视图
     *
     * @param searchParamVo
     * @param model
     */
    void loadModel(SearchParamVo searchParamVo, Model model);
}
