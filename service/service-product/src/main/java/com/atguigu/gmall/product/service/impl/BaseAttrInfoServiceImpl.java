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
import java.util.ArrayList;
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
        return baseAttrInfoMapper.attrInfoList(category1Id, category2Id, category3Id);
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        if (baseAttrInfo.getId() == null) {
            //id为null -> 新增
            this.save(baseAttrInfo);
            baseAttrInfo.getAttrValueList().forEach(baseAttrValue -> {
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(baseAttrValue);
            });
        } else {
            //id不为null -> 更新bai
            this.updateById(baseAttrInfo);
            //统计本次更新中的bavId
            List<BaseAttrValue> bavList = baseAttrInfo.getAttrValueList();
            List<Long> bavIdList = new ArrayList<>();
            bavList.forEach(value -> {
                if (value.getId() != null) {
                    bavIdList.add(value.getId());
                }
            });
            if (bavIdList.size() > 0) {
                //删除主键Id不在此List中的bav
                baseAttrValueMapper.delete(new LambdaQueryWrapper<BaseAttrValue>()
                        .eq(BaseAttrValue::getAttrId, baseAttrInfo.getId())
                        .notIn(BaseAttrValue::getId, bavIdList));
            } else {
                baseAttrValueMapper.delete(new LambdaQueryWrapper<BaseAttrValue>()
                        .eq(BaseAttrValue::getAttrId, baseAttrInfo.getId()));
            }
            //遍历bavList
            bavList.forEach(baseAttrValue -> {
                if (baseAttrValue.getId() == null) {
                    //bavId为空 -> 新增
                    baseAttrValue.setAttrId(baseAttrInfo.getId());
                    baseAttrValueMapper.insert(baseAttrValue);
                } else {
                    //bavId不为空 -> 修改
                    baseAttrValueMapper.updateById(baseAttrValue);
                }
            });

        }
    }
}




