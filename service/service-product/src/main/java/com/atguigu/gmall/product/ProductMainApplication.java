package com.atguigu.gmall.product;

import com.atguigu.gmall.common.config.MybatisPlusConfig;
import com.atguigu.gmall.common.config.Swagger2Config;
import com.atguigu.gmall.common.config.annotation.EnableMinioClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Connor
 * @date 2022/8/22
 */
@SpringCloudApplication
@EnableSwagger2
@EnableMinioClient
@MapperScan("com.atguigu.gmall.product.mapper")
@Import({Swagger2Config.class, MybatisPlusConfig.class})
public class ProductMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductMainApplication.class, args);
    }
}
