package com.owlsecurity.portal.repository;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;

import com.owlsecurity.portal.entity.Client;

public interface ClientRepository
        extends JpaRepository<Client, Long> {
	
	Optional<Client> findByUserId(Long userId);
	
	

}