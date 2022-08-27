package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.item.rpc.ProductFeignClient;
import com.atguigu.gmall.item.service.CategoryService;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Connor
 * @date 2022/8/26
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private ProductFeignClient productFeignClient;

    @Override
    public List<CategoryTreeTo> getCategoryTree() {
        Result<List<CategoryTreeTo>> result = productFeignClient.getCategoryTree();
        if (!result.isOk()) {
            throw new GmallException(ResultCodeEnum.SERVICE_ERROR);
        }
        return result.getData();
    }
}
