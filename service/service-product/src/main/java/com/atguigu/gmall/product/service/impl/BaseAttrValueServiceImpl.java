package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Connor
 * @description 针对表【base_attr_value(属性值表)】的数据库操作Service实现
 * @createDate 2022-08-22 22:49:22
 */
@Service
public class BaseAttrValueServiceImpl extends ServiceImpl<BaseAttrValueMapper, BaseAttrValue>
        implements BaseAttrValueService {

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        return this.list(new LambdaQueryWrapper<BaseAttrValue>().eq(BaseAttrValue::getAttrId, attrId));
    }
}




