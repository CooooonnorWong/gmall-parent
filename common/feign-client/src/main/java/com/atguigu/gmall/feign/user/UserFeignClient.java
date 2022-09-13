package com.atguigu.gmall.feign.user;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Connor
 * @date 2022/9/14
 */
@FeignClient("service-user")
@RequestMapping("/api/inner/rpc/user")
public interface UserFeignClient {
    /**
     * 获取当前登录用户的所有收货地址
     *
     * @return
     */
    @GetMapping("/getUserAddress")
    Result<List<UserAddress>> getUserAddress();
}
