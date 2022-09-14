package com.atguigu.gmall.weball.service.impl;

import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderConfirmDataVo;
import com.atguigu.gmall.weball.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Date;

/**
 * @author Connor
 * @date 2022/9/13
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderFeignClient orderFeignClient;

    @Override
    public void loadTradePage(Model model) {
        OrderConfirmDataVo data = orderFeignClient.getOrderConfirmData().getData();
        model.addAttribute("detailArrayList", data.getDetailArrayList());
        model.addAttribute("userAddressList", data.getUserAddressList());
        model.addAttribute("totalNum", data.getTotalNum());
        model.addAttribute("totalAmount", data.getTotalAmount());
        model.addAttribute("tradeNo", data.getTradeNo());

    }

    @Override
    public String loadPayPage(Long orderId, Model model) {
        OrderInfo info = orderFeignClient.getOrderInfo(orderId).getData();
        Date current = new Date();
        if (current.after(info.getExpireTime())) {
            return "payment/error";
        }
        model.addAttribute("orderInfo", info);
        return "payment/pay";
    }
}
