
package com.owlsecurity.portal.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.owlsecurity.portal.entity.Client;
import com.owlsecurity.portal.entity.User;
import com.owlsecurity.portal.repository.ClientRepository;
import com.owlsecurity.portal.repository.UserRepository;

@Service
public class ClientAccessService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    public ClientAccessService(
            UserRepository userRepository,
            ClientRepository clientRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    private User getLoggedInUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().equals("anonymousUser")) {
            throw new AccessDeniedException("Authentication required");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new AccessDeniedException("User not found"));
    }

    public boolean isAdmin() {
        String role = getLoggedInUser().getRole();

        return role != null
                && (role.equalsIgnoreCase("ADMIN")
                || role.equalsIgnoreCase("ROLE_ADMIN"));
    }

    public void requireAdmin() {
        if (!isAdmin()) {
            throw new AccessDeniedException(
                    "Only admin can perform this operation");
        }
    }

    
    public Long getLoggedInClientId() {
        User user = getLoggedInUser();

        System.out.println("Logged-in user ID: " + user.getId());
        System.out.println("Logged-in user role: " + user.getRole());

        if (user.getRole() != null &&
                (user.getRole().equalsIgnoreCase("ADMIN") ||
                 user.getRole().equalsIgnoreCase("ROLE_ADMIN"))) {
            throw new AccessDeniedException(
                    "Admin does not have a client-specific ID");
        }

        Client client = clientRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new AccessDeniedException("Client not found"));

        System.out.println("Logged-in client ID: " + client.getId());

        return client.getId();
    }
    

    public void verifyClientAccess(Long requestedClientId) {

        // Admin can access any client's reports.
        if (isAdmin()) {
            return;
        }

        Long loggedInClientId = getLoggedInClientId();

        if (!loggedInClientId.equals(requestedClientId)) {
            throw new AccessDeniedException(
                    "You cannot access another client's data");
        }
    }
}
