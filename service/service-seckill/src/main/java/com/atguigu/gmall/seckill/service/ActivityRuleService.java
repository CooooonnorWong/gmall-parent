package com.atguigu.gmall.seckill.service;


import com.atguigu.gmall.model.activity.ActivityRule;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Connor
 * @description 针对表【activity_rule(优惠规则)】的数据库操作Service
 * @createDate 2022-08-25 22:04:36
 */
public interface ActivityRuleService extends IService<ActivityRule> {

    /**
     * 根据活动id获取活动规则列表
     *
     * @param actInfoId
     * @return
     */
    List<ActivityRule> findActivityRuleList(Long actInfoId);
}
