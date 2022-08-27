package com.atguigu.gmall.weball;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Connor
 * @date 2022/8/26
 */
@SpringCloudApplication
@EnableFeignClients
public class WebAllMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAllMainApplication.class, args);
    }
}
