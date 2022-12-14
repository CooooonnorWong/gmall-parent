package com.atguigu.gmall.product;

import com.atguigu.gmall.common.annotation.EnableAutoFeignInterceptor;
import com.atguigu.gmall.common.annotation.EnableMinioClient;
import com.atguigu.gmall.common.annotation.EnableThreadPool;
import com.atguigu.gmall.common.config.MybatisPlusConfig;
import com.atguigu.gmall.common.config.Swagger2Config;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Connor
 * @date 2022/8/22
 */
@SpringCloudApplication
@EnableSwagger2
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.feign.search"
})
@EnableMinioClient
@EnableThreadPool
@EnableAutoFeignInterceptor
@MapperScan("com.atguigu.gmall.product.mapper")
@Import({Swagger2Config.class, MybatisPlusConfig.class})
public class ProductMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductMainApplication.class, args);
    }
}
