package com.atguigu.gmall.product.init;

import com.atguigu.gmall.common.config.bloomfilter.BloomFilterProperties;
import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.product.service.SkuInfoService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author Connor
 * @date 2022/9/1
 */
@Service
@EnableConfigurationProperties(BloomFilterProperties.class)
@Slf4j
public class SkuIdBloomFilterService {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private BloomFilterProperties properties;

    @PostConstruct
    public void initSkuIdBloomFilter() {
        log.info("布隆初始化正在进行....");
        List<Long> idList = skuInfoService.getSkuIdList();
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);
        if (!filter.isExists()) {
            filter.tryInit(properties.getExpectedInsertions(), properties.getFpp());
        }
        idList.forEach(filter::add);
        log.info("布隆初始化完成....，总计添加了 {} 条数据", idList.size());
    }
}
