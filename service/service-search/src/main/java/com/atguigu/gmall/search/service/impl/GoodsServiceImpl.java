package com.atguigu.gmall.search.service.impl;

import com.atguigu.gmall.model.vo.search.*;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.search.service.GoodsService;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private SearchResponseVo buildResult(SearchHits<Goods> hits, SearchParamVo paramVo) {
        SearchResponseVo vo = new SearchResponseVo();
        //检索用的所有参数
        vo.setSearchParamVo(paramVo);
        //面包屑
        //品牌的面包屑
        if (!StringUtils.isEmpty(paramVo.getTrademark())) {
            vo.setTrademarkParam("品牌:" + paramVo.getTrademark().split(":")[1]);
        }
        //平台属性面包屑
        if (paramVo.getProps() != null && paramVo.getProps().length > 0) {
            vo.setPropsParamList(Arrays.stream(paramVo.getProps())
                    .map(prop -> {
                        String[] split = prop.split(":");
                        SearchAttr propsParam = new SearchAttr();
                        propsParam.setAttrId(Long.parseLong(split[0]));
                        propsParam.setAttrValue(split[1]);
                        propsParam.setAttrName(split[2]);
                        return propsParam;
                    }).collect(Collectors.toList()));
        }
        //获取Es命中的结果
        List<Goods> goods = hits.getSearchHits().stream().map(hit -> {
            Goods content = hit.getContent();
            //如果有关键字检索,设置标题高亮
            if (!StringUtils.isEmpty(paramVo.getKeyword())) {
                content.setTitle(hit.getHighlightField("title").get(0));
            }
            return content;
        }).collect(Collectors.toList());

        //品牌列表
        vo.setTrademarkList(buildTradeMarkList(hits));

        //平台属性列表
        vo.setAttrsList(buildAttrsList(hits));

        //检索到的商品集合
        vo.setGoodsList(goods);
        //排序
        if (!StringUtils.isEmpty(paramVo.getOrder())) {
            OrderMapVo orderMapVo = new OrderMapVo();
            String[] orderMap = paramVo.getOrder().split(":");
            orderMapVo.setType(orderMap[0]);
            orderMapVo.setSort(orderMap[1]);
            vo.setOrderMap(orderMapVo);
        }
        //页码
        vo.setPageNo(paramVo.getPageNo());
        //总页数
        long totalHits = hits.getTotalHits();
        vo.setTotalPages((int) (totalHits % paramVo.getPageSize() == 0 ? totalHits / paramVo.getPageSize() : (totalHits / paramVo.getPageSize() + 1)));
        //url参数整个连接
        vo.setUrlParam(buildUrl(paramVo));
        return vo;
    }

    /**
     * 从命中结果集中构建存在的平台属性列表
     *
     * @param hits
     * @return
     */
    private List<AttrVo> buildAttrsList(SearchHits<Goods> hits) {
        ParsedNested attrsAggs = hits.getAggregations().get("attrsAggs");
        ParsedLongTerms attrIdAggs = attrsAggs.getAggregations().get("attrIdAggs");
        return attrIdAggs.getBuckets().stream().map(bucket -> {
            long attrId = bucket.getKeyAsNumber().longValue();
            ParsedStringTerms attrNameAggs = bucket.getAggregations().get("attrNameAggs");
            String attrName = attrNameAggs.getBuckets().get(0).getKeyAsString();
            ParsedStringTerms attrValueAggs = bucket.getAggregations().get("attrValueAggs");
            List<String> attrValueList = attrValueAggs.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
            AttrVo attrVo = new AttrVo();
            attrVo.setAttrId(attrId);
            attrVo.setAttrName(attrName);
            attrVo.setAttrValueList(attrValueList);
            return attrVo;
        }).collect(Collectors.toList());
    }

    /**
     * 从命中结果集中构建存在的品牌列表
     *
     * @param hits
     * @return
     */
    private List<TrademarkVo> buildTradeMarkList(SearchHits<Goods> hits) {
        ParsedLongTerms tmIdAggs = hits.getAggregations().get("tmIdAggs");
        return tmIdAggs.getBuckets().stream().map(bucket -> {
            long tmId = bucket.getKeyAsNumber().longValue();
            ParsedStringTerms tmNameAggs = bucket.getAggregations().get("tmNameAggs");
            String tmName = tmNameAggs.getBuckets().get(0).getKeyAsString();
            ParsedStringTerms tmLogoUrlAggs = bucket.getAggregations().get("tmLogoUrlAggs");
            String tmLogoUrl = tmLogoUrlAggs.getBuckets().get(0).getKeyAsString();
            TrademarkVo tm = new TrademarkVo();
            tm.setTmId(tmId);
            tm.setTmName(tmName);
            tm.setTmLogoUrl(tmLogoUrl);
            return tm;
        }).collect(Collectors.toList());
    }

    private String buildUrl(SearchParamVo paramVo) {
        StringBuilder builder = new StringBuilder("list.html?");
        if (!StringUtils.isEmpty(paramVo.getKeyword())) {
            builder.append("&keyword=").append(paramVo.getKeyword());
        }
        if (paramVo.getCategory1Id() != null) {
            builder.append("category1Id=").append(paramVo.getCategory1Id());
        }
        if (paramVo.getCategory2Id() != null) {
            builder.append("category2Id=").append(paramVo.getCategory2Id());
        }
        if (paramVo.getCategory3Id() != null) {
            builder.append("category3Id=").append(paramVo.getCategory3Id());
        }
        if (!StringUtils.isEmpty(paramVo.getTrademark())) {
            builder.append("&trademark=").append(paramVo.getTrademark());
        }
        if (paramVo.getProps() != null && paramVo.getProps().length > 0) {
            Arrays.stream(paramVo.getProps()).forEach(prop -> builder.append("&prop=").append(prop));
        }
        return builder.toString();
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
                innerBool.must(QueryBuilders.termQuery("attrs.attrValue", split[1]));
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

        //聚合检索 - 检索结果集中的品牌和平台属性列表
        TermsAggregationBuilder tmIdAggs = AggregationBuilders.terms("tmIdAggs").field("tmId").size(1000);
        tmIdAggs.subAggregation(AggregationBuilders.terms("tmNameAggs").field("tmName").size(1));
        tmIdAggs.subAggregation(AggregationBuilders.terms("tmLogoUrlAggs").field("tmLogoUrl").size(1));
        query.addAggregation(tmIdAggs);

        NestedAggregationBuilder nested = AggregationBuilders.nested("attrsAggs", "attrs");

        TermsAggregationBuilder attrIdAggs = AggregationBuilders.terms("attrIdAggs").field("attrs.attrId").size(1000);
        TermsAggregationBuilder attrNameAggs = AggregationBuilders.terms("attrNameAggs").field("attrs.attrName").size(1);
        TermsAggregationBuilder attrValueAggs = AggregationBuilders.terms("attrValueAggs").field("attrs.attrValue").size(1000);

        attrIdAggs.subAggregation(attrNameAggs);
        attrIdAggs.subAggregation(attrValueAggs);
        nested.subAggregation(attrIdAggs);
        query.addAggregation(nested);
        return query;
    }
}
