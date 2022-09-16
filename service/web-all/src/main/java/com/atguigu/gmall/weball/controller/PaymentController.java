package com.atguigu.gmall.weball.controller;

import com.atguigu.gmall.weball.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Connor
 * @date 2022/9/14
 */
@Controller
public class PaymentController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/pay.html")
    public String payPage(@RequestParam("orderId") Long orderId,
                          Model model) {

        return orderService.loadPayPage(orderId, model);
    }

    @GetMapping("/pay/success.html")
    public String success() {

        return "payment/success";
    }
}
