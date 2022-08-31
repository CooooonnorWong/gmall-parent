package com.atguigu.gmall.common.annotation;

import com.atguigu.gmall.common.config.RedissonAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Connor
 * @date 2022/8/31
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RedissonAutoConfiguration.class)
public @interface EnableRedissonClient {
}
