package com.zkx.springbootminio.utils;

import io.minio.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class MinioUtil {
    private static String url = "http://192.168.153.131:9000/";
    @Autowired
    private MinioClient client;
    private static MinioClient minioClient;

    /**
     * 通过该方法注入MinioClient，否则会空指针
     */
    @PostConstruct
    public void init() {
        MinioUtil.minioClient = client;
    }

    /**
     * 上传
     */
    public static String putObject(MultipartFile multipartFile, String bucket) throws Exception {
        // bucket 不存在，创建
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {
            // 上传文件的名称
            String fileName = multipartFile.getOriginalFilename();
            fileName = System.currentTimeMillis() + fileName;
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .contentType(multipartFile.getContentType())
                    .bucket(bucket)
                    .object(fileName)
                    .stream(inputStream, multipartFile.getSize(), 10000000)
                    .build();
            minioClient.putObject(putObjectArgs);
            // 返回访问路径
            return url + bucket + "/" + UriUtils.encode(fileName, StandardCharsets.UTF_8);
        }
    }

    /**
     * 文件下载
     */
    public static void download(String fileName,  String bucket, HttpServletResponse response){
        // 从链接中得到文件名
        InputStream inputStream;
        try {
            StatObjectArgs statObjectArgs = StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .build();
            StatObjectResponse stat = minioClient.statObject(statObjectArgs);
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .build();
            inputStream = minioClient.getObject(getObjectArgs);
            response.setContentType(stat.contentType());
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            IOUtils.copy(inputStream, response.getOutputStream());
            inputStream.close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("有异常：" + e);
        }
    }

    /**
     * 列出所有存储桶名称
     *
     * @return
     * @throws Exception
     */
    public static List<String> listBucketNames()
            throws Exception {
        List<Bucket> bucketList = listBuckets();
        List<String> bucketListName = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            bucketListName.add(bucket.name());
        }
        return bucketListName;
    }

    /**
     * 查看所有桶
     *
     * @return
     * @throws Exception
     */
    public static List<Bucket> listBuckets()
            throws Exception {
        return minioClient.listBuckets();
    }

    /**
     * 检查存储桶是否存在
     *
     * @param bucketName
     * @return
     * @throws Exception
     */
    public static boolean bucketExists(String bucketName) throws Exception {
        boolean flag = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (flag) {
            return true;
        }
        return false;
    }

    /**
     * 创建存储桶
     *
     * @param bucketName
     * @return
     * @throws Exception
     */
    public static boolean makeBucket(String bucketName)
            throws Exception {
        boolean flag = bucketExists(bucketName);
        if (!flag) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除桶
     *
     * @param bucketName
     * @return
     * @throws Exception
     */
    public static boolean removeBucket(String bucketName)
            throws Exception {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                // 有对象文件，则删除失败
                if (item.size() > 0) {
                    return false;
                }
            }
            // 删除存储桶，注意，只有存储桶为空时才能删除成功。
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            flag = bucketExists(bucketName);
            if (!flag) {
                return true;
            }

        }
        return false;
    }

    /**
     * 列出存储桶中的所有对象
     *
     * @param bucketName 存储桶名称
     * @return
     * @throws Exception
     */
    public static Iterable<Result<Item>> listObjects(String bucketName) throws Exception {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            return minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
        }
        return null;
    }

    /**
     * 列出存储桶中的所有对象名称
     *
     * @param bucketName 存储桶名称
     * @return
     * @throws Exception
     */
    public static List<String> listObjectNames(String bucketName) throws Exception {
        List<String> listObjectNames = new ArrayList<>();
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                listObjectNames.add(item.objectName());
            }
        }
        return listObjectNames;
    }

    /**
     * 删除一个对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @throws Exception
     */
    public static boolean removeObject(String bucketName, String objectName) throws Exception {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            List<String> objectList = listObjectNames(bucketName);
            for (String s : objectList) {
                if(s.equals(objectName)){
                    RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build();
                    minioClient.removeObject(removeObjectArgs);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 文件访问路径
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     * @throws Exception
     */
    public static String getObjectUrl(String bucketName, String objectName) throws Exception {
        boolean flag = bucketExists(bucketName);
        String url = "";
        if (flag) {
            GetPresignedObjectUrlArgs presignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build();
            url = minioClient.getPresignedObjectUrl(presignedObjectUrlArgs);
        }
        return url;
    }
}
