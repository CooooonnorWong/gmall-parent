package com.atguigu.gmall.rabbit.anotation;

import com.atguigu.gmall.rabbit.config.GmallRabbitAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Connor
 * @date 2022/9/15
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(GmallRabbitAutoConfiguration.class)
public @interface EnableGmallRabbit {
}
