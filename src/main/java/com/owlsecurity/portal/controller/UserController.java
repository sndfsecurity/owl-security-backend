package com.owlsecurity.portal.controller;

import org.springframework.web.bind.annotation.*;

import com.owlsecurity.portal.dto.ResetPasswordRequest;
import com.owlsecurity.portal.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @PutMapping("/{id}/reset-password")
    public String resetPassword(

            @PathVariable Long id,

            @RequestBody
            ResetPasswordRequest request

    ) {

        userService.resetPassword(
                id,
                request.getNewPassword()
        );

        return "Password Updated Successfully";
    }
}