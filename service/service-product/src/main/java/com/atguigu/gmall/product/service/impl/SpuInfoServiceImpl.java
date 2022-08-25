package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Connor
 * @description 针对表【spu_info(商品表)】的数据库操作Service实现
 * @createDate 2022-08-23 01:15:19
 */
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
        implements SpuInfoService {

    @Autowired
    private SpuImageService spuImageService;
    @Autowired
    private SpuSaleAttrService spuSaleAttrService;
    @Autowired
    private SpuSaleAttrValueService spuSaleAttrValueService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSpuInfo(SpuInfo spuInfo) {
        this.save(spuInfo);
        if (spuInfo.getSpuImageList() != null && spuInfo.getSpuImageList().size() > 0) {
            spuInfo.getSpuImageList().forEach(spuImage -> spuImage.setSpuId(spuInfo.getId()));
            spuImageService.saveBatch(spuInfo.getSpuImageList());
        }
        if (spuInfo.getSpuSaleAttrList() != null && spuInfo.getSpuSaleAttrList().size() > 0) {
            spuInfo.getSpuSaleAttrList().forEach(spuSaleAttr -> {
                spuSaleAttr.setSpuId(spuInfo.getId());
                if (spuSaleAttr.getSpuSaleAttrValueList() != null && spuSaleAttr.getSpuSaleAttrValueList().size() > 0) {
                    spuSaleAttr.getSpuSaleAttrValueList().forEach(spuSaleAttrValue -> {
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                    });
                    spuSaleAttrValueService.saveBatch(spuSaleAttr.getSpuSaleAttrValueList());
                }
            });
            spuSaleAttrService.saveBatch(spuInfo.getSpuSaleAttrList());
        }
    }
}




