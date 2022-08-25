package com.atguigu.gmall.activity.mapper;


import com.atguigu.gmall.model.activity.ActivityRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Connor
 * @description 针对表【activity_rule(优惠规则)】的数据库操作Mapper
 * @createDate 2022-08-25 22:04:36
 * @Entity com.atguigu.gmall.activity.domain.ActivityRule
 */
public interface ActivityRuleMapper extends BaseMapper<ActivityRule> {

    /**
     * 根据活动id获取活动规则列表
     *
     * @param actInfoId
     * @return
     */
    List<ActivityRule> findActivityRuleList(@Param("actInfoId") Long actInfoId);
}




