package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessVo;
import com.atguigu.gmall.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Connor
 * @date 2022/9/7
 */
@RestController
@RequestMapping("/api/user/passport")
public class UserController {
    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/login")
    public Result<Object> login(@RequestBody UserInfo userInfo) {
        LoginSuccessVo vo = userInfoService.login(userInfo);
        if (vo != null) {
            return Result.ok(vo);
        }
        return Result.build("", ResultCodeEnum.LOGIN_ERROR);
    }

    @GetMapping("/logout")
    public Result<Object> logout(@RequestHeader("token") String token) {
        userInfoService.logout(token);
        return Result.ok();
    }
}
