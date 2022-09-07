package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
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
import java.util.concurrent.TimeUnit;

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
                .eq(UserInfo::getPasswd, MD5.encrypt(userInfo.getPasswd())));
        if (one == null) {
            return null;
        }
        LoginSuccessVo vo = new LoginSuccessVo();
        vo.setToken(UUID.randomUUID().toString().replace("-", "") + MD5.encrypt(one.getPasswd() + UUID.randomUUID().toString()));
        vo.setNickName(one.getNickName());
        redisTemplate.opsForValue().set(SysRedisConst.LOGIN_USER_PREFIX + vo.getToken(), Jsons.toStr(one), 7, TimeUnit.DAYS);
        return vo;
    }

    @Override
    public void logout(String token) {
        redisTemplate.delete(SysRedisConst.LOGIN_USER_PREFIX + token);
    }
}




