package com.atguigu.gmall.order.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import com.atguigu.gmall.order.business.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/getOrderConfirmData")
    public Result<OrderConfirmDataVo> getOrderConfirmData() {
        OrderConfirmDataVo vo = businessService.getOrderConfirmData();
        return Result.ok(vo);
    }

}
