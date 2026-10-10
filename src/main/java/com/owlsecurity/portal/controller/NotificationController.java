
package com.owlsecurity.portal.controller;

import com.owlsecurity.portal.dto.PushSubscriptionRequest;
import com.owlsecurity.portal.entity.Client;
import com.owlsecurity.portal.entity.PushSubscription;
import com.owlsecurity.portal.entity.User;
import com.owlsecurity.portal.repository.ClientRepository;
import com.owlsecurity.portal.repository.PushSubscriptionRepository;
import com.owlsecurity.portal.repository.UserRepository;
import com.owlsecurity.portal.service.WebPushNotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final WebPushNotificationService webPushNotificationService;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public NotificationController(
            PushSubscriptionRepository pushSubscriptionRepository,
            WebPushNotificationService webPushNotificationService,
            ClientRepository clientRepository,
            UserRepository userRepository
    ) {
        this.pushSubscriptionRepository = pushSubscriptionRepository;
        this.webPushNotificationService = webPushNotificationService;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> saveSubscription(
            @RequestBody PushSubscriptionRequest request
    ) {
        if (request.getEndpoint() == null
                || request.getEndpoint().isBlank()
                || request.getP256dh() == null
                || request.getP256dh().isBlank()
                || request.getAuth() == null
                || request.getAuth().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Invalid push subscription data");
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || "anonymousUser".equals(authentication.getName())) {
            throw new AccessDeniedException("Authentication required");
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new AccessDeniedException("Authenticated user not found"));

        String role = user.getRole();

        if (role == null) {
            throw new AccessDeniedException("User role not found");
        }

        role = role.replace("ROLE_", "").toUpperCase();

        if (!role.equals("ADMIN") && !role.equals("CLIENT")) {
            throw new AccessDeniedException("Unsupported user role");
        }

        Long actualClientId = null;

        if ("CLIENT".equals(role)) {
            Optional<Client> clientOptional =
                    clientRepository.findByUserId(user.getId());

            if (clientOptional.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("No client found for this user.");
            }

            actualClientId = clientOptional.get().getId();
        }

        Optional<PushSubscription> existing =
                pushSubscriptionRepository.findByEndpoint(
                        request.getEndpoint()
                );

        PushSubscription subscription = existing.orElseGet(
                PushSubscription::new
        );

        subscription.setUserId(user.getId());
        subscription.setRole(role);
        subscription.setClientId(actualClientId);
        subscription.setEndpoint(request.getEndpoint());
        subscription.setP256dh(request.getP256dh());
        subscription.setAuth(request.getAuth());
        subscription.setActive(true);

        PushSubscription saved =
                pushSubscriptionRepository.save(subscription);

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

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || "anonymousUser".equals(authentication.getName())) {
            throw new AccessDeniedException("Authentication required");
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new AccessDeniedException("Authenticated user not found"));

        Optional<PushSubscription> existing =
                pushSubscriptionRepository.findByEndpoint(endpoint);

        if (existing.isEmpty()) {
            return ResponseEntity.ok("No active subscription found.");
        }

        PushSubscription subscription = existing.get();

        // Only the owner of this subscription can deactivate it.
        if (subscription.getUserId() == null
                || !subscription.getUserId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "You cannot deactivate another user's subscription"
            );
        }

        subscription.setActive(false);
        pushSubscriptionRepository.save(subscription);

        return ResponseEntity.ok("Push subscription deactivated.");
    }
}
