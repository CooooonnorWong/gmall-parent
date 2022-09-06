package com.atguigu.gmall.user.service.impl;

import com.atguigu.cache.constant.SysRedisConst;
import com.atguigu.cache.util.Jsons;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessVo;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.user.service.UserInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author Connor
 * @description 针对表【user_info(用户表)】的数据库操作Service实现
 * @createDate 2022-09-07 02:28:50
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
        implements UserInfoService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public LoginSuccessVo login(UserInfo userInfo) {
        UserInfo one = this.getOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getLoginName, userInfo.getLoginName())
                .eq(UserInfo::getPasswd, userInfo.getPasswd()));
        if (one == null) {
            return null;
        }
        LoginSuccessVo vo = new LoginSuccessVo();
        vo.setToken(UUID.randomUUID().toString().replace("-", "") + MD5.encrypt(one.getPasswd() + UUID.randomUUID().toString()));
        vo.setNickName(one.getNickName());
        redisTemplate.opsForValue().set(SysRedisConst.USER_INFO_PREFIX + one.getId(), Jsons.toStr(vo));
        return vo;
    }
}




