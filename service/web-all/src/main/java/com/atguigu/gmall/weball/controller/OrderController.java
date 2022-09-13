package com.atguigu.gmall.weball.controller;

import com.atguigu.gmall.weball.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Connor
 * @date 2022/9/13
 */
@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/trade.html")
    public String tradePage(Model model) {
        orderService.loadPage(model);
        return "order/trade";
    }
}
