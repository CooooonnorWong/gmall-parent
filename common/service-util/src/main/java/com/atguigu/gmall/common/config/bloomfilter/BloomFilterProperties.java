package com.atguigu.gmall.common.config.bloomfilter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Connor
 * @date 2022/8/30
 */
@Data
@ConfigurationProperties(prefix = "gmall.bloom-filter")
public class BloomFilterProperties {
    private long expectedInsertions = 10000L;
    private double fpp = 0.0001;
}
