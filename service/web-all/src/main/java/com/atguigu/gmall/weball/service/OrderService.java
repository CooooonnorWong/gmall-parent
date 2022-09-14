package com.atguigu.gmall.weball.service;

import org.springframework.ui.Model;

/**
 * @author Connor
 * @date 2022/9/13
 */
public interface OrderService {
    /**
     * 加载结算确认订单页面
     *
     * @param model
     */
    void loadTradePage(Model model);

    /**
     * 加载支付页面
     *
     * @param orderId
     * @param model
     */
    String loadPayPage(Long orderId, Model model);
}
