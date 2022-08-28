package com.atguigu.gmall.common.config.threadpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @author Connor
 * @date 2022/8/28
 */
@Component
@EnableConfigurationProperties(ThreadPoolProperties.class)
public class ThreadPoolAutoConfiguration {
    @Autowired
    private ThreadPoolProperties poolProperties;
    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public ExecutorService threadPool() {
        return new ThreadPoolExecutor(poolProperties.getCorePoolSize(),
                poolProperties.getMaximumPoolSize(),
                poolProperties.getKeepAliveTime(),
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(poolProperties.getQueueCapacity()),
                new ThreadFactory() {
                    private int serialNum;

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("[" + applicationName + "]-[core-thread]-[" + ++serialNum + "]");
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
