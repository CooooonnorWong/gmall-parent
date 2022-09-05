package com.atguigu.gmall.search.service.impl;

import com.atguigu.gmall.model.vo.search.Goods;
import com.atguigu.gmall.model.vo.search.SearchParamVo;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.service.GoodsService;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @author Connor
 * @date 2022/9/5
 */
@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private ElasticsearchRestTemplate esRestTemplate;

    @Override
    public void sale(Goods goods) {
        goodsRepository.save(goods);
    }

    @Override
    public void notForSale(Goods goods) {
        goodsRepository.delete(goods);
    }

    @Override
    public SearchResponseVo search(SearchParamVo searchParamVo) {
        Query query = buildQueryDSL(searchParamVo);
        SearchHits<Goods> hits = esRestTemplate.search(query, Goods.class, IndexCoordinates.of("goods"));
        return buildResult(hits, searchParamVo);
    }

    private SearchResponseVo buildResult(SearchHits<Goods> hits, SearchParamVo searchParamVo) {

        return null;
    }

    private Query buildQueryDSL(SearchParamVo paramVo) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (paramVo.getKeyword() != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("title", paramVo.getKeyword()));
        }
        if (paramVo.getCategory1Id() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("category1Id", paramVo.getCategory1Id()));
        }
        if (paramVo.getCategory2Id() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("category2Id", paramVo.getCategory2Id()));
        }
        if (paramVo.getCategory3Id() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("category3Id", paramVo.getCategory3Id()));
        }
        if (paramVo.getTrademark() != null) {
            String[] split = paramVo.getTrademark().split(":");
            String tmId = split[0];
            boolQueryBuilder.must(QueryBuilders.termQuery("tmId", tmId));
        }
        if (paramVo.getProps() != null) {
            for (String prop : paramVo.getProps()) {
                String[] split = prop.split(":");
                BoolQueryBuilder innerBool = QueryBuilders.boolQuery();
                innerBool.must(QueryBuilders.termQuery("attrs.attrId", split[0]));
                innerBool.must(QueryBuilders.termQuery("attrs.attrName", split[1]));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", innerBool, ScoreMode.None);
                boolQueryBuilder.must(nestedQuery);
            }
        }
        NativeSearchQuery query = new NativeSearchQuery(boolQueryBuilder);
        String[] split = paramVo.getOrder().split(":");
        String field = "hotScore";
        switch (split[0]) {
            case "1":
                field = "hotScore";
                break;
            case "2":
                field = "price";
                break;
            case "3":
                field = "createTime";
                break;
            default:
                break;
        }
        query.addSort("asc".equals(split[1]) ? Sort.by(field).ascending() : Sort.by(field).descending());
        query.setPageable(PageRequest.of(paramVo.getPageNo() - 1, paramVo.getPageSize()));

        query.setHighlightQuery(
                new HighlightQuery(
                        new HighlightBuilder()
                                .field("title")
                                .preTags("<span style='color:red'>")
                                .postTags("</span>")));

        // TODO: 2022/9/6 聚合检索
        return query;
    }
}
