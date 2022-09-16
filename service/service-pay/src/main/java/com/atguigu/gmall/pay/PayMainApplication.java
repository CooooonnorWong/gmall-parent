package com.atguigu.gmall.pay;

import com.atguigu.gmall.common.annotation.EnableAutoExceptionHandler;
import com.atguigu.gmall.common.annotation.EnableAutoFeignInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Connor
 * @date 2022/9/16
 */
@SpringCloudApplication
@EnableFeignClients({
        "com.atguigu.gmall.feign.order"
})
@EnableAutoFeignInterceptor
@EnableAutoExceptionHandler
public class PayMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayMainApplication.class, args);
    }
}
