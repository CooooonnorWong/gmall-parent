package com.atguigu.gmall.model.vo.search;

import lombok.Data;

@Data
public class OrderMapVo {
    //排序类型， 1是综合，2是价格
    private String type;
    //排序规则
    private String sort;

}
