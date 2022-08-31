package com.atguigu.gmall.item.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.lock.RedisDistributeLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Connor
 * @date 2022/8/31
 */
@RestController
@RequestMapping("/lock")
public class LockTestController {
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 自定义的分布式锁
     */
    @Autowired
    private RedisDistributeLock redisDistributeLock;
    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("/customizeLockTest")
    public Result<Object> customizeLockTest() {
        String lock = redisDistributeLock.lock();
        redisTemplate.opsForValue().setIfAbsent("a", "0");
        String s = redisTemplate.opsForValue().get("a");
        int i = Integer.parseInt(s);
        redisTemplate.opsForValue().set("a", ++i + "");
        redisDistributeLock.unlock(lock);
        return Result.ok();
    }

    @GetMapping("/redissonReentrantLock")
    public Result<Object> redissonReentrantLock() {
        RLock lock = redissonClient.getLock("redisson:lock");
        lock.lock();
        redisTemplate.opsForValue().setIfAbsent("a", "0");
        String s = redisTemplate.opsForValue().get("a");
        int i = Integer.parseInt(s);
        redisTemplate.opsForValue().set("a", ++i + "");
        lock.unlock();
        return Result.ok();
    }
}
