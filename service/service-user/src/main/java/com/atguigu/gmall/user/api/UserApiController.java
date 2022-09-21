package com.atguigu.gmall.user.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Connor
 * @date 2022/9/14
 */
@RestController
@RequestMapping("/api/inner/rpc/user")
public class UserApiController {
    @Autowired
    private UserAddressService userAddressService;

    @GetMapping("/getUserAddress")
    public Result<List<UserAddress>> getUserAddress() {
        List<UserAddress> list =  userAddressService.getUserAddress();
        return Result.ok(list);
    }

    @GetMapping("/getDefaultUserAddress/{userId}")
    public Result<UserAddress> getDefaultUserAddress(@PathVariable("userId") Long userId) {
        return Result.ok(userAddressService.getDefaultUserAddress(userId));
    }
}
