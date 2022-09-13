package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.business.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Connor
 * @date 2022/9/14
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private BusinessService businessService;

    @PostMapping("/auth/submitOrder")
    public Result<String> submitOrder(@RequestParam("tradeNo") String tradeNo,
                                      @RequestBody OrderSubmitVo orderSubmitVo) {
        Long orderId = businessService.submitOrder(orderSubmitVo, tradeNo);
        return Result.ok(orderId.toString());
    }

}
