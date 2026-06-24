package com.owlsecurity.portal.controller;

import org.springframework.web.bind.annotation.*;
import com.owlsecurity.portal.dto.LoginRequest;
import com.owlsecurity.portal.dto.LoginResponse;
import com.owlsecurity.portal.dto.RegisterRequest;
import com.owlsecurity.portal.entity.User;
import com.owlsecurity.portal.service.UserService;

import com.owlsecurity.portal.security.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    
    private final JwtUtil jwtUtil;

    public AuthController(
            UserService userService,
            JwtUtil jwtUtil
    ) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

   

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {

    	User user = new User();

    	user.setName(request.getName());
    	user.setEmail(request.getEmail());
    	user.setPassword(request.getPassword());
    	user.setRole(request.getRole());

        return userService.saveUser(user);
    }
    
    
    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request
    ) {

        User user = userService.login(
                request.getEmail(),
                request.getPassword()
                
        );

        if (user == null) {

        	return new LoginResponse(
        		    null,
        		    "Invalid Email or Password",
        		    null,
        		    null,
        		    null
        		);
        }
        
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole()
        );

        return new LoginResponse(
        	    token,
        	    "Login Success",
        	    user.getRole(),
        	    user.getName(),
        	    user.getId()
        	);
    }
}