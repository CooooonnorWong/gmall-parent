package com.atguigu.gmall.search;

import com.atguigu.gmall.search.bean.Person;
import com.atguigu.gmall.search.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.util.List;

/**
 * @author Connor
 * @date 2022/9/3
 */
@SpringBootTest
public class ElasticsearchTest {
    @Autowired
    private ElasticsearchRestTemplate restTemplate;
    @Autowired
    private PersonRepository personRepository;

    @Test
    public void test1() {
        restTemplate.save(new Person(1L, 22, "北京市朝阳区", "张", "三", "你好，我叫张三，喜欢跳，Rap，打篮球"));
        restTemplate.save(new Person(2L, 19, "北京市海淀区", "李", "四", "你好，我叫李四，喜欢唱，打篮球"));
        restTemplate.save(new Person(3L, 20, "上海市浦东新区", "王", "五", "你好，我叫王五，喜欢唱，跳，打篮球"));
        restTemplate.save(new Person(4L, 24, "北京市昌平区", "赵", "六", "你好，我叫赵六，喜欢唱，跳，Rap"));
        restTemplate.save(new Person(5L, 27, "深圳市福田区", "菜", "虚鲲", "你好，我叫菜虚鲲，喜欢跳，Rap"));
    }

    @Test
    public void test2(){
        List<Person> all = personRepository.findAllByAddress("北京市");
        System.out.println(all);
    }
}
