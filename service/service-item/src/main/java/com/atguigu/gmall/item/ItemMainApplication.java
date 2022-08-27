package com.atguigu.gmall.item;

import com.atguigu.gmall.common.config.Swagger2Config;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Connor
 * @date 2022/8/26
 */
@SpringCloudApplication
@EnableFeignClients
@EnableSwagger2
@Import(Swagger2Config.class)
public class ItemMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemMainApplication.class, args);
    }
}
