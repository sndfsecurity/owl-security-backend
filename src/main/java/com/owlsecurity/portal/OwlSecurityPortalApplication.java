package com.owlsecurity.portal;

import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.owlsecurity.portal.entity.User;
import com.owlsecurity.portal.repository.UserRepository;

@SpringBootApplication
public class OwlSecurityPortalApplication
        implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @Override
    public void run(String... args)
            throws Exception {

        User admin = new User();

        admin.setName("Admin");
        admin.setEmail("admin@owl.com");
        admin.setPassword(
                passwordEncoder.encode(
                        "admin123"
                )
        );
        admin.setRole("ADMIN");

        userRepository.save(admin);

        System.out.println(
                "Admin Created Successfully"
        );
    }
}


//package com.owlsecurity.portal;
//
//import org.springframework.boot.SpringApplication;
//
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class OwlSecurityPortalApplication {
//
//		    public static void main(String[] args) {
//		  
//		
//		        SpringApplication.run(
//		                OwlSecurityPortalApplication.class,
//		                args
//		        );
//    }
//
//    
//}