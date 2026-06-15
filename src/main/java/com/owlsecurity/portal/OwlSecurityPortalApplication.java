package com.owlsecurity.portal;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OwlSecurityPortalApplication {

    public static void main(String[] args) {

        TimeZone.setDefault(
            TimeZone.getTimeZone(
                "Asia/Kolkata"
            )
        );

        SpringApplication.run(
            OwlSecurityPortalApplication.class,
            args
        );
    }
}