package com.atguigu.gmall.model.to.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 需要减库存的商品信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WareDeduceSkuInfo {

    Long skuId;
    Integer skuNum;
    String skuName;
}
