package com.atguigu.gmall.order;

import com.atguigu.gmall.common.annotation.EnableAutoExceptionHandler;
import com.atguigu.gmall.common.annotation.EnableAutoFeignInterceptor;
import com.atguigu.gmall.common.annotation.EnableThreadPool;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Connor
 * @date 2022/9/13
 */
@SpringCloudApplication
@MapperScan("com.atguigu.gmall.order.mapper")
@EnableAutoFeignInterceptor
@EnableAutoExceptionHandler
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.feign.cart",
        "com.atguigu.gmall.feign.user",
        "com.atguigu.gmall.feign.ware"
})
@EnableThreadPool
public class OrderMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderMainApplication.class, args);
    }
}
