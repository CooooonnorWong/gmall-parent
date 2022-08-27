package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.to.CategoryTreeTo;

import java.util.List;

/**
 * @author Connor
 * @date 2022/8/26
 */
public interface CategoryTreeToService {
    /**
     * 获取三级分类的树形结构集合
     *
     * @return
     */
    List<CategoryTreeTo> getCategoryTree();
}
