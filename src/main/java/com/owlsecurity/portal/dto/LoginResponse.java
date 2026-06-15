package com.owlsecurity.portal.dto;

public class LoginResponse {

    private String token;
    private String message;
    private String role;
    private String name;
    
    private Long userId;

    public LoginResponse() {
    }

    public LoginResponse(
            String token,
            String message,
            String role,
            String name,
            Long userId
    ) {
        this.token = token;
        this.message = message;
        this.role = role;
        this.name = name;
        this.userId = userId;
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
}