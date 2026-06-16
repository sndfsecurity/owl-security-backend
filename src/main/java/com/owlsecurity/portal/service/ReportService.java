package com.owlsecurity.portal.service;

import java.time.LocalDateTime;
import java.util.List;

import com.owlsecurity.portal.dto.ReportRequest;
import com.owlsecurity.portal.entity.Report;

public interface ReportService {

    Report saveReport(Report report);

    List<Report> getAllReports();

    Report getReportById(Long id);

    Report updateReport(Long id, ReportRequest request);

    void deleteReport(Long id);
    
    List<Report> getReportsByClient(Long clientId);
    
    List<Report> getReportsByDate(
            String reportDate
    );

    List<Report> getReportsByClientAndDate(
            Long clientId,
            String reportDate
    );
    
    List<Report> getReportsByDateRange(
            LocalDateTime start,
            LocalDateTime end
    );

    List<Report> getReportsByClientAndDateRange(
            Long clientId,
            LocalDateTime start,
            LocalDateTime end
    );
}