package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.utils.AuthUtils;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.biz.OrderBizService;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    private OrderBizService orderBizService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/auth/submitOrder")
    public Result<String> submitOrder(@RequestParam("tradeNo") String tradeNo,
                                      @RequestBody OrderSubmitVo orderSubmitVo) {
//        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        Long orderId = orderBizService.submitOrder(orderSubmitVo, tradeNo);
        return Result.ok(orderId.toString());
    }

    @GetMapping("/auth/{page}/{limit}")
    public Result<Page<OrderInfo>> getPageOrderList(@PathVariable("page") Integer page,
                                                    @PathVariable("limit") Integer limit) {
        Page<OrderInfo> pages = new Page<>(page, limit);
        Long userId = AuthUtils.currentAuthInfo().getUserId();
        orderInfoService.page(pages, new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getUserId, userId));
        pages.getRecords().stream()
                .parallel()
                .forEach(orderInfo -> orderInfo.setOrderDetailList(orderDetailService.getOrderDetails(orderInfo.getId(), userId)));
        return Result.ok(pages);
    }

}
