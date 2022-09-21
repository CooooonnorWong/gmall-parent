package com.atguigu.gmall.seckill;

import com.atguigu.gmall.common.annotation.EnableAutoExceptionHandler;
import com.atguigu.gmall.common.annotation.EnableAutoFeignInterceptor;
import com.atguigu.gmall.common.annotation.EnableThreadPool;
import com.atguigu.gmall.rabbit.anotation.EnableGmallRabbit;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Connor
 * @date 2022/9/20
 */
@SpringCloudApplication
@MapperScan("com.atguigu.gmall.seckill.mapper")
@EnableAutoExceptionHandler
@EnableAutoFeignInterceptor
@EnableFeignClients({
        "com.atguigu.gmall.feign.user"
})
@EnableThreadPool
@EnableGmallRabbit
@EnableTransactionManagement
@EnableScheduling
public class SeckillMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeckillMainApplication.class, args);
    }
}
