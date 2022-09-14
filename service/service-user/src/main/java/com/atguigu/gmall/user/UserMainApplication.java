package com.atguigu.gmall.user;

import com.atguigu.gmall.common.annotation.EnableAutoFeignInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * @author Connor
 * @date 2022/9/7
 */
@SpringCloudApplication
@EnableAutoFeignInterceptor
@MapperScan(basePackages = "com.atguigu.gmall.user.mapper")
public class UserMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserMainApplication.class, args);
    }
}
