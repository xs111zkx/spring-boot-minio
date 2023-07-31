package com.zkx.springbootminio.controller;

import com.zkx.springbootminio.utils.MinioUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/test1")
public class MinioController {
    // 上传
    @PostMapping("/upload")
    public Object upload(@RequestParam("file") MultipartFile multipartFile) throws Exception {
        return MinioUtil.putObject(multipartFile, "test");
    }

    // 下载文件
    @GetMapping("/download")
    public void download(@RequestParam("fileName")String fileName, HttpServletResponse response) {
        MinioUtil.download(fileName, "test", response);
    }

    // 列出所有存储桶名称
    @PostMapping("/list")
    public List<String> list() throws Exception {
        return MinioUtil.listBucketNames();
    }

    // 创建存储桶
    @PostMapping("/createBucket")
    public boolean createBucket(String bucketName) throws Exception {
        return MinioUtil.makeBucket(bucketName);
    }

    // 删除存储桶
    @PostMapping("/deleteBucket")
    public boolean deleteBucket(String bucketName) throws Exception {
        return MinioUtil.removeBucket(bucketName);
    }

    // 列出存储桶中的所有对象名称
    @PostMapping("/listObjectNames")
    public List<String> listObjectNames(String bucketName) throws Exception {
        return MinioUtil.listObjectNames(bucketName);
    }

    // 删除一个对象
    @PostMapping("/removeObject")
    public boolean removeObject(String bucketName, String objectName) throws Exception {
        return MinioUtil.removeObject(bucketName, objectName);
    }

    // 文件访问路径
    @PostMapping("/getObjectUrl")
    public String getObjectUrl(String bucketName, String objectName) throws Exception {
        return MinioUtil.getObjectUrl(bucketName, objectName);
    }
}
