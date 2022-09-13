package com.atguigu.gmall.weball.service.impl;

import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import com.atguigu.gmall.weball.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

/**
 * @author Connor
 * @date 2022/9/13
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderFeignClient orderFeignClient;

    @Override
    public void loadPage(Model model) {
        OrderConfirmDataVo data = orderFeignClient.getOrderConfirmData().getData();
        model.addAttribute("detailArrayList", data.getDetailArrayList());
        model.addAttribute("userAddressList", data.getUserAddressList());
        model.addAttribute("totalNum", data.getTotalNum());
        model.addAttribute("totalAmount", data.getTotalAmount());
        model.addAttribute("tradeNo", data.getTradeNo());

    }
}
