package com.atguigu.gmall.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author Connor
 * @date 2022/9/7
 */
@Data
@ConfigurationProperties(prefix = "gmall.auth")
public class AuthProperties {
    private List<String> authUrl;
    private List<String> noneAuthUrl;
    private List<String> forbiddenUrl;
    private String loginPageUrl;
}
