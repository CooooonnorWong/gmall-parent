package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.FileUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Connor
 * @date 2022/8/23
 */
@RestController
@Api(tags = "文件上传")
@RequestMapping("/admin/product/fileUpload")
public class FileUploadController {
    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping
    @ApiOperation("文件上传")
    public Result<Object> fileUpload(@RequestPart MultipartFile file) throws Exception {
        String url = fileUploadService.upload(file);
        return Result.ok(url);
    }
}
