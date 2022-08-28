package com.atguigu.gmall.common.config.threadpool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Connor
 * @date 2022/8/28
 */
@Data
@ConfigurationProperties(prefix = "gmall.thread-pool")
public class ThreadPoolProperties {
    private int corePoolSize = 2;
    private int maximumPoolSize = 8;
    private int queueCapacity = 200;
    private Long keepAliveTime = 200L;
}
