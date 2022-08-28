package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Connor
 * @description 针对表【base_category3(三级分类表)】的数据库操作Service
 * @createDate 2022-08-22 20:45:50
 */
public interface BaseCategory3Service extends IService<BaseCategory3> {

    /**
     * 三级分类列表
     *
     * @param category2Id
     * @return
     */
    List<BaseCategory3> getCategory3List(Long category2Id);

    /**
     * 获取CategoryViewTo
     *
     * @param category3Id
     * @return
     */
    CategoryViewTo getCategoryView(Long category3Id);
}
