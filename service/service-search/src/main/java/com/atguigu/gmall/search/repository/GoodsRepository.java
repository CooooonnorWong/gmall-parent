package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.model.vo.search.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Connor
 * @date 2022/9/5
 */
@Repository
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {
}
