package com.atguigu.gmall.weball.controller;

import com.atguigu.gmall.feign.seckill.SeckillFeignClient;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.vo.seckill.SeckillOrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Connor
 * @date 2022/9/20
 */
@Controller
public class SeckillController {

    @Autowired
    private SeckillFeignClient seckillFeignClient;

    @GetMapping("/seckill.html")
    public String seckillPage(Model model) {
        List<SeckillGoods> goods = seckillFeignClient.getCurrentSeckillGoods().getData();
        model.addAttribute("list", goods);
        return "seckill/index";
    }

    @GetMapping("/seckill/{skuId}.html")
    public String seckillGoodsPage(@PathVariable("skuId") Long skuId, Model model) {
        SeckillGoods goods = seckillFeignClient.getSeckillGoodsInfo(skuId).getData();
        model.addAttribute("item", goods);
        return "seckill/item";
    }

    @GetMapping("/seckill/queue.html")
    public String seckillQueuePage(@RequestParam("skuId") Long skuId,
                                   @RequestParam("skuIdStr") String skuIdStr,
                                   Model model) {
        model.addAttribute("skuId", skuId);
        model.addAttribute("skuIdStr", skuIdStr);
        return "seckill/queue";
    }

    @GetMapping("/seckill/trade.html")
    public String tradePage(@RequestParam("skuId") Long skuId, Model model) {
        SeckillOrderConfirmVo vo = seckillFeignClient.getSeckillOrderConfirmVo(skuId).getData();
        model.addAttribute("detailArrayList",vo.getTempOrder().getOrderDetailList());
        model.addAttribute("userAddressList",vo.getUserAddressList());
        model.addAttribute("totalNum",vo.getTempOrder().getOrderDetailList().size());

        model.addAttribute("totalAmount",vo.getTempOrder().getTotalAmount());
        return "seckill/trade";
    }
}
