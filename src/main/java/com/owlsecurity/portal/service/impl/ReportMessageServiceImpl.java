
package com.owlsecurity.portal.service.impl;

import com.owlsecurity.portal.entity.Report;
import com.owlsecurity.portal.entity.ReportMessage;
import com.owlsecurity.portal.repository.ReportMessageRepository;
import com.owlsecurity.portal.repository.ReportRepository;
import com.owlsecurity.portal.security.ClientAccessService;
import com.owlsecurity.portal.service.ReportMessageService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.owlsecurity.portal.repository.UserRepository;

import com.owlsecurity.portal.service.WebPushNotificationService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class ReportMessageServiceImpl implements ReportMessageService {

    private final ReportMessageRepository messageRepository;
    private final ReportRepository reportRepository;
    private final ClientAccessService clientAccessService;
    private final UserRepository userRepository;
    private final WebPushNotificationService webPushNotificationService;

    public ReportMessageServiceImpl(
            ReportMessageRepository messageRepository,
            ReportRepository reportRepository,
            UserRepository userRepository,
            ClientAccessService clientAccessService,
            WebPushNotificationService webPushNotificationService) {

        this.messageRepository = messageRepository;
        this.reportRepository = reportRepository;
        this.clientAccessService = clientAccessService;
        this.userRepository = userRepository;
        this.webPushNotificationService = webPushNotificationService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportMessage> getMessagesByReportId(Long reportId) {
        Report report = getAccessibleReport(reportId);
        return messageRepository.findByReportIdOrderByCreatedAtAsc(
                report.getId());
    }

    @Override
    @Transactional
    public ReportMessage sendMessage(Long reportId, String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        if (message.length() > 5000) {
            throw new IllegalArgumentException(
                    "Message cannot exceed 5000 characters");
        }

        Report report = getAccessibleReport(reportId);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName() == null) {
            throw new AccessDeniedException("Authentication required");
        }

        boolean admin = clientAccessService.isAdmin();
        

        if (admin &&
                !messageRepository.existsByReportIdAndSenderRole(
                        report.getId(), "CLIENT")) {

            throw new org.springframework.security.access.AccessDeniedException(
                    "Admin can reply only after the client sends the first message"
            );
        }

        ReportMessage reportMessage = new ReportMessage();
        reportMessage.setReportId(report.getId());
        reportMessage.setClientId(report.getClientId());
        reportMessage.setSenderRole(admin ? "ADMIN" : "CLIENT");
        reportMessage.setSenderUserId(resolveSenderUserId(authentication));
        reportMessage.setMessage(message.trim());
        reportMessage.setCreatedAt(
                LocalDateTime.now(ZoneId.of("Asia/Kolkata")));


			ReportMessage savedMessage = messageRepository.save(reportMessage);
			
			try {
			    if (admin) {
			        webPushNotificationService.sendAdminReplyNotification(report);
			    } else {
			        webPushNotificationService.sendClientMessageNotification(report);
			    }
			} catch (Exception e) {
			    System.err.println(
			            "Message notification failed for report "
			                    + report.getId() + ": " + e.getMessage()
			    );
			}
			
			return savedMessage;

    }
    
    

    private Report getAccessibleReport(Long reportId) {

        System.out.println("Message API: reportId = " + reportId);

        if (reportId == null) {
            throw new IllegalArgumentException("Report ID is required");
        }

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Report not found"));

        System.out.println("Report found: " + report.getId());
        System.out.println("Report clientId: " + report.getClientId());
        System.out.println("Report lifecycle: " + report.getReportLifecycle());

        boolean admin = clientAccessService.isAdmin();

        System.out.println("Logged-in user is admin: " + admin);

        if (!admin) {
            System.out.println("Checking client access...");
            clientAccessService.verifyClientAccess(report.getClientId());
            System.out.println("Client access verified");
        }

        return report;
    }

		private Long resolveSenderUserId(Authentication authentication) {
		    return userRepository.findByEmail(authentication.getName())
		            .orElseThrow(() ->
		                    new org.springframework.security.access.AccessDeniedException(
		                            "User not found"))
		            .getId();
		}

}
