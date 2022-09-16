package com.atguigu.gmall.order.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.utils.AuthUtils;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import com.atguigu.gmall.order.business.BusinessService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Connor
 * @date 2022/9/13
 */
@RestController
@RequestMapping("/api/inner/rpc/order")
public class OrderApiController {
    @Autowired
    private BusinessService businessService;
    @Autowired
    private OrderInfoService orderInfoService;

    @GetMapping("/getOrderConfirmData")
    public Result<OrderConfirmDataVo> getOrderConfirmData() {
        OrderConfirmDataVo vo = businessService.getOrderConfirmData();
        return Result.ok(vo);
    }

    @GetMapping("/getOrderInfo")
    public Result<OrderInfo> getOrderInfo(@RequestParam("orderId") Long orderId) {
        OrderInfo orderInfo = orderInfoService.getOne(new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getId, orderId)
                .eq(OrderInfo::getUserId, AuthUtils.currentAuthInfo().getUserId()));
        return Result.ok(orderInfo);
    }

}
