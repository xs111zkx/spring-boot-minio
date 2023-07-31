package com.zkx.springbootminio.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("192.168.153.131", 9000, false)
                .credentials("123456", "12345678")
                .build();
    }



}
 