package com.atguigu.gmall.seckill.util;

import com.atguigu.gmall.common.util.MD5;

/**
 * @author Connor
 * @date 2022/9/21
 */
public final class BizUtil {
    public static String generateSeckillCode(Long skuId, Long userId, String date) {
        return MD5.encrypt(skuId + "_" + userId + "_" + date);
    }
}
