package com.atguigu.gmall.common.config.bloomfilter;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author Connor
 * @date 2022/8/30
 */
@SpringBootConfiguration
@EnableConfigurationProperties(BloomFilterProperties.class)
public class BloomFilterAutoConfiguration {

    @Bean
    public BloomFilter<Long> bloomFilter(BloomFilterProperties properties) {
        return BloomFilter.create(Funnels.longFunnel(), properties.getExpectedInsertions(), properties.getFpp());
    }
}
