package com.owlsecurity.portal.service.impl;

import org.springframework.stereotype.Service;

import com.owlsecurity.portal.entity.Client;
import com.owlsecurity.portal.entity.User;
import com.owlsecurity.portal.repository.ClientRepository;
import com.owlsecurity.portal.repository.UserRepository;
import com.owlsecurity.portal.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    
    private final ClientRepository clientRepository;
 
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(
    		
    	    UserRepository userRepository,
    	    ClientRepository clientRepository
    	) {
    	    this.userRepository = userRepository;
    	    this.clientRepository = clientRepository;
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
        
        if(user.getRole().equals("CLIENT")) {

            Client client =
                clientRepository
                    .findByUserId(user.getId())
                    .orElse(null);

            if(client != null &&
               "INACTIVE".equals(client.getStatus())) {

                return null;
            }
        }
        

        return user;
    }
    
    
    @Override
    public void resetPassword(
            Long userId,
            String newPassword
    ) {

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "User not found"
                                )
                        );

        if (newPassword == null
                || newPassword.trim().isEmpty()) {

            throw new RuntimeException(
                    "Password cannot be empty"
            );
        }

        if (newPassword.length() < 6) {

            throw new RuntimeException(
                    "Password must be at least 6 characters"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        newPassword
                )
        );

        userRepository.save(user);
    }
}