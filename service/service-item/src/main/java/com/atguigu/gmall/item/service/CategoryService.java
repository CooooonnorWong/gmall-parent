package com.atguigu.gmall.item.service;

import com.atguigu.gmall.model.to.CategoryTreeTo;

import java.util.List;

/**
 * @author Connor
 * @date 2022/8/26
 */
public interface CategoryService {
    /**
     * 远程调用service-product获取三级分类的树形结构集合
     *
     * @return
     */
    List<CategoryTreeTo> getCategoryTree();
}
