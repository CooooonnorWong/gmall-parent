package com.atguigu.gmall.common.utils;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Connor
 * @date 2022/9/8
 */
public class AuthUtils {
    /**
     * 获取用户id和临时用户id
     *
     * @return
     */
    public static UserAuthInfo currentAuthInfo() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String userId = request.getHeader(SysRedisConst.HEADER_USERID);
        String userTempId = request.getHeader(SysRedisConst.HEADER_USERTEMPID);
        UserAuthInfo userAuthInfo = new UserAuthInfo();
        userAuthInfo.setUserId(StringUtils.isEmpty(userId) ? null : Long.parseLong(userId));
        userAuthInfo.setUserTempId(userTempId);
        return userAuthInfo;
    }
}
