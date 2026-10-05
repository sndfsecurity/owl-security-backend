package com.owlsecurity.portal.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.owlsecurity.portal.dto.ReportRequest;
import com.owlsecurity.portal.entity.Report;
import com.owlsecurity.portal.repository.ReportRepository;
import com.owlsecurity.portal.service.CloudinaryService;
import com.owlsecurity.portal.service.ReportService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

@Service
public class ReportServiceImpl implements ReportService {

	private final ReportRepository reportRepository;
	private final CloudinaryService cloudinaryService;

	public ReportServiceImpl(
	        ReportRepository reportRepository,
	        CloudinaryService cloudinaryService
	) {
	    this.reportRepository = reportRepository;
	    this.cloudinaryService = cloudinaryService;
	}
	
	
	@Override
	public Report saveReport(Report report) {
	    ZonedDateTime indiaNow = ZonedDateTime.now(
	        ZoneId.of("Asia/Kolkata")
	    );

	    report.setCreatedAt(indiaNow.toLocalDateTime());

	    report.setReportDate(
	        indiaNow.format(
	            DateTimeFormatter.ofPattern(
	                "dd-MM-yyyy",
	                java.util.Locale.ENGLISH
	            )
	        )
	    );

	    report.setReportTime(
	        indiaNow.format(
	            DateTimeFormatter.ofPattern(
	                "hh:mm a",
	                java.util.Locale.ENGLISH
	            )
	        )
	    );

	    return reportRepository.save(report);
	}
	
	@Override
	public Report saveReport(Report report, MultipartFile pdf) {
	    if (pdf != null && !pdf.isEmpty()) {
	        try {
	            String pdfUrl = cloudinaryService.uploadPdf(pdf);
	            report.setPdfUrl(pdfUrl);
	        } catch (IOException e) {
	            throw new RuntimeException("Failed to upload PDF", e);
	        }
	    }

	    return saveReport(report);
	}
	
	// new methods for the draft
	
	@Override
	public Report saveDraft(Report report) {
	    ZonedDateTime indiaNow =
	            ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

	    /*
	     * For a new draft, createdAt should represent
	     * when the draft was first created.
	     */
	    if (report.getCreatedAt() == null) {
	        report.setCreatedAt(indiaNow.toLocalDateTime());
	    }

	    /*
	     * Do NOT overwrite reportDate/reportTime here.
	     * They belong to the draft data coming from the frontend.
	     */
	    report.setUpdatedAt(indiaNow.toLocalDateTime());
	    report.setReportLifecycle("DRAFT");

	    return reportRepository.save(report);
	}

	@Override
	public Report saveDraft(Report report, MultipartFile pdf) {

	    if (pdf != null && !pdf.isEmpty()) {
	        try {
	            String pdfUrl = cloudinaryService.uploadPdf(pdf);
	            report.setPdfUrl(pdfUrl);
	        } catch (IOException e) {
	            throw new RuntimeException("Failed to upload draft PDF", e);
	        }
	    }

	    return saveDraft(report);
	}

	@Override
	public List<Report> getDraftsByClient(Long clientId) {
	    return reportRepository
	            .findByClientIdAndReportLifecycleOrderByUpdatedAtDesc(
	                    clientId,
	                    "DRAFT"
	            );
	}

	@Override
	public Report updateDraft(Long id, ReportRequest request) {

	    Report report = reportRepository.findById(id).orElse(null);

	    if (report == null) {
	        return null;
	    }

	    /*
	     * Only update an existing draft through this method.
	     */
	    if (!"DRAFT".equals(report.getReportLifecycle())) {
	        return report;
	    }

	    report.setClientId(request.getClientId());
	    report.setReportDate(request.getReportDate());
	    report.setReportTime(request.getReportTime());
	    report.setStatus(request.getStatus());
	    report.setPriority(request.getPriority());
	    report.setNotes(request.getNotes());
	    report.setDraftData(request.getDraftData());

	    if (request.getImageUrls() != null) {
	        report.setImageUrls(request.getImageUrls());
	    }

	    report.setVideoPath(request.getVideoPath());
	    report.setVideoUrl(request.getVideoUrl());

	    if (request.getVideoUrls() != null) {
	        report.setVideoUrls(request.getVideoUrls());
	    } else if (request.getVideoUrl() != null
	            && !request.getVideoUrl().isBlank()) {
	        report.setVideoUrls(List.of(request.getVideoUrl()));
	    }

	    report.setReportLifecycle("DRAFT");

	    report.setUpdatedAt(
	            ZonedDateTime.now(ZoneId.of("Asia/Kolkata"))
	                    .toLocalDateTime()
	    );

	    return reportRepository.save(report);
	}
	
	@Override
	public Report updateDraft(
	        Long id,
	        ReportRequest request,
	        MultipartFile pdf) {

	    Report report =
	            reportRepository.findById(id).orElse(null);

	    if (report == null) {
	        return null;
	    }

	    if (!"DRAFT".equals(report.getReportLifecycle())) {
	        return report;
	    }

	    report.setClientId(request.getClientId());
	    report.setReportDate(request.getReportDate());
	    report.setReportTime(request.getReportTime());
	    report.setStatus(request.getStatus());
	    report.setPriority(request.getPriority());
	    report.setNotes(request.getNotes());
	    report.setDraftData(request.getDraftData());

	    if (request.getImageUrls() != null) {
	        report.setImageUrls(request.getImageUrls());
	    }

	    report.setVideoPath(request.getVideoPath());
	    report.setVideoUrl(request.getVideoUrl());

	    if (request.getVideoUrls() != null) {
	        report.setVideoUrls(request.getVideoUrls());
	    } else if (
	            request.getVideoUrl() != null &&
	            !request.getVideoUrl().isBlank()) {

	        report.setVideoUrls(
	                List.of(request.getVideoUrl())
	        );
	    }

	    /*
	     * If a new PDF is attached while editing
	     * the draft, replace the old PDF.
	     */
	    if (pdf != null && !pdf.isEmpty()) {

	        String oldPdfUrl = report.getPdfUrl();

	        try {
	            String newPdfUrl =
	                    cloudinaryService.uploadPdf(pdf);

	            report.setPdfUrl(newPdfUrl);

	            if (oldPdfUrl != null
	                    && !oldPdfUrl.isBlank()
	                    && !oldPdfUrl.equals(newPdfUrl)) {

	                try {
	                    cloudinaryService.deleteFile(oldPdfUrl);
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }

	        } catch (IOException e) {
	            throw new RuntimeException(
	                    "Failed to upload updated draft PDF",
	                    e
	            );
	        }
	    }

	    report.setReportLifecycle("DRAFT");

	    report.setUpdatedAt(
	            ZonedDateTime
	                    .now(ZoneId.of("Asia/Kolkata"))
	                    .toLocalDateTime()
	    );

	    return reportRepository.save(report);
	}
	
	
	@Override
	public Report submitDraft(
	        Long id,
	        ReportRequest request,
	        MultipartFile pdf) {

	    Report report =
	            reportRepository.findById(id).orElse(null);

	    if (report == null) {
	        return null;
	    }

	    if (!"DRAFT".equals(report.getReportLifecycle())) {
	        return report;
	    }

	    report.setClientId(request.getClientId());
	    report.setStatus(request.getStatus());
	    report.setPriority(request.getPriority());
	    report.setNotes(request.getNotes());

	    if (request.getImageUrls() != null) {
	        report.setImageUrls(request.getImageUrls());
	    }

	    report.setVideoPath(request.getVideoPath());
	    report.setVideoUrl(request.getVideoUrl());

	    if (request.getVideoUrls() != null) {
	        report.setVideoUrls(request.getVideoUrls());
	    } else if (
	            request.getVideoUrl() != null &&
	            !request.getVideoUrl().isBlank()) {

	        report.setVideoUrls(
	                List.of(request.getVideoUrl())
	        );
	    }

	    /*
	     * Replace the old PDF when a newer PDF
	     * is attached during final submission.
	     */
	    if (pdf != null && !pdf.isEmpty()) {

	        String oldPdfUrl = report.getPdfUrl();

	        try {
	            String newPdfUrl =
	                    cloudinaryService.uploadPdf(pdf);

	            report.setPdfUrl(newPdfUrl);

	            if (oldPdfUrl != null
	                    && !oldPdfUrl.isBlank()
	                    && !oldPdfUrl.equals(newPdfUrl)) {

	                try {
	                    cloudinaryService.deleteFile(oldPdfUrl);
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }

	        } catch (IOException e) {
	            throw new RuntimeException(
	                    "Failed to upload final report PDF",
	                    e
	            );
	        }
	    }

	    ZonedDateTime indiaNow =
	            ZonedDateTime.now(
	                    ZoneId.of("Asia/Kolkata")
	            );

	    /*
	     * Final submitted report gets the final
	     * submission date and time.
	     */
	    report.setCreatedAt(
	            indiaNow.toLocalDateTime()
	    );

	    report.setReportDate(
	            indiaNow.format(
	                    DateTimeFormatter.ofPattern(
	                            "dd-MM-yyyy",
	                            Locale.ENGLISH
	                    )
	            )
	    );

	    report.setReportTime(
	            indiaNow.format(
	                    DateTimeFormatter.ofPattern(
	                            "hh:mm a",
	                            Locale.ENGLISH
	                    )
	            )
	    );

	    /*
	     * The SAME draft row becomes submitted.
	     */
	    report.setReportLifecycle("SUBMITTED");

	    /*
	     * Draft data is no longer needed.
	     */
	    report.setDraftData(null);

	    report.setUpdatedAt(
	            indiaNow.toLocalDateTime()
	    );

	    return reportRepository.save(report);
	}
	
	
	
	// -------------------------------------------------------------------------------------------

	
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
        report.setImageUrls(request.getImageUrls());

        report.setVideoUrl(request.getVideoUrl());
        report.setVideoPath(request.getVideoPath());

        if (request.getVideoUrls() != null) {
            report.setVideoUrls(request.getVideoUrls());
        } else if (request.getVideoUrl() != null
                && !request.getVideoUrl().isBlank()) {
            report.setVideoUrls(List.of(request.getVideoUrl()));
        }

        return reportRepository.save(report);
    }
    
    //delete report.....................

    
	  
    @Override
    public void deleteReport(Long id) {
        Report report = reportRepository.findById(id).orElse(null);

        if (report == null) {
            return;
        }

        try {
            if (report.getImageUrls() != null) {
                for (String imageUrl : report.getImageUrls()) {
                    cloudinaryService.deleteFile(imageUrl);
                }
            }

            if (report.getVideoUrls() != null) {
                for (String videoUrl : report.getVideoUrls()) {
                    if (videoUrl != null && !videoUrl.isBlank()) {
                        cloudinaryService.deleteFile(videoUrl);
                    }
                }
            }

            // Delete legacy video URL if it is not already in videoUrls
            if (report.getVideoUrl() != null
                    && !report.getVideoUrl().isBlank()
                    && (report.getVideoUrls() == null
                        || !report.getVideoUrls().contains(report.getVideoUrl()))) {
                cloudinaryService.deleteFile(report.getVideoUrl());
            }

            if (report.getPdfUrl() != null
                    && !report.getPdfUrl().isBlank()) {
                cloudinaryService.deleteFile(report.getPdfUrl());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        reportRepository.delete(report);
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
    
    @Override
    public List<Report> getRecentReports() {

        return reportRepository
                .findTop5ByOrderByCreatedAtDesc();

    }
    
    @Override
    public Page<Report> getSubmittedReportsByClient(
            Long clientId,
            int page,
            int size) {

        return reportRepository
                .findByClientIdAndReportLifecycleOrderByCreatedAtDesc(
                        clientId,
                        "SUBMITTED",
                        PageRequest.of(page, size)
                );
    }
    
    @Override
    public Page<Report> getSubmittedReports(
            int page,
            int size) {

        return reportRepository
                .findByReportLifecycleOrderByCreatedAtDesc(
                        "SUBMITTED",
                        PageRequest.of(page, size)
                );
    }
}