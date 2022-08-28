package com.atguigu.gmall.product.service.impl;


import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.ValueSkuJsonTo;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Connor
 * @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service实现
 * @createDate 2022-08-23 20:34:13
 */
@Service
public class SpuSaleAttrServiceImpl extends ServiceImpl<SpuSaleAttrMapper, SpuSaleAttr>
        implements SpuSaleAttrService {

    @Autowired
    private SpuSaleAttrValueService spuSaleAttrValueService;
    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(Long spuId) {
//        List<SpuSaleAttr> spuSaleAttrList = this.list(new LambdaQueryWrapper<SpuSaleAttr>().eq(SpuSaleAttr::getSpuId, spuId));
//        spuSaleAttrList.forEach(spuSaleAttr -> {
//            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttrValueService
//                    .list(new LambdaQueryWrapper<SpuSaleAttrValue>()
//                            .eq(SpuSaleAttrValue::getSpuId, spuId)
//                            .eq(SpuSaleAttrValue::getBaseSaleAttrId, spuSaleAttr.getBaseSaleAttrId()));
//            spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValueList);
//        });
        return spuSaleAttrMapper.getSpuSaleAttrList(spuId);
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListAndMarkCheck(Long spuId, Long skuId) {
        return baseMapper.getSpuSaleAttrListAndMarkCheck(spuId, skuId);
    }

    @Override
    public String getValueSkuJson(Long spuId) {
        List<ValueSkuJsonTo> list = baseMapper.getValueSkuJson(spuId);
        Map<String, Long> map = list.stream().collect(Collectors.toMap(ValueSkuJsonTo::getValueSkuJson, ValueSkuJsonTo::getSkuId));
        return Jsons.toStr(map);
    }

}




