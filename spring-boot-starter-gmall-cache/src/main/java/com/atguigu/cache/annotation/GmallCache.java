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
    /**
     * 缓存键名
     *
     * @return
     */
    String cacheKey() default "";

    /**
     * 缓存过期时间
     *
     * @return
     */
    long cacheTTL() default 60 * 30L;

    /**
     * 分布式锁名
     *
     * @return
     */
    String lockName() default "";

    /**
     * 分布式布隆过滤器名
     *
     * @return
     */
    String bloomName() default "";

    /**
     * 布隆需要查找的值
     *
     * @return
     */
    String bloomValue() default "";
}
