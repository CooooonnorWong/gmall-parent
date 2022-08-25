package com.atguigu.gmall.activity.service.impl;


import com.atguigu.gmall.activity.mapper.ActivityRuleMapper;
import com.atguigu.gmall.activity.service.ActivityRuleService;
import com.atguigu.gmall.model.activity.ActivityRule;
import com.atguigu.gmall.model.enums.ActivityType;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Connor
 * @description 针对表【activity_rule(优惠规则)】的数据库操作Service实现
 * @createDate 2022-08-25 22:04:36
 */
@Service
public class ActivityRuleServiceImpl extends ServiceImpl<ActivityRuleMapper, ActivityRule>
        implements ActivityRuleService {

    @Override
    public List<ActivityRule> findActivityRuleList(Long actInfoId) {
        List<ActivityRule> ruleList = baseMapper.findActivityRuleList(actInfoId);
        ruleList.forEach(rule -> {
            if (ActivityType.FULL_DISCOUNT.name().equalsIgnoreCase(rule.getActivityType())) {
// TODO: 2022/8/26 查询活动规则列表
            } else if (ActivityType.FULL_REDUCTION.name().equalsIgnoreCase(rule.getActivityType())) {
//                rule.setReduceAmount(rule.getConditionAmount());
            }
        });
        return ruleList;
    }
}




