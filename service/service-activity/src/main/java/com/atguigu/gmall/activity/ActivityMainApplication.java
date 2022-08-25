package com.atguigu.gmall.activity;

import com.atguigu.gmall.common.config.Swagger2Config;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Connor
 * @date 2022/8/25
 */
@SpringCloudApplication
@EnableSwagger2
@MapperScan("com.atguigu.gmall.activity.mapper")
@Import(Swagger2Config.class)
public class ActivityMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ActivityMainApplication.class, args);
    }
}
