package com.owlsecurity.portal.service;

import com.owlsecurity.portal.entity.User;

public interface UserService {

    User saveUser(User user);
    
    User login(String email, String password);
}