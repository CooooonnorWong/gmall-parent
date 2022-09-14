package com.atguigu.gmall.feign.order;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Connor
 * @date 2022/9/13
 */
@FeignClient("service-order")
@RequestMapping("/api/inner/rpc/order")
public interface OrderFeignClient {
    /**
     * 获取订单确认页需要的数据
     *
     * @return
     */
    @GetMapping("/getOrderConfirmData")
    Result<OrderConfirmDataVo> getOrderConfirmData();

    /**
     * 根据orderId获取订单信息
     *
     * @param orderId
     * @return
     */
    @GetMapping("/getOrderInfo")
    Result<OrderInfo> getOrderInfo(@RequestParam("orderId") Long orderId);
}
