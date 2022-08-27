package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author Connor
 * @description 针对表【base_category1(一级分类表)】的数据库操作Mapper
 * @createDate 2022-08-22 20:45:50
 * @Entity com.atguigu.gmall.product.domain.BaseCategory1
 */
public interface BaseCategory1Mapper extends BaseMapper<BaseCategory1> {

    /**
     * 获取三级分类的树形结构集合
     *
     * @return
     */
    List<CategoryTreeTo> getCategoryTree();
}




