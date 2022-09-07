package com.atguigu.gmall.common.constant;

/**
 * @author Connor
 * @date 2022/8/31
 */
public interface SysRedisConst {
    String LOCK_SKU_DETAIL = "lock:sku:detail:";
    String NULL_VAL = "t";
    String SKU_INFO_PREFIX = "sku:info:";
    Long NULL_VAL_TTL = 60 * 30L;
    Long SKUDETAIL_TTL = 60 * 60 * 24 * 7L;
    String BLOOM_SKUID = "bloom:skuid";
    String LOCK_PREFIX = "lock:";
    String CACHE_CATEGORY_TREE = "category:tree";
    String LOGIN_USER_PREFIX = "login:user:";
    String HEADER_USERID = "userid";
}
