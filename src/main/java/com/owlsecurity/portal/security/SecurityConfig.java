package com.owlsecurity.portal.security;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;
import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

    	http
        .cors(withDefaults())

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth
                		
                		.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                		.requestMatchers(
                			    "/auth/**",
                			    "/api/clients/register"
                			).permitAll()

                        .requestMatchers("/api/dashboard/**")
                        .hasRole("ADMIN")
                        
                        .requestMatchers("/api/users/**")
                        .hasRole("ADMIN")
                        
                        .requestMatchers("/api/clients/user/**")
                        .hasRole("CLIENT")
                        
                        .requestMatchers(
                                "/api/clients/profile/**"
                        )
                        .hasRole("CLIENT")

                        .requestMatchers("/api/clients/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/reports/client/**")
                        .hasAnyRole("ADMIN", "CLIENT")

                        .requestMatchers("/api/reports/range")
                        .hasAnyRole("ADMIN", "CLIENT")
                        
                        .requestMatchers("/api/reports/**")
                        .hasRole("ADMIN")
                        
                        .requestMatchers("/api/client/**")
                        .hasRole("CLIENT")
                        
                        .requestMatchers(
                        	    "/api/upload/**"
                        	).permitAll()

                        
                        	.requestMatchers(
                        	    "/uploads/**"
                        	).permitAll()
                        	
                        	
                        	.anyRequest()
                        	.permitAll()
                );

//                .addFilterBefore(
//                        jwtFilter,
//                        UsernamePasswordAuthenticationFilter.class
//                );

        return http.build();
    }
}