package com.atguigu.gmall.weball.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Connor
 * @date 2022/9/7
 */
@Controller
public class LoginController {

    @GetMapping("/login.html")
    public String loginPage(@RequestParam String originUrl, Model model) {
        model.addAttribute("originUrl", originUrl);
        return "login";
    }
}
