package com.solvd.micro9.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StartUsersMicroservice {

    public static void main(String[] args) {
        SpringApplication.run(StartUsersMicroservice.class, args);
    }

}