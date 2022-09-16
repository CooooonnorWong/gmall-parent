package com.atguigu.gmall.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Connor
 * @date 2022/9/16
 */
@ConfigurationProperties(prefix = "gmall.pay.alipay")
@Data
public class AlipayProperties {
    private String serverUrl;
    private String appId;
    private String privateKey;
    private String format;
    private String charset;
    private String alipayPublicKey;
    private String signType;
    private String notifyUrl;
    private String returnUrl;
}
