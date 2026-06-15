package com.owlsecurity.portal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import com.owlsecurity.portal.dto.ReportRequest;
import com.owlsecurity.portal.entity.Report;
import com.owlsecurity.portal.service.ReportService;

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
    public List<Report> getAllReports() {
        return reportService.getAllReports();
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
    public List<Report> getClientReports(
            @PathVariable Long clientId
    ) {

        return reportService.getReportsByClient(clientId);
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
    
    @GetMapping(
            "/client/{clientId}/date/{reportDate}"
    )
    public List<Report> getClientReportsByDate(
            @PathVariable Long clientId,
            @PathVariable String reportDate
    ) {

        return reportService
                .getReportsByClientAndDate(
                        clientId,
                        reportDate
                );
    }
}