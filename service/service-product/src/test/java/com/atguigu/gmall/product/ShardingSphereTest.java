package com.atguigu.gmall.product;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Connor
 * @date 2022/9/3
 */
@SpringBootTest
public class ShardingSphereTest {
    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;

    @Test
    public void testSplittingRead() {
        List<Long> skuIdList = skuInfoMapper.getSkuIdList();
        System.out.println("valueSkuJson = " + skuIdList);
    }

    @Test
    public void testrw(){
        /**
         * 所有的负载均衡来到从库
         */
        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark);


        BaseTrademark baseTrademark1 = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark1);


        BaseTrademark baseTrademark2 = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark2);

        BaseTrademark baseTrademark3 = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark3);
    }

}
