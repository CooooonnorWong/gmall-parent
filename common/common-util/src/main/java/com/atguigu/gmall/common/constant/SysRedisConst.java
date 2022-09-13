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
    String HEADER_USERID = "userId";
    String HEADER_USERTEMPID = "userTempId";

    /**
     * 用户id或临时id
     */
    public static final String CART_KEY = "cart:user:";
    /**
     * 购物车中商品条目总数限制
     */
    public static final long CART_ITEMS_LIMIT = 200;
    /**
     * 单个商品数量限制
     */
    public static final Integer CART_ITEM_NUM_LIMIT = 200;
    /**
     * 临时订单流水号
     */
    String ORDER_TEMP_TOKEN = "order:temptoken:";
}
