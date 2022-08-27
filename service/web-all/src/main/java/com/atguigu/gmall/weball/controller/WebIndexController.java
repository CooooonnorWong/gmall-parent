package com.atguigu.gmall.weball.controller;

import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.atguigu.gmall.weball.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author Connor
 * @date 2022/8/26
 */
@Controller
public class WebIndexController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping({"/", "/index", "/index.html"})
    public String indexPage(Model model) {
        List<CategoryTreeTo> categoryTree = categoryService.getCategoryTree();
        model.addAttribute("list", categoryTree);
        return "index/index";
    }
}
