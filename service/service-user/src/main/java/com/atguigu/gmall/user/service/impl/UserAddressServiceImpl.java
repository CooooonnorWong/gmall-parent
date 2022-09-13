package com.atguigu.gmall.user.service.impl;


import com.atguigu.gmall.common.utils.AuthUtils;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.service.UserAddressService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Connor
 * @description 针对表【user_address(用户地址表)】的数据库操作Service实现
 * @createDate 2022-09-07 02:28:50
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress>
        implements UserAddressService {

    @Override
    public List<UserAddress> getUserAddress() {
        Long userId = AuthUtils.currentAuthInfo().getUserId();
        return baseMapper.selectList(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId));
    }
}




