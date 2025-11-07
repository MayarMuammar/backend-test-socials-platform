package com.mayar.social_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SocialPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialPlatformApplication.class, args);
    }

}
