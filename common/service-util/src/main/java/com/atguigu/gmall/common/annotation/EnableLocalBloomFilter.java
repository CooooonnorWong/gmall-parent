package com.atguigu.gmall.common.annotation;

import com.atguigu.gmall.common.config.bloomfilter.BloomFilterAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Connor
 * @date 2022/8/30
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(BloomFilterAutoConfiguration.class)
public @interface EnableLocalBloomFilter {
}
