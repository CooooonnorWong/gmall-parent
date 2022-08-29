package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author Connor
 * @date 2022/8/29
 */
@SpringBootTest
public class RedisTemplateTest {
    @Autowired
    private StringRedisTemplate template;

    @Test
    public void test() {
        template.opsForValue().set("hello", "world!",60, TimeUnit.SECONDS);
        System.out.println("保存完成");
        System.out.println("成功获取: " + template.opsForValue().get("hello"));

    }

}
