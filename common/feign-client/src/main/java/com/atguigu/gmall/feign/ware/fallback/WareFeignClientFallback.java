package com.atguigu.gmall.feign.ware.fallback;

import com.atguigu.gmall.feign.ware.WareFeignClient;
import org.springframework.stereotype.Component;

/**
 * @author Connor
 * @date 2022/9/17
 */

@Component
public class WareFeignClientFallback implements WareFeignClient {
    /**
     * ζε‘εεΊ
     *
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public String hasStock(Long skuId, Integer num) {
        return "1";
    }
}
