package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Connor
 * @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
 * @createDate 2022-08-22 22:49:22
 */
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
        implements BaseAttrInfoService {

    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Resource
    private BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id) {
        int level = 0;
        if (category1Id != 0) {
            if (category2Id == 0) {
                if (category3Id == 0) {
                    level = 3;
                } else {
                    return null;
                }
            } else {
                if (category3Id == 0) {
                    level = 2;
                } else {
                    level = 1;
                }
            }
        } else {
            return null;
        }
        List<BaseAttrInfo> baseAttrInfos =
                baseAttrInfoMapper
                        .selectList(
                                new LambdaQueryWrapper<BaseAttrInfo>()
                                        .eq(BaseAttrInfo::getCategoryLevel, level));
        baseAttrInfos.forEach(attr ->
                attr.setAttrValueList(baseAttrValueMapper
                        .selectList(
                                new LambdaQueryWrapper<BaseAttrValue>()
                                        .eq(BaseAttrValue::getAttrId, attr.getId())))
        );
        return baseAttrInfos;
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        if (baseAttrInfo.getId() == null) {
            this.save(baseAttrInfo);
            baseAttrInfo.getAttrValueList().forEach(baseAttrValue -> {
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(baseAttrValue);
            });
        } else {
            this.updateById(baseAttrInfo);
            baseAttrInfo.getAttrValueList().forEach(baseAttrValue -> {
                if (baseAttrValue.getId() == null) {
                    baseAttrValue.setAttrId(baseAttrInfo.getId());
                    baseAttrValueMapper.insert(baseAttrValue);
                } else {
                    baseAttrValueMapper.updateById(baseAttrValue);
                }
            });

        }
    }
}




