package com.atguigu.gmall.product.init;

import com.atguigu.cache.constant.SysRedisConst;
import com.atguigu.gmall.common.config.bloomfilter.BloomFilterProperties;
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
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);
        if (!filter.isExists()) {
            List<Long> idList = skuInfoService.getSkuIdList();
            filter.tryInit(properties.getExpectedInsertions(), properties.getFpp());
            idList.forEach(filter::add);
            log.info("布隆初始化完成....，总计添加了 {} 条数据", idList.size());
        } else {
            log.info("布隆已经初始化完成");
        }
    }

    public void rebuildSkuIdBloomFilter(String bloomName) {
        String newBloomName = bloomName + "_new";
        RBloomFilter<Object> oldFilter = redissonClient.getBloomFilter(bloomName);
        RBloomFilter<Object> newFilter = redissonClient.getBloomFilter(newBloomName);
        newFilter.tryInit(properties.getExpectedInsertions(), properties.getFpp());
        skuInfoService.getSkuIdList().forEach(newFilter::add);
        oldFilter.rename("deprecated_bloom");
        newFilter.rename(bloomName);
        oldFilter.deleteAsync();
        redissonClient.getBloomFilter("deprecated_bloom").deleteAsync();
    }
}
