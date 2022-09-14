package com.atguigu.gmall.model.vo.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartInfoVo {
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 商品图片地址
     */
    private String imgUrl;
    /**
     * 商品名称
     */
    private String skuName;
    /**
     * 实时价格，最新价格
     */
    private BigDecimal orderPrice;
    /**
     * 商品数量
     */
    private Integer skuNum;
    /**
     * 是否有货
     */
    private String hasStock = "1";
}
