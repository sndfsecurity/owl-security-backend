package com.owlsecurity.portal.repository;

import com.owlsecurity.portal.entity.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PushSubscriptionRepository
        extends JpaRepository<PushSubscription, Long> {

    Optional<PushSubscription> findByEndpoint(String endpoint);

    List<PushSubscription> findByClientIdAndActiveTrue(Long clientId);

    List<PushSubscription> findByClientId(Long clientId);
    
    List<PushSubscription> findByRoleAndActiveTrue(String role);
}