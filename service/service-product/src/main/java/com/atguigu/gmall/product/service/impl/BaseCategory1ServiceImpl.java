package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Connor
* @description 针对表【base_category1(一级分类表)】的数据库操作Service实现
* @createDate 2022-08-22 20:45:50
*/
@Service
public class BaseCategory1ServiceImpl extends ServiceImpl<BaseCategory1Mapper, BaseCategory1>
    implements BaseCategory1Service{

    @Override
    public List<BaseCategory1> getBaseCategory1List() {
        return this.list();
    }
}




