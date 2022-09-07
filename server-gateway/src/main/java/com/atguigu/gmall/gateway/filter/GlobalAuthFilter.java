package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.gateway.config.AuthProperties;
import com.atguigu.gmall.model.user.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author Connor
 * @date 2022/9/7
 */
@Component
@Slf4j
@EnableConfigurationProperties(AuthProperties.class)
public class GlobalAuthFilter implements GlobalFilter {
    private final AntPathMatcher matcher = new AntPathMatcher();
    @Autowired
    private AuthProperties authProperties;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        //访问公共静态资源
        for (String pattern : authProperties.getNoneAuthUrl()) {
            if (matcher.match(pattern, path)) {
                return chain.filter(exchange);
            }
        }
        //访问服务内部api: 禁止访问
        for (String pattern : authProperties.getForbiddenUrl()) {
            if (matcher.match(pattern, path)) {
                Result<String> result = Result.build("禁止访问", ResultCodeEnum.PERMISSION);
                return responseResult(result, exchange);
            }
        }
        String token = getToken(exchange);
        UserInfo userInfo = getUserInfoByToken(token);
        String uri = exchange.getRequest().getURI().toString();
        //访问受保护资源(登录)
        for (String pattern : authProperties.getAuthUrl()) {
            if (matcher.match(pattern, path)) {
                //检验token
                if (userInfo == null) {
                    //token错误,查无此人,返回登陆页
                    return redirectToLoginPage(authProperties.getLoginPageUrl() + "?originUrl=" + uri, exchange);
                }
                //token正确,查到数据,透传id,放行
                return chain.filter(userIdPenetrate(userInfo, exchange));
            }
        }
        //能走到这儿，既不是静态资源直接放行，也不是必须登录才能访问的，就一普通请求
        //普通请求只要带了 token，说明可能登录了。只要登录了，就透传用户id
        if (userInfo != null) {
            exchange = userIdPenetrate(userInfo, exchange);
        } else {
            //如果前端带了token，但是没用户信息，代表这是假令牌
            if (!StringUtils.isEmpty(token)) {
                return redirectToLoginPage(authProperties.getLoginPageUrl() + "?originUrl=" + uri, exchange);
            }
        }

        return chain.filter(exchange);
    }

    /**
     * 透传用户id
     *
     * @param userInfo
     * @param exchange
     * @return
     */
    private ServerWebExchange userIdPenetrate(UserInfo userInfo, ServerWebExchange exchange) {
        if (userInfo == null) {
            return exchange;
        }
        //请求一旦发来，所有的请求数据是固定的，不能进行任何修改，只能读取
        //根据原来的请求，封装一个新情求
        //放行的时候传改掉的exchange
        return exchange
                .mutate()
                .response(exchange.getResponse())
                .request(exchange.getRequest()
                        .mutate()
                        .header(SysRedisConst.HEADER_USERID, userInfo.getId().toString())
                        .build())
                .build();

    }

    /**
     * 重定向到指定地址
     *
     * @param redirectUrl
     * @param exchange
     * @return
     */
    private Mono<Void> redirectToLoginPage(String redirectUrl, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        //1、重定向【302状态码 + 响应头中 Location: 新位置】
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().add(HttpHeaders.LOCATION, redirectUrl);
        //2、清除旧的错误的Cookie[token]（同名cookie并max-age=0）解决无限重定向问题
        response.getCookies().set("token", ResponseCookie
                .from("token", "")
                .path("/")
                .domain(".gmall.com")
                .maxAge(0)
                .build());
        return response.setComplete();
    }

    /**
     * 根据token获取UserInfo
     *
     * @param token
     * @return
     */
    private UserInfo getUserInfoByToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return Jsons.toObj(redisTemplate.opsForValue().get(SysRedisConst.LOGIN_USER_PREFIX + token), UserInfo.class);
    }

    /**
     * 获取前端返回的token值
     *
     * @param exchange
     * @return
     */
    private String getToken(ServerWebExchange exchange) {
        //由于前端乱写，到处可能都有【Cookie[token=xxx]】【Header[token=xxx]】
        //1、先检查Cookie中有没有这个 token
        HttpCookie token = exchange.getRequest().getCookies().getFirst("token");
        if (token != null) {
            return token.getValue();
        }
        //2.Cookie中没有,再从header中找
        return exchange.getRequest().getHeaders().getFirst("token");
    }

    private Mono<Void> responseResult(Result<String> result, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        return response.writeWith(
                Mono.just(
                        response.bufferFactory()
                                .wrap(
                                        Jsons.toStr(result)
                                                .getBytes(StandardCharsets.UTF_8))));
    }
}
