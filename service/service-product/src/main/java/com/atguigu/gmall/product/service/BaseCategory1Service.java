package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Connor
 * @description 针对表【base_category1(一级分类表)】的数据库操作Service
 * @createDate 2022-08-22 20:45:50
 */
public interface BaseCategory1Service extends IService<BaseCategory1> {

    /**
     * 获取一级分类列表
     *
     * @return
     */
    List<BaseCategory1> getBaseCategory1List();
}
