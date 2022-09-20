package com.atguigu.gmall.seckill.controller;

import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Connor
 * @date 2022/9/21
 */
@RestController
@RequestMapping("/api/activity/seckill/auth")
public class SeckillRestController {
    @GetMapping("/getSeckillSkuIdStr/{skuId}")
    public Result<Object> getSeckillSkuIdStr(@PathVariable("skuId") Long skuId) {

        return Result.ok();
    }
}
