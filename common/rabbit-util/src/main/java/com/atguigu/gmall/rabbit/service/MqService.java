package com.atguigu.gmall.rabbit.service;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Connor
 * @date 2022/9/19
 */
@Service
@Slf4j
public class MqService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void reConsumeMsg(Long maxRetry, String key, Long msgTag, Channel channel) throws IOException {
        Long retry = redisTemplate.opsForValue().increment(key);
        if (retry > maxRetry) {
            // TODO: 2022/9/16 消息消费失败,写入数据库
            //重试次数到达限制,不入队
            redisTemplate.delete(key);
            log.info("消息消费失败,消息:{},重试次数:{},写入数据库.", msgTag, retry);
            channel.basicNack(msgTag, false, false);
        } else {
            channel.basicNack(msgTag, false, true);
        }

    }
}
