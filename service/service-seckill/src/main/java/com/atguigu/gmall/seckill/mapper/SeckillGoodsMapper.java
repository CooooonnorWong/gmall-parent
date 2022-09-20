package com.atguigu.gmall.seckill.mapper;


import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Connor
 * @description 针对表【seckill_goods】的数据库操作Mapper
 * @createDate 2022-08-25 22:04:36
 * @Entity com.atguigu.gmall.activity.domain.SeckillGoods
 */
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

    /**
     * 获取当天参加秒杀活动的商品列表
     *
     * @return
     */
    List<SeckillGoods> getCurrentSeckillGoodsCache(@Param("date") String date);
}




