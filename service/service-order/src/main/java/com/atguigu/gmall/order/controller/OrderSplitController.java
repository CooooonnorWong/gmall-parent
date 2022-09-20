package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.model.vo.order.OrderWareMapVo;
import com.atguigu.gmall.model.vo.order.WareChildOrderVo;
import com.atguigu.gmall.order.biz.OrderBizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Connor
 * @date 2022/9/20
 */
@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderSplitController {
    @Autowired
    private OrderBizService orderBizService;

    @PostMapping("/orderSplit")
    public List<WareChildOrderVo> orderSplit(OrderWareMapVo vo) {
        log.info("开始拆分订单");
        return orderBizService.splitOrder(vo);
    }
}
