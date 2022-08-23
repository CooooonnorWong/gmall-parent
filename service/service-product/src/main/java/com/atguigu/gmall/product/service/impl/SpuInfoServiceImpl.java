package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Connor
 * @description 针对表【spu_info(商品表)】的数据库操作Service实现
 * @createDate 2022-08-23 01:15:19
 */
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
        implements SpuInfoService {

    @Resource
    private SpuInfoMapper spuInfoMapper;

    @Override
    public Page<SpuInfo> getSpuPage(Integer page, Integer limit, Long category3Id) {
//        return this.page(new Page<SpuInfo>(page, limit), new LambdaQueryWrapper<SpuInfo>().eq(SpuInfo::getCategory3Id, category3Id));
        return spuInfoMapper.selectPage(new Page<SpuInfo>(page, limit), new LambdaQueryWrapper<SpuInfo>().eq(SpuInfo::getCategory3Id, category3Id));
    }
}




