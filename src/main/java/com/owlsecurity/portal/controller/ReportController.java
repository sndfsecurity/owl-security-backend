package com.owlsecurity.portal.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.*;
import com.owlsecurity.portal.dto.ReportRequest;
import com.owlsecurity.portal.entity.Report;
import com.owlsecurity.portal.service.ReportService;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public Report createReport(@RequestBody ReportRequest request) {

        Report report = new Report();

        report.setClientId(request.getClientId());
        report.setReportDate(request.getReportDate());
        report.setReportTime(request.getReportTime());
        report.setStatus(request.getStatus());
        report.setPriority(request.getPriority());
        report.setNotes(request.getNotes());
        
        report.setImagePath(request.getImagePath());
        report.setImageUrl(request.getImageUrl());

        return reportService.saveReport(report);
    }

    @GetMapping
    public Page<Report> getAllReports(

            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "10"
            )
            int size

    ) {

        return reportService
                .getAllReports(
                        page,
                        size
                );
    }
    

    @GetMapping("/{id}")
    public Report getReportById(@PathVariable Long id) {
        return reportService.getReportById(id);
    }

    @PutMapping("/{id}")
    public Report updateReport(
            @PathVariable Long id,
            @RequestBody ReportRequest request) {

        return reportService.updateReport(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteReport(@PathVariable Long id) {

        reportService.deleteReport(id);

        return "Report Deleted Successfully";
    }
    
    
    @GetMapping("/client/{clientId}")
    public Page<Report> getClientReports(
            @PathVariable Long clientId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "5")
            int size
    ) {

        return reportService.getReportsByClient(
                clientId,
                page,
                size
        );
    }
    
    
    
    @GetMapping("/date/{reportDate}")
    public List<Report> getReportsByDate(
            @PathVariable String reportDate
    ) {

        return reportService
                .getReportsByDate(
                        reportDate
                );
    }
    
    
    
    
    @GetMapping("/client/{clientId}/date/{reportDate}")
     public List<Report> getClientReportsByDate(
            @PathVariable Long clientId,
            @PathVariable String reportDate
    ) 
    
    {

        return reportService
                .getReportsByClientAndDate(
                        clientId,
                        reportDate
                );
    }
    
    
    
    @GetMapping("/range")
    public Page<Report> getReportsByRange(

            @RequestParam String fromDate,
            @RequestParam String toDate,

            @RequestParam(required = false)
            Long clientId,

            @RequestParam(
                    defaultValue = "0"
            )
            int page,

            @RequestParam(
                    defaultValue = "10"
            )
            int size
    ) {

        LocalDateTime start =
                LocalDate.parse(fromDate)
                        .atStartOfDay();

        LocalDateTime end =
                LocalDate.parse(toDate)
                        .atTime(
                                23,
                                59,
                                59
                        );

        if(clientId != null) {

            return reportService
                    .getReportsByClientAndDateRange(
                            clientId,
                            start,
                            end,
                            page,
                            size
                    );
        }

        return reportService
                .getReportsByDateRange(
                        start,
                        end,
                        page,
                        size
                );
    }
    
}