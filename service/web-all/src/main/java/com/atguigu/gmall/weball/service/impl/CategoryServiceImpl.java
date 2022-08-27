package com.atguigu.gmall.weball.service.impl;


import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.weball.rpc.ItemFeignClient;
import com.atguigu.gmall.weball.service.CategoryService;
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
    private ItemFeignClient itemFeignClient;

    @Override
    public List<CategoryTreeTo> getCategoryTree() {
        Result<List<CategoryTreeTo>> result = itemFeignClient.getCategoryTree();
        if (!result.isOk()) {
            throw new GmallException(ResultCodeEnum.SERVICE_ERROR);
        }
        return result.getData();
    }
}
