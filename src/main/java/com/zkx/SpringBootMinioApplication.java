package com.zkx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.zkx.*")
@SpringBootApplication
public class SpringBootMinioApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootMinioApplication.class, args);
    }
}