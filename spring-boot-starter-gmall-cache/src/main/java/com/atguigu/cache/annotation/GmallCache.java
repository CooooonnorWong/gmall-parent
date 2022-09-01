package com.atguigu.cache.annotation;

import java.lang.annotation.*;

/**
 * @author Connor
 * @date 2022/9/1
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface GmallCache {
    String cacheKey() default "";

    String lockName() default "";

    String bloomName() default "";

    String bloomValue() default "";
}
