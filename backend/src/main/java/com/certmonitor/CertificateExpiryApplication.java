package com.certmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CertificateExpiryApplication {
    public static void main(String[] args) {
        SpringApplication.run(CertificateExpiryApplication.class, args);
    }
}
