package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.item.rpc.ProductFeignClient;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Connor
 * @date 2022/8/26
 */
@Service
public class SkuDetailServiceImpl implements SkuDetailService {
    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public SkuDetailTo getSkuDetailTo(Long skuId) {
        Result<SkuDetailTo> result = productFeignClient.getSkuDetailTo(skuId);
        if (!result.isOk()) {
            throw new GmallException(ResultCodeEnum.SERVICE_ERROR);
        }
        return result.getData();
    }
}
