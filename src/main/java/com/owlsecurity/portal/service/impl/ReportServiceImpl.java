package com.owlsecurity.portal.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.owlsecurity.portal.dto.ReportRequest;
import com.owlsecurity.portal.entity.Report;
import com.owlsecurity.portal.repository.ReportRepository;
import com.owlsecurity.portal.service.ReportService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public Report saveReport(Report report) {
  
        report.setCreatedAt(
        	    LocalDateTime.now()
        	);

	        report.setReportDate(
	        	    LocalDate.now().format(
	        	        DateTimeFormatter.ofPattern("dd-MM-yyyy")
	        	    )
	        	);

	        
	       
	        report.setReportTime(
	        	    LocalTime.now().format(
	        	        DateTimeFormatter.ofPattern("hh:mm a")
	        	    )
	        	);
	        
        return reportRepository.save(report);
    }

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public Report getReportById(Long id) {
        return reportRepository.findById(id).orElse(null);
    }

    @Override
    public Report updateReport(Long id, ReportRequest request) {

        Report report = reportRepository.findById(id).orElse(null);

        if (report == null) {
            return null;
        }

        report.setClientId(request.getClientId());
        report.setReportDate(request.getReportDate());
        report.setReportTime(request.getReportTime());
        report.setStatus(request.getStatus());
        report.setPriority(request.getPriority());
        report.setNotes(request.getNotes());
        report.setImageUrl(request.getImageUrl());

        return reportRepository.save(report);
    }

    @Override
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
    
    
    @Override
    public List<Report> getReportsByClient(Long clientId) {

        return reportRepository.findByClientIdOrderByCreatedAtDesc(
                clientId);
    }
    
    @Override
    public List<Report> getReportsByDate(
            String reportDate
    ) {

        return reportRepository.findByReportDate(
                reportDate
        );
    }
    
    @Override
    public List<Report> getReportsByClientAndDate(
            Long clientId,
            String reportDate
    ) {

        return reportRepository
                .findByClientIdAndReportDate(
                        clientId,
                        reportDate
                );
    }
    
    
    @Override
    public List<Report> getReportsByDateRange(
            LocalDateTime start,
            LocalDateTime end
    ) {

        return reportRepository
                .findByCreatedAtBetween(
                        start,
                        end
                );
    }

    @Override
    public List<Report> getReportsByClientAndDateRange(
            Long clientId,
            LocalDateTime start,
            LocalDateTime end
    ) {

        return reportRepository
                .findByClientIdAndCreatedAtBetween(
                        clientId,
                        start,
                        end
                );
    }
    
    @Override
    public Page<Report> getAllReports(
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );

        return reportRepository
                .findAllByOrderByCreatedAtDesc(
                        pageable
                );
    }
    
    @Override
    public Page<Report> getReportsByDateRange(
            LocalDateTime start,
            LocalDateTime end,
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );

        return reportRepository
                .findByCreatedAtBetween(
                        start,
                        end,
                        pageable
                );
    }
    
    @Override
    public Page<Report> getReportsByClientAndDateRange(
            Long clientId,
            LocalDateTime start,
            LocalDateTime end,
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size
                );

        return reportRepository
                .findByClientIdAndCreatedAtBetween(
                        clientId,
                        start,
                        end,
                        pageable
                );
    }
    
    
    @Override
    public Page<Report> getReportsByClient(
            Long clientId,
            int page,
            int size
    ) {

        return reportRepository
                .findByClientIdOrderByCreatedAtDesc(
                        clientId,
                        PageRequest.of(page, size)
                );
    }
}