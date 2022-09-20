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
    String CART_KEY = "cart:user:";
    /**
     * 购物车中商品条目总数限制
     */
    long CART_ITEMS_LIMIT = 200;
    /**
     * 单个商品数量限制
     */
    Integer CART_ITEM_NUM_LIMIT = 200;
    /**
     * 临时订单流水号
     */
    String ORDER_TEMP_TOKEN = "order:temptoken:";
    /**
     * 未支付订单过期时间(s)
     */
    long ORDER_CLOSE_TTL = 60 * 45L;
    /**
     * 订单可退款时间期限(s)
     */
    long ORDER_REFUND_TTL = 60 * 60 * 24 * 7L;
    /**
     * 重试关闭订单
     */
    String MQ_RETRY_CLOSE_ORDER = "mq:retry:order:close:";
    /**
     * 重试订单支付
     */
    String MQ_RETRY_ORDER_PAID = "mq:retry:order:paid:";
    /**
     * 重试订单库存扣减
     */
    String MQ_RETRY_WARE_STOCK_DEDUCE = "mq:retry:ware:stock:deduce:";
    /**
     * 秒杀商品缓存键
     */
    String CACHE_SECKILL_GOODS = "seckill:goods:";
    /**
     * 秒杀商品库存缓存键
     */
    String CACHE_SECKILL_GOODS_STOCK = "seckill:goods:stock:";
}
