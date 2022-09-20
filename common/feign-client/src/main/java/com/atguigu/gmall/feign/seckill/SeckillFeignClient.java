package com.atguigu.gmall.feign.seckill;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.activity.SeckillGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Connor
 * @date 2022/9/20
 */
@FeignClient("service-seckill")
@RequestMapping("/api/inner/rpc/seckill")
public interface SeckillFeignClient {
    /**
     * 获取当天秒杀商品信息
     *
     * @return
     */
    @GetMapping("/today/seckillGoods")
    Result<List<SeckillGoods>> getCurrentSeckillGoods();

    /**
     * 查询商品详情
     *
     * @param skuId
     * @return
     */
    @GetMapping("/goodsInfo/{skuId}")
    Result<SeckillGoods> getSeckillGoodsInfo(@PathVariable("skuId") Long skuId);
}
