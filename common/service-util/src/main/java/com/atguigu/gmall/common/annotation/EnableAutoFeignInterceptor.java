package com.atguigu.gmall.common.annotation;

import com.atguigu.gmall.common.config.FeignInterceptorConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Connor
 * @date 2022/9/13
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FeignInterceptorConfig.class)
public @interface EnableAutoFeignInterceptor {
}
