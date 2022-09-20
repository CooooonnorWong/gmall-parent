package com.atguigu.gmall.model.to.mq;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WareDeduceStatusMsg {

    private Long orderId;
    private String status;
}
