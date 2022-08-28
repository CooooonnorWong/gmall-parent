package com.atguigu.gmall.common.config.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Connor
 * @date 2022/8/25
 */
@Data
@ConfigurationProperties(prefix = "gmall.minio")
public class MinioProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private Long maxImgSize;
    private Long maxVideoSize;
}
