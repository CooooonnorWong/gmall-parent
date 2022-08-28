package com.atguigu.gmall.common.config.annotation;

import com.atguigu.gmall.common.config.threadpool.ThreadPoolAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Connor
 * @date 2022/8/28
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ThreadPoolAutoConfiguration.class)
public @interface EnableThreadPool {
}
