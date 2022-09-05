package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.vo.search.SearchAttr;
import com.atguigu.gmall.product.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Connor
* @description 针对表【sku_attr_value(sku平台属性值关联表)】的数据库操作Service实现
* @createDate 2022-08-23 20:34:13
*/
@Service
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValue>
    implements SkuAttrValueService {

    @Override
    public List<SearchAttr> getSearchAttrList(Long skuId) {
        return baseMapper.getSearchAttrList(skuId);
    }
}




