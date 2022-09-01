package com.atguigu.gmall.item.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Connor
 * @date 2022/8/31
 */
@Component
public class CustomizeRedisDistributedLock {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public String lock() {
        String uuid = UUID.randomUUID().toString();
        while (!redisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS)) {
        }
        return uuid;
    }

    public void unlock(String uuid) {
        String luaScript = "if redis.call('get',KEYS[1]) == ARGV[1]  then return redis.call('del',KEYS[1]); else return 0;end;";
        redisTemplate.execute(new DefaultRedisScript<>(luaScript, Long.class), Arrays.asList("lock"), uuid);
    }
}
