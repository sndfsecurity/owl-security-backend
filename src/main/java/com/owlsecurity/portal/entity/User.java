package com.owlsecurity.portal.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.PrePersist;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(unique = true)
    private String email;
    
    private String password;

    private String role;
    
    @Column(nullable = true)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime lastLogin;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
	
	@PrePersist
	protected void onCreate() {
	    if (createdAt == null) {
	        createdAt = LocalDateTime.now();
	    }
	}

	
}