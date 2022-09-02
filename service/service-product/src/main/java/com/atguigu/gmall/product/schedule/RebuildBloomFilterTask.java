package com.atguigu.gmall.product.schedule;

import com.atguigu.cache.constant.SysRedisConst;
import com.atguigu.gmall.product.init.SkuIdBloomFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author Connor
 * @date 2022/9/2
 */
@Service
public class RebuildBloomFilterTask {
    @Autowired
    private SkuIdBloomFilterService bloomFilterService;

    /**
     * * * * * * ? *
     * 秒 分 时 日 月 周 年
     */
    @Scheduled(cron = "0 0 3 ? * 3")
    public void rebuildSkuIdBloomFilter() {
        bloomFilterService.rebuildSkuIdBloomFilter(SysRedisConst.BLOOM_SKUID);
    }
}
