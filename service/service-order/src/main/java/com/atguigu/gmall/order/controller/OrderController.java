package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.business.BusinessService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    @Autowired
    private OrderInfoService orderInfoService;

    @PostMapping("/auth/submitOrder")
    public Result<String> submitOrder(@RequestParam("tradeNo") String tradeNo,
                                      @RequestBody OrderSubmitVo orderSubmitVo) {
//        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        Long orderId = businessService.submitOrder(orderSubmitVo, tradeNo);
        return Result.ok(orderId.toString());
    }

    @GetMapping("/auth/{page}/{limit}")
    public Result<Page<OrderInfo>> getPageOrderList(@PathVariable("page") Integer page,
                                                    @PathVariable("limit") Integer limit) {
        Page<OrderInfo> pages = new Page<>(page, limit);
        // TODO: 2022/9/14 数据待完善
        orderInfoService.page(pages);
        return Result.ok(pages);
    }

}
