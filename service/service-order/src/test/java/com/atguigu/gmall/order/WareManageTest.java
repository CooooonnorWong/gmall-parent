package com.atguigu.gmall.order;

import com.atguigu.gmall.feign.ware.WareFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Connor
 * @date 2022/9/14
 */
@SpringBootTest
public class WareManageTest {
    @Autowired
    private WareFeignClient wareFeignClient;

    @Test
    public void testHasStock() {
        String s = wareFeignClient.hasStock(43L, 3);
        System.out.println("是否有库存: " + s);
    }
}
