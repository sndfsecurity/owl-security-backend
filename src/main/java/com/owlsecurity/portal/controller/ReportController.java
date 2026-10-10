package com.owlsecurity.portal.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.*;
import com.owlsecurity.portal.dto.ReportRequest;
import com.owlsecurity.portal.entity.Report;
import com.owlsecurity.portal.service.ReportService;
import org.springframework.data.domain.Page;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;

import com.owlsecurity.portal.security.ClientAccessService;
import org.springframework.security.access.AccessDeniedException;


@RestController
@RequestMapping("/api/reports")
public class ReportController {

	private final ReportService reportService;
	private final ClientAccessService clientAccessService;

	public ReportController(
	        ReportService reportService,
	        ClientAccessService clientAccessService) {
	    this.reportService = reportService;
	    this.clientAccessService = clientAccessService;
	}

    @PostMapping
    public Report createReport(@RequestBody ReportRequest request) {
    	
        clientAccessService.requireAdmin();
        
        Report report = new Report();

        report.setClientId(request.getClientId());
        report.setReportDate(request.getReportDate());
        report.setReportTime(request.getReportTime());
        report.setStatus(request.getStatus());
        report.setPriority(request.getPriority());
        report.setNotes(request.getNotes());
        
        report.setImageUrls(
        	    request.getImageUrls()
        	);

        report.setVideoPath(request.getVideoPath());
        report.setVideoUrl(request.getVideoUrl());
        
        report.setVideoUrls(
        	    request.getVideoUrls() != null && !request.getVideoUrls().isEmpty()
        	        ? request.getVideoUrls()
        	        : (request.getVideoUrl() != null && !request.getVideoUrl().isBlank()
        	            ? List.of(request.getVideoUrl())
        	            : new ArrayList<>())
        	);
        
        return reportService.saveReport(report);
    }
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Report createReportWithPdf(
    		
            @RequestPart("request") ReportRequest request,
            @RequestPart(value = "pdf", required = false) MultipartFile pdf) {
    	
        clientAccessService.requireAdmin();


        Report report = new Report();
        report.setClientId(request.getClientId());
        report.setReportDate(request.getReportDate());
        report.setReportTime(request.getReportTime());
        report.setStatus(request.getStatus());
        report.setPriority(request.getPriority());
        report.setNotes(request.getNotes());
        report.setImageUrls(request.getImageUrls());
        
        report.setVideoPath(request.getVideoPath());
        report.setVideoUrl(request.getVideoUrl());
        
        report.setVideoUrls(
        	    request.getVideoUrls() != null && !request.getVideoUrls().isEmpty()
        	        ? request.getVideoUrls()
        	        : (request.getVideoUrl() != null && !request.getVideoUrl().isBlank()
        	            ? List.of(request.getVideoUrl())
        	            : new ArrayList<>())
        	);

        return reportService.saveReport(report, pdf);
    }
    
    // new draft methods ...........................
    
    // =========================
    // DRAFT REPORT APIs
    // =========================

    @PostMapping(
            value = "/draft",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Report saveDraft(
            @RequestPart("request") ReportRequest request,
            @RequestPart(value = "pdf", required = false) MultipartFile pdf) {
    	
    	clientAccessService.requireAdmin();

        Report report = new Report();

        report.setClientId(request.getClientId());
        report.setReportDate(request.getReportDate());
        report.setReportTime(request.getReportTime());
        report.setStatus(request.getStatus());
        report.setPriority(request.getPriority());
        report.setNotes(request.getNotes());

        report.setImageUrls(request.getImageUrls());

        report.setVideoPath(request.getVideoPath());
        report.setVideoUrl(request.getVideoUrl());

        report.setVideoUrls(
                request.getVideoUrls() != null
                        && !request.getVideoUrls().isEmpty()
                        ? request.getVideoUrls()
                        : (request.getVideoUrl() != null
                            && !request.getVideoUrl().isBlank()
                            ? List.of(request.getVideoUrl())
                            : new ArrayList<>())
        );

        report.setReportLifecycle("DRAFT");
        report.setDraftData(request.getDraftData());

        return reportService.saveDraft(report, pdf);
    }

    @GetMapping("/client/{clientId}/drafts")
    public List<Report> getDraftsByClient(
            @PathVariable Long clientId) {

    	clientAccessService.requireAdmin();

        return reportService.getDraftsByClient(clientId);
    }

    @PutMapping("/draft/{id}")
    public Report updateDraft(
            @PathVariable Long id,
            @RequestBody ReportRequest request) {
    	
        clientAccessService.requireAdmin();

        return reportService.updateDraft(id, request);
    }
    
    
    @PutMapping(
            value = "/draft/{id}/save",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Report updateDraftWithPdf(
            @PathVariable Long id,
            @RequestPart("request") ReportRequest request,
            @RequestPart(value = "pdf", required = false) MultipartFile pdf) {
    	
        clientAccessService.requireAdmin();


        return reportService.updateDraft(id, request, pdf);
    }
    

    @PutMapping(
            value = "/draft/{id}/submit",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Report submitDraft(
            @PathVariable Long id,
            @RequestPart("request") ReportRequest request,
            @RequestPart(value = "pdf", required = false) MultipartFile pdf) {
    	
        clientAccessService.requireAdmin();


        return reportService.submitDraft(id, request, pdf);
    }
    
    
    //......................................................................................................

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
	
    	clientAccessService.requireAdmin();
    	
        return reportService
                .getAllReports(
                        page,
                        size
                );
    }
    
    @GetMapping("/submitted")
    public Page<Report> getSubmittedReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

    	clientAccessService.requireAdmin();
        return reportService.getSubmittedReports(
                page,
                size
        );
    }
    
    

		@GetMapping("/submitted/range")
		public Page<Report> getSubmittedReportsByRange(
		        @RequestParam String fromDate,
		        @RequestParam String toDate,
		        @RequestParam(required = false) Long clientId,
		        @RequestParam(defaultValue = "0") int page,
		        @RequestParam(defaultValue = "10") int size
		) {
		    LocalDateTime start =
		            LocalDate.parse(fromDate).atStartOfDay();
		
		    LocalDateTime end =
		            LocalDate.parse(toDate).atTime(23, 59, 59);
		
		    if (clientAccessService.isAdmin()) {
		        if (clientId != null) {
		            return reportService.getSubmittedReportsByClientAndDateRange(
		                    clientId, start, end, page, size
		            );
		        }
		
		        return reportService.getSubmittedReportsByDateRange(
		                start, end, page, size
		        );
		    }
		
		    Long loggedInClientId =
		            clientAccessService.getLoggedInClientId();
		
		    if (clientId != null && !clientId.equals(loggedInClientId)) {
		        throw new AccessDeniedException(
		                "You cannot access another client's data"
		        );
		    }
		
		    return reportService.getSubmittedReportsByClientAndDateRange(
		            loggedInClientId, start, end, page, size
		    );
		}

    
   

    @GetMapping("/{id}")
    public Report getReportById(@PathVariable Long id) {

        Report report = reportService.getReportById(id);

        if (report == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    "Report not found"
            );
        }

        clientAccessService.verifyClientAccess(report.getClientId());

        return report;
    }
    


		@PutMapping("/{id}")
		public Report updateReport(
		        @PathVariable Long id,
		        @RequestBody ReportRequest request) {
		
		    Report existingReport = reportService.getReportById(id);
		
		    if (existingReport == null) {
		        throw new org.springframework.web.server.ResponseStatusException(
		                org.springframework.http.HttpStatus.NOT_FOUND,
		                "Report not found"
		        );
		    }
		
		    clientAccessService.requireAdmin();
		    
		    
		    if (request.getClientId() != null
		            && !request.getClientId().equals(existingReport.getClientId())) {
		        throw new org.springframework.web.server.ResponseStatusException(
		                org.springframework.http.HttpStatus.FORBIDDEN,
		                "Changing report ownership is not allowed"
		        );
		    }
		
		    return reportService.updateReport(id, request);
		}


    
		@DeleteMapping("/{id}")
		public String deleteReport(@PathVariable Long id) {

		    Report report = reportService.getReportById(id);

		    if (report == null) {
		        throw new org.springframework.web.server.ResponseStatusException(
		                org.springframework.http.HttpStatus.NOT_FOUND,
		                "Report not found"
		        );
		    }

		    clientAccessService.requireAdmin();

		    reportService.deleteReport(id);

		    return "Report Deleted Successfully";
		}
    
    
    @GetMapping("/client/{clientId}")
    public Page<Report> getClientReports(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        clientAccessService.verifyClientAccess(clientId);

        return reportService.getReportsByClient(
                clientId,
                page,
                size
        );
    }
    
    
    @GetMapping("/client/{clientId}/submitted")
    public Page<Report> getSubmittedClientReports(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
    	
    	clientAccessService.verifyClientAccess(clientId);

        return reportService.getSubmittedReportsByClient(
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
		        @RequestParam(required = false) Long clientId,
		        @RequestParam(defaultValue = "0") int page,
		        @RequestParam(defaultValue = "8") int size
		) {
		    LocalDateTime start =
		            LocalDate.parse(fromDate).atStartOfDay();
		
		    LocalDateTime end =
		            LocalDate.parse(toDate).atTime(23, 59, 59);
		
		    if (clientAccessService.isAdmin()) {
		        if (clientId != null) {
		            return reportService.getReportsByClientAndDateRange(
		                    clientId, start, end, page, size
		            );
		        }
		
		        return reportService.getReportsByDateRange(
		                start, end, page, size
		        );
		    }
		
		    Long loggedInClientId =
		            clientAccessService.getLoggedInClientId();
		
		    if (clientId != null && !clientId.equals(loggedInClientId)) {
		        throw new AccessDeniedException(
		                "You cannot access another client's data"
		        );
		    }
		
		    return reportService.getReportsByClientAndDateRange(
		            loggedInClientId, start, end, page, size
		    );
		}

    
    
    
    
    @GetMapping("/recent")
    public List<Report> getRecentReports() {

        return reportService
                .getRecentReports();

    }
    
}