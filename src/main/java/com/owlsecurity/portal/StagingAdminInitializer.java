
package com.owlsecurity.portal;

import com.owlsecurity.portal.entity.User;
import com.owlsecurity.portal.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("staging")
public class StagingAdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${STAGING_ADMIN_NAME:}")
    private String adminName;

    @Value("${STAGING_ADMIN_EMAIL:}")
    private String adminEmail;

    @Value("${STAGING_ADMIN_PASSWORD:}")
    private String adminPassword;

    public StagingAdminInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (adminName.isBlank()
                || adminEmail.isBlank()
                || adminPassword.isBlank()) {
            System.out.println(
                "Staging Admin creation skipped: credentials not configured."
            );
            return;
        }

        if (userRepository.findByEmail(adminEmail).isPresent()) {
            System.out.println("Staging Admin already exists.");
            return;
        }

        User admin = new User();
        admin.setName(adminName);
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole("ADMIN");

        userRepository.save(admin);

        System.out.println("Staging Admin created successfully.");
    }
}
