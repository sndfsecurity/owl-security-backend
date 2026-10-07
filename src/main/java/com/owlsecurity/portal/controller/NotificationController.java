package com.owlsecurity.portal.controller;

import com.owlsecurity.portal.dto.PushSubscriptionRequest;
import com.owlsecurity.portal.entity.PushSubscription;
import com.owlsecurity.portal.repository.PushSubscriptionRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.owlsecurity.portal.service.WebPushNotificationService;
import com.owlsecurity.portal.entity.Client;
import com.owlsecurity.portal.repository.ClientRepository;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final PushSubscriptionRepository pushSubscriptionRepository;
    
    private final WebPushNotificationService webPushNotificationService;
    
    private final ClientRepository clientRepository;

    public NotificationController(
            PushSubscriptionRepository pushSubscriptionRepository,
            WebPushNotificationService webPushNotificationService,
            ClientRepository clientRepository
    ) {
        this.pushSubscriptionRepository =
                pushSubscriptionRepository;

        this.webPushNotificationService =
                webPushNotificationService;

        this.clientRepository =
                clientRepository;
    }
    
    
    
    @PostMapping("/subscribe")
    public ResponseEntity<?> saveSubscription(
            @RequestBody PushSubscriptionRequest request
    ) {

        if (request.getUserId() == null
                || request.getEndpoint() == null
                || request.getP256dh() == null
                || request.getAuth() == null) {

            return ResponseEntity.badRequest()
                    .body("Invalid push subscription data");
        }

        Optional<Client> clientOptional =
                clientRepository.findByUserId(
                        request.getUserId()
                );

        if (clientOptional.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("No client found for this user.");
        }

        Client client = clientOptional.get();

        Long actualClientId = client.getId();

        Optional<PushSubscription> existing =
                pushSubscriptionRepository
                        .findByEndpoint(
                                request.getEndpoint()
                        );

        PushSubscription subscription;

        if (existing.isPresent()) {
            subscription = existing.get();
        } else {
            subscription = new PushSubscription();
        }

        subscription.setClientId(actualClientId);
        subscription.setEndpoint(request.getEndpoint());
        subscription.setP256dh(request.getP256dh());
        subscription.setAuth(request.getAuth());
        subscription.setActive(true);

        PushSubscription saved =
                pushSubscriptionRepository.save(
                        subscription
                );

        return ResponseEntity.ok(saved.getId());
    }
    
    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateSubscription(
            @RequestBody Map<String, String> request
    ) {
        String endpoint = request.get("endpoint");

        if (endpoint == null || endpoint.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Push endpoint is required.");
        }

        Optional<PushSubscription> existing =
                pushSubscriptionRepository.findByEndpoint(endpoint);

        if (existing.isEmpty()) {
            return ResponseEntity.ok(
                    "No active subscription found."
            );
        }

        PushSubscription subscription =
                existing.get();

        subscription.setActive(false);

        pushSubscriptionRepository.save(subscription);

        return ResponseEntity.ok(
                "Push subscription deactivated."
        );
    }
}