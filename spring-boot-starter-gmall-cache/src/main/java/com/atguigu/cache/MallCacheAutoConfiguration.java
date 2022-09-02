package com.atguigu.cache;


import com.atguigu.cache.aspect.CacheAspect;
import com.atguigu.cache.service.CacheOpsService;
import com.atguigu.cache.service.impl.CacheOpsServiceImpl;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 以前容器中的所有组件要导入进去
 * 整个缓存场景涉及到的所有组件都得注入到容器中
 *
 * @author Connor
 */
@EnableAspectJAutoProxy
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Configuration
public class MallCacheAutoConfiguration {
    @Bean
    public CacheAspect cacheAspect() {
        return new CacheAspect();
    }

    @Bean
    public CacheOpsService cacheOpsService() {
        return new CacheOpsServiceImpl();
    }

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        //1、创建一个配置
        Config config = new Config();
        //2、指定好redisson的配置项
        config.useSingleServer()
                .setAddress("redis://" + redisProperties.getHost() + ":" + redisProperties.getPort())
                .setPassword(redisProperties.getPassword());

        //3、创建一个 RedissonClient
        //Redis url should start with redis:// or rediss:// (for SSL connection)

        return Redisson.create(config);
    }
}
