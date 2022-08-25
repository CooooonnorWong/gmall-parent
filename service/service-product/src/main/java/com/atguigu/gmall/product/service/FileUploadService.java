package com.atguigu.gmall.product.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Connor
 * @date 2022/8/25
 */
public interface FileUploadService {
    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    String upload(MultipartFile file) throws Exception;
}
