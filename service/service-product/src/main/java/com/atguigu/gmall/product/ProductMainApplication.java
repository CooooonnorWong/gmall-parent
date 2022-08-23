package com.atguigu.gmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Connor
 * @date 2022/8/22
 */
@SpringCloudApplication
@EnableSwagger2
@MapperScan("com.atguigu.gmall.product.mapper")
public class ProductMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductMainApplication.class, args);
    }
}
