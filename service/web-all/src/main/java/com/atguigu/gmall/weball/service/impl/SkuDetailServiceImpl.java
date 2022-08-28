package com.atguigu.gmall.weball.service.impl;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.weball.rpc.ItemFeignClient;
import com.atguigu.gmall.weball.service.SkuDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 * @author Connor
 * @date 2022/8/26
 */
@Service
public class SkuDetailServiceImpl implements SkuDetailService {
    @Autowired
    private ItemFeignClient itemFeignClient;

    @Override
    public void loadSkuDetailTo(Long skuId, Model model) {
        Result<SkuDetailTo> result = itemFeignClient.getSkuDetailTo(skuId);
        if (!result.isOk()) {
            throw new GmallException(ResultCodeEnum.SERVICE_ERROR);
        }
        SkuDetailTo to = result.getData();
        model.addAttribute("categoryView", to.getCategoryViewTo());
        model.addAttribute("skuInfo", to.getSkuInfo());
        model.addAttribute("spuSaleAttrList", to.getSpuSaleAttrList());
        model.addAttribute("price", to.getPrice());
        model.addAttribute("valuesSkuJson", to.getValueSkuJson());
    }
}
