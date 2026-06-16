package com.owlsecurity.portal.controller;

import java.time.LocalDate;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.owlsecurity.portal.dto.DashboardResponse;
import com.owlsecurity.portal.repository.ClientRepository;
import com.owlsecurity.portal.repository.ReportRepository;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ClientRepository clientRepository;
    private final ReportRepository reportRepository;

    public DashboardController(
            ClientRepository clientRepository,
            ReportRepository reportRepository) {

        this.clientRepository = clientRepository;
        this.reportRepository = reportRepository;
    }

    @GetMapping
    public DashboardResponse getDashboard() {

        long totalClients = clientRepository.count();

        long totalReports = reportRepository.count();

        long reportsToday =
                reportRepository.countByReportDate(
                        LocalDate.now().toString()
                );
        
        long alerts =
                reportRepository.countByPriority(
                        "HIGH"
                );

        return new DashboardResponse(
                totalClients,
                totalReports,
                reportsToday,
                alerts
        );
    }
}