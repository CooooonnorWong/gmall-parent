package com.atguigu.gmall.user.service;


import com.atguigu.gmall.model.user.UserAddress;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Connor
 * @description 针对表【user_address(用户地址表)】的数据库操作Service
 * @createDate 2022-09-07 02:28:50
 */
public interface UserAddressService extends IService<UserAddress> {

    /**
     * 获取当前登陆用户的所有收货地址
     *
     * @return
     */
    List<UserAddress> getUserAddress();
}
