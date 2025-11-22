package com.red.care.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class RedCareTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedCareTaskApplication.class, args);
    }
}
