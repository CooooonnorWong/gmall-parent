package com.atguigu.gmall.weball.service;

import org.springframework.ui.Model;

/**
 * @author Connor
 * @date 2022/8/26
 */
public interface SkuDetailService {
    /**
     * 获取SkuDetailTo,装配model
     *
     * @param skuId
     * @param model
     */
    void loadSkuDetailTo(Long skuId, Model model);
}
