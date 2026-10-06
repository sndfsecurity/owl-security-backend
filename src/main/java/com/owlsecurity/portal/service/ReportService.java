package com.owlsecurity.portal.service;

import java.time.LocalDateTime;
import java.util.List;
import com.owlsecurity.portal.dto.ReportRequest;
import com.owlsecurity.portal.entity.Report;
import org.springframework.data.domain.Page;

import org.springframework.web.multipart.MultipartFile;

public interface ReportService {

    Report saveReport(Report report);
    
    Report saveReport(Report report, MultipartFile pdf);
    
    // New draft methods
    
    Report saveDraft(Report report);

    Report saveDraft(Report report, MultipartFile pdf);

    List<Report> getDraftsByClient(Long clientId);

    Report updateDraft(Long id, ReportRequest request);
    
    Report updateDraft(
            Long id,
            ReportRequest request,
            MultipartFile pdf
    );

    Report submitDraft(
            Long id,
            ReportRequest request,
            MultipartFile pdf
    );
    
    // Existing methods - DO NOT CHANGE
    
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
    
    Page<Report> getAllReports(
            int page,
            int size
    );

    Page<Report> getReportsByDateRange(
            LocalDateTime start,
            LocalDateTime end,
            int page,
            int size
    );

    Page<Report> getReportsByClientAndDateRange(
            Long clientId,
            LocalDateTime start,
            LocalDateTime end,
            int page,
            int size
    );
    
    Page<Report> getReportsByClient(
            Long clientId,
            int page,
            int size
    );
    
    List<Report> getRecentReports();
    
    Page<Report> getSubmittedReportsByClient(
            Long clientId,
            int page,
            int size
    );
    
    Page<Report> getSubmittedReports(
            int page,
            int size
    );
    
    Page<Report> getSubmittedReportsByDateRange(
            LocalDateTime start,
            LocalDateTime end,
            int page,
            int size
    );

    Page<Report> getSubmittedReportsByClientAndDateRange(
            Long clientId,
            LocalDateTime start,
            LocalDateTime end,
            int page,
            int size
    );
    
}