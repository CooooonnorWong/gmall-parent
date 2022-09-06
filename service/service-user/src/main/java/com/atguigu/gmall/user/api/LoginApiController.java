package com.atguigu.gmall.user.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.model.vo.user.LoginSuccessVo;
import com.atguigu.gmall.user.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Connor
 * @date 2022/9/7
 */
@RestController
@RequestMapping("/api/user/passport")
public class LoginApiController {
    @Autowired
    private UserInfoService userInfoService;

    @PostMapping("/login")
    public Result<Object> login(@RequestBody UserInfo userInfo) {
        LoginSuccessVo vo = userInfoService.login(userInfo);
        if (vo != null) {
            return Result.ok();
        }
        return Result.build("", ResultCodeEnum.LOGIN_ERROR);
    }
}
