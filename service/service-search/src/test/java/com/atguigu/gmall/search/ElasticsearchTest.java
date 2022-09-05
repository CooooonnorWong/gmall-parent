package com.atguigu.gmall.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

/**
 * @author Connor
 * @date 2022/9/3
 */
@SpringBootTest
public class ElasticsearchTest {
    @Autowired
    private ElasticsearchRestTemplate restTemplate;
}
