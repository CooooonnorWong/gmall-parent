package com.atguigu.gmall.seckill.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.vo.seckill.SeckillOrderConfirmVo;
import com.atguigu.gmall.seckill.biz.SeckillBizService;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @Autowired
    private SeckillBizService seckillBizService;

    /**
     * 获取当天秒杀商品信息
     *
     * @return
     */
    @GetMapping("/today/seckillGoods")
    public Result<List<SeckillGoods>> getCurrentSeckillGoods() {
        List<SeckillGoods> goods = seckillGoodsService.getCurrentSeckillGoodsCache();
        return Result.ok(goods);
    }

    /**
     * 查询商品详情
     *
     * @param skuId
     * @return
     */
    @GetMapping("/goodsInfo/{skuId}")
    public Result<SeckillGoods> getSeckillGoodsInfo(@PathVariable("skuId") Long skuId) {
        SeckillGoods goods = seckillGoodsService.getGoodsInfo(skuId);
        return Result.ok(goods);
    }

    /**
     * 获取秒杀订单确认页详情
     *
     * @param skuId
     * @return
     */
    @GetMapping("/getSeckillOrderConfirmVo/{skuId}")
    public Result<SeckillOrderConfirmVo> getSeckillOrderConfirmVo(@PathVariable("skuId") Long skuId) {
        SeckillOrderConfirmVo vo = seckillBizService.getSeckillOrderConfirmVo(skuId);
        return Result.ok(vo);
    }
}
