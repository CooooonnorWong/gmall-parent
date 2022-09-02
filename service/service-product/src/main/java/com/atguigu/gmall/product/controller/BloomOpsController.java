package com.atguigu.gmall.product.controller;

import com.atguigu.cache.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.init.SkuIdBloomFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Connor
 * @date 2022/9/2
 */
@RestController
@RequestMapping("/admin/bloom")
public class BloomOpsController {

    @Autowired
    private SkuIdBloomFilterService bloomFilterService;

    @GetMapping("/rebuild/skuIdBloomFilter")
    public Result<Object> rebuildSkuIdBloomFilter() {
        bloomFilterService.rebuildSkuIdBloomFilter(SysRedisConst.BLOOM_SKUID);
        return Result.ok();
    }
}
