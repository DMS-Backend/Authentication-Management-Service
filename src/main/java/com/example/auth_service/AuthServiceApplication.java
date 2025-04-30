package com.example.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.auth_service", "com.example.repositories", "com.example.dto"})
@EnableJpaRepositories(basePackages = {"com.example.auth_service.repositories", "com.example.repositories"})
@EntityScan(basePackages = {"com.example.auth_service.model", "com.example.dto"})
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}