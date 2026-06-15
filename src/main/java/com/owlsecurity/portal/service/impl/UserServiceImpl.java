package com.owlsecurity.portal.service.impl;

import org.springframework.stereotype.Service;


import com.owlsecurity.portal.entity.User;
import com.owlsecurity.portal.repository.UserRepository;
import com.owlsecurity.portal.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User saveUser(User user) {

        user.setPassword(
            passwordEncoder.encode(user.getPassword())
        );

        return userRepository.save(user);
    }
    
    
    
    @Override
    public User login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if(user == null) {
            return null;
        }

        if(!passwordEncoder.matches(
                password,
                user.getPassword()
        )) {
            return null;
        }

        return user;
    }
}