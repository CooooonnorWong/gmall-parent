package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.common.config.minio.MinioProperties;
import com.atguigu.gmall.product.service.FileUploadService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;

/**
 * @author Connor
 * @date 2022/8/25
 */
@Service
@EnableConfigurationProperties(MinioProperties.class)
public class FileUploadServiceImpl implements FileUploadService {
    @Autowired
    private MinioProperties minioProperties;
    @Autowired
    private MinioClient minioClient;

    @Override
    public String upload(MultipartFile file) throws Exception {
        // 检查存储桶是否已经存在
        if (!minioClient.bucketExists(minioProperties.getBucketName())) {
            // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
            minioClient.makeBucket(minioProperties.getBucketName());
        }
        String objectName = DateUtil.formatDate(new Date()).replace("-", "/") +
                "/" +
                UUID.randomUUID().toString().replace("-", "") +
                file.getOriginalFilename();
        // 使用putObject上传一个文件到存储桶中。
        PutObjectOptions putObjectOptions = new PutObjectOptions(file.getSize(), minioProperties.getMaxImgSize());
        putObjectOptions.setContentType(file.getContentType());
        minioClient.putObject(minioProperties.getBucketName(),
                objectName,
                file.getInputStream(),
                putObjectOptions);
        return minioProperties.getEndpoint() + "/" + minioProperties.getBucketName() + "/" + objectName;
    }
}
