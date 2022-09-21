package com.atguigu.gmall.seckill.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.seckill.biz.SeckillBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Connor
 * @date 2022/9/21
 */
@RestController
@RequestMapping("/api/activity/seckill/auth")
public class SeckillRestController {
    @Autowired
    private SeckillBizService seckillBizService;

    @GetMapping("/getSeckillSkuIdStr/{skuId}")
    public Result<String> getSeckillSkuIdStr(@PathVariable("skuId") Long skuId) {
        String seckillCode = seckillBizService.generateSeckillCode(skuId);
        return Result.ok(seckillCode);
    }

    @PostMapping("/seckillOrder/{skuId}")
    public Result<Object> seckillOrder(@PathVariable("skuId") Long skuId,
                                       @RequestParam("skuIdStr") String skuIdStr) {
        ResultCodeEnum codeEnum = seckillBizService.seckillOrder(skuId, skuIdStr);
        return Result.build("", codeEnum);
    }

    @GetMapping("/checkOrder/{skuId}")
    public Result<Object> checkOrder(@PathVariable("skuId") Long skuId) {
        ResultCodeEnum codeEnum = seckillBizService.checkSeckillOrderStatus(skuId);
        return Result.build("", codeEnum);
    }
}
