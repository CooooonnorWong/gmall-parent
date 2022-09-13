package com.atguigu.gmall.common.config;

import com.atguigu.gmall.common.constant.SysRedisConst;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Connor
 * @date 2022/9/8
 */
@Component
public class FeignInterceptorConfig {

    /**
     * 请求拦截器
     * 对feign请求做最后一次修改
     * 将
     *
     * @return
     */
    @Bean
    public RequestInterceptor remainAuthInfoInterceptor() {
        return template -> {
            //获取当前tomcat线程绑定的请求内容
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            //对feign请求模板做最后一次修改
            template.header(SysRedisConst.HEADER_USERID, request.getHeader(SysRedisConst.HEADER_USERID));
            template.header(SysRedisConst.HEADER_USERTEMPID, request.getHeader(SysRedisConst.HEADER_USERTEMPID));
        };
    }
}
