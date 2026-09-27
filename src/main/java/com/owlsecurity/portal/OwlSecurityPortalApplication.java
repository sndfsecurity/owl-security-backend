package com.owlsecurity.portal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.owlsecurity.portal.entity.User;
import com.owlsecurity.portal.repository.UserRepository;
import com.owlsecurity.portal.service.UserService;

@SpringBootApplication
public class OwlSecurityPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                OwlSecurityPortalApplication.class,
                args
        );
    }

    @Bean
    CommandLineRunner createAdmin(
            UserRepository userRepository,
            UserService userService
    ) {
        return args -> {
            String email = "admin@owl123.com";

            if (userRepository.findByEmail(email).isEmpty()) {
                User user = new User();
                user.setName("OWL_Admin");
                user.setEmail(email);
                user.setPassword("Admin7776");
                user.setRole("ADMIN");

                userService.saveUser(user);

                System.out.println("New admin created successfully.");
            } else {
                System.out.println("Admin already exists.");
            }
        };
    }
}

