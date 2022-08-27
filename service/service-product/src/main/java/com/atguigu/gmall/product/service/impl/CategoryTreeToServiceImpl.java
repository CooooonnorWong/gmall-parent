package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.service.CategoryTreeToService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Connor
 * @date 2022/8/26
 */
@Service
public class CategoryTreeToServiceImpl implements CategoryTreeToService {
    @Resource
    private BaseCategory1Mapper mapper;

    @Override
    public List<CategoryTreeTo> getCategoryTree() {
        return mapper.getCategoryTree();
    }
}
