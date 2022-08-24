package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

    @PostMapping
    @ApiOperation("文件上传")
    public Result<Object> fileUpload(@RequestPart MultipartFile file) {
        // TODO: 2022/8/23 文件上传
        return Result.ok("name: " + file.getOriginalFilename() + " size: " + file.getSize());
    }
}
