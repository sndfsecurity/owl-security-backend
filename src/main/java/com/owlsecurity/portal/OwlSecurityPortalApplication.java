package com.owlsecurity.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OwlSecurityPortalApplication {

		    public static void main(String[] args) {
		  
		
		        SpringApplication.run(
		                OwlSecurityPortalApplication.class,
		                args
		        );
    }

    
}

//
//package com.owlsecurity.portal;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//
//import com.owlsecurity.portal.entity.User;
//import com.owlsecurity.portal.repository.UserRepository;
//import com.owlsecurity.portal.service.UserService;
//
//@SpringBootApplication
//public class OwlSecurityPortalApplication {
//
//    public static void main(String[] args) {
//
//        SpringApplication.run(
//                OwlSecurityPortalApplication.class,
//                args
//        );
//    }
//
//    @Bean
//    CommandLineRunner createTestAdmin(
//            UserRepository userRepository,
//            UserService userService
//    ) {
//        return args -> {
//
//            String email = "testadmin@owl.com";
//
//            // Create the test admin only if it does not already exist
//            if (userRepository.findByEmail(email).isEmpty()) {
//
//                User user = new User();
//
//                user.setName("Test Admin");
//                user.setEmail(email);
//                user.setPassword("Admin123");
//                user.setRole("ADMIN");
//
//                userService.saveUser(user);
//
//                System.out.println("======================================");
//                System.out.println("Test Admin created successfully");
//                System.out.println("Email: " + email);
//                System.out.println("Password: Admin123");
//                System.out.println("======================================");
//            }
//        };
//    }
//}