package com.owlsecurity.portal.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.owlsecurity.portal.dto.ClientDashboardResponse;
import com.owlsecurity.portal.entity.Client;
import com.owlsecurity.portal.entity.Report;
import com.owlsecurity.portal.entity.User;
import com.owlsecurity.portal.repository.ClientRepository;
import com.owlsecurity.portal.repository.ReportRepository;
import com.owlsecurity.portal.repository.UserRepository;

@RestController
@RequestMapping("/api/client")
public class ClientPortalController {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ReportRepository reportRepository;

    public ClientPortalController(
            UserRepository userRepository,
            ClientRepository clientRepository,
            ReportRepository reportRepository) {

        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.reportRepository = reportRepository;
    }

    @GetMapping("/dashboard")
    public ClientDashboardResponse getDashboard(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow();

        Client client = clientRepository
                .findByUserId(user.getId())
                .orElseThrow();

        long totalReports =
                reportRepository.countByClientId(
                        client.getId()
                );

        return new ClientDashboardResponse(
                client.getCompanyName(),
                totalReports,
                client.getStatus()
        );
    }

    @GetMapping("/reports")
    public List<Report> getMyReports(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow();

        Client client = clientRepository
                .findByUserId(user.getId())
                .orElseThrow();

        return reportRepository.findByClientId(
                client.getId()
        );
    }
}