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

	        
	       
	        LocalTime currentTime =
	        	    LocalTime.now(
	        	        ZoneId.of("Asia/Kolkata")
	        	    );

	        	report.setReportTime(
	        	    currentTime.format(
	        	        DateTimeFormatter.ofPattern(
	        	            "hh:mm a"
	        	        )
	        	    )
	        	);
	        
        return reportRepository.save(report);
    }

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAll();
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

        return reportRepository.findByClientId(clientId);
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
}