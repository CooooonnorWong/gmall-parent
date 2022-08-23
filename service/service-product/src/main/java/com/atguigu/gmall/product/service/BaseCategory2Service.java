package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory2;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Connor
 * @description 针对表【base_category2(二级分类表)】的数据库操作Service
 * @createDate 2022-08-22 20:45:50
 */
public interface BaseCategory2Service extends IService<BaseCategory2> {

    /**
     * 获取二级分类列表
     *
     * @param category1Id
     * @return
     */
    List<BaseCategory2> getCategory2List(Long category1Id);
}
