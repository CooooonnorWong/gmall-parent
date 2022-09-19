package com.atguigu.gmall.pay.controller;

import com.alipay.api.AlipayApiException;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.pay.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * @author Connor
 * @date 2022/9/16
 */
@Controller
@RequestMapping("/api/payment")
@Slf4j
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    /**
     * 跳转到支付宝收银台
     *
     * @param orderId
     * @return
     * @throws AlipayApiException
     */
    @ResponseBody
    @GetMapping("/alipay/submit/{orderId}")
    public String alipayPage(@PathVariable("orderId") Long orderId) throws AlipayApiException {
        return paymentService.getAlipayHtmlPage(orderId);
    }

    /**
     * 支付完成后支付宝命令浏览器跳转
     *
     * @param params
     * @return
     * @throws AlipayApiException
     */
    @RequestMapping("/paysuccess")
    public String paySuccess(@RequestParam Map<String, String> params) throws AlipayApiException {
//        if (paymentService.rsaCheck(params)) {
//            //验签通过
//            log.info("验签通过,正在修改订单信息");
//        }

        return "redirect:http://www.gmall.com/pay/success.html";
    }

    /**
     * 支付宝服务器异步调用(需要内网穿透)
     *
     * @param params
     * @return
     * @throws AlipayApiException
     */
    @ResponseBody
    @RequestMapping("/success/notify")
    public String successNotify(@RequestParam Map<String, String> params) throws AlipayApiException {
        if (!paymentService.rsaCheck(params)) {
            return "error";
        }
        //验签通过
        log.info("异步通知抵达。支付成功，验签通过。数据：{}", Jsons.toStr(params));
        // TODO: 2022/9/17 修改订单信息
        log.info("验签通过,正在修改订单信息");
        return "success";
    }
}
