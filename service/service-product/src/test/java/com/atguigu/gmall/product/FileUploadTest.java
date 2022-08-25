package com.atguigu.gmall.product;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.MinioException;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author Connor
 * @date 2022/8/25
 */
@SpringBootTest
public class FileUploadTest {
    public static void main(String[] args) throws Exception {
        try {
            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = new MinioClient("http://192.168.6.200:9000",
                    "admin",
                    "adminadmin");

            // 检查存储桶是否已经存在
            boolean isExist = minioClient.bucketExists("gmall");
            if (isExist) {
                System.out.println("Bucket already exists.");
            } else {
                // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
                minioClient.makeBucket("gmall");
            }
            InputStream inputStream = new FileInputStream("C:\\Users\\Connor\\Desktop\\表情包\\背叛了工人阶级.jpg");
            // 使用putObject上传一个文件到存储桶中。
            minioClient.putObject("gmall",
                    "背叛了工人阶级.jpg",
                    inputStream,
                    new PutObjectOptions(inputStream.available(), -1L));
            System.out.println("/home/user/Photos/asiaphotos.zip is successfully uploaded as asiaphotos.zip to `asiatrip` bucket.");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        }
    }
}
