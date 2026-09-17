package com.owlsecurity.portal.dto;

import java.time.LocalDateTime;

public class LoginResponse {

    private String token;
    private String message;
    private String role;
    private String name;
    
    private Long userId;
    
    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;

    public LoginResponse() {
    }

    public LoginResponse(
            String token,
            String message,
            String role,
            String name,
            Long userId,
            LocalDateTime createdAt,
            LocalDateTime lastLogin
    ) {
        this.token = token;
        this.message = message;
        this.role = role;
        this.name = name;
        this.userId = userId;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }
    
    
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
}