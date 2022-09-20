package com.atguigu.gmall.seckill.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Connor
 * @date 2022/9/20
 */
@RestController
@RequestMapping("/api/inner/rpc/seckill")
public class SeckillApiController {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @GetMapping("/today/seckillGoods")
    public Result<List<SeckillGoods>> getCurrentSeckillGoods() {
        List<SeckillGoods> goods = seckillGoodsService.getCurrentSeckillGoodsCache();
        return Result.ok(goods);
    }
}
