package com.atguigu.gmall.weball.controller;

import com.atguigu.gmall.weball.service.SkuDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Connor
 * @date 2022/8/26
 */
@Controller
public class ItemController {
    @Autowired
    private SkuDetailService skuDetailService;

    @GetMapping("/{skuId}.html")
    public String item(@PathVariable Long skuId, Model model) {
        skuDetailService.loadSkuDetailTo(skuId, model);
        return "item/index";
    }
}
