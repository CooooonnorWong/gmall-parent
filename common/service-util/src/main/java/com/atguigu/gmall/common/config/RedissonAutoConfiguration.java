package com.atguigu.gmall.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author Connor
 * @date 2022/8/31
 */
@Component
@AutoConfigureAfter(RedisProperties.class)
public class RedissonAutoConfiguration {
    @Bean
    public RedissonClient redissonClient(RedisProperties properties) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setPassword(properties.getPassword());
        return Redisson.create(config);
    }
}
