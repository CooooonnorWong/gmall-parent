package com.atguigu.gmall.feign.ware;

import com.atguigu.gmall.feign.ware.fallback.WareFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Connor
 * @date 2022/9/14
 */
@FeignClient(value = "ware-manage", url = "${gmall.ware.url:http://localhost:10001}", fallback = WareFeignClientFallback.class)
public interface WareFeignClient {
    /**
     * 查询该商品是否还有库存
     *
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/hasStock")
    String hasStock(@RequestParam("skuId") Long skuId,
                    @RequestParam("num") Integer num);
}
