
package com.owlsecurity.portal.service.impl;

import com.owlsecurity.portal.entity.Report;
import com.owlsecurity.portal.entity.ReportMessage;
import com.owlsecurity.portal.repository.ReportMessageRepository;
import com.owlsecurity.portal.repository.ReportRepository;
import com.owlsecurity.portal.repository.UserRepository;
import com.owlsecurity.portal.security.ClientAccessService;
import com.owlsecurity.portal.service.ReportMessageService;
import com.owlsecurity.portal.service.WebPushNotificationService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.owlsecurity.portal.service.ReportMessageService.ReportMessageStatus;

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

    // Get messages and mark messages from the other person as read.
    @Override
    @Transactional
    public List<ReportMessage> getMessagesByReportId(Long reportId) {

        Report report = getAccessibleReport(reportId);

        List<ReportMessage> messages =
                messageRepository.findByReportIdOrderByCreatedAtAsc(
                        report.getId());

        boolean admin = clientAccessService.isAdmin();
        boolean changed = false;

        for (ReportMessage message : messages) {
            if (admin && !message.isReadByAdmin()) {
                message.setReadByAdmin(true);
                changed = true;
            } else if (!admin && !message.isReadByClient()) {
                message.setReadByClient(true);
                changed = true;
            }
        }

        if (changed) {
            messageRepository.saveAll(messages);
        }

        return messages;
    }

    // Send a message and mark it read for its sender,
    // but unread for the recipient.
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

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null) {
            throw new AccessDeniedException("Authentication required");
        }

        boolean admin = clientAccessService.isAdmin();

        if (admin &&
                !messageRepository.existsByReportIdAndSenderRole(
                        report.getId(), "CLIENT")) {

            throw new AccessDeniedException(
                    "Admin can reply only after the client sends the first message");
        }

        ReportMessage reportMessage = new ReportMessage();

        reportMessage.setReportId(report.getId());
        reportMessage.setClientId(report.getClientId());
        reportMessage.setSenderRole(admin ? "ADMIN" : "CLIENT");
        reportMessage.setSenderUserId(
                resolveSenderUserId(authentication));
        reportMessage.setMessage(message.trim());
        reportMessage.setCreatedAt(
                LocalDateTime.now(ZoneId.of("Asia/Kolkata")));

        // Sender has already seen their own message.
        reportMessage.setReadByAdmin(admin);
        reportMessage.setReadByClient(!admin);

        ReportMessage savedMessage =
                messageRepository.save(reportMessage);

        try {
            if (admin) {
                webPushNotificationService
                        .sendAdminReplyNotification(report);
            } else {
                webPushNotificationService
                        .sendClientMessageNotification(report);
            }
        } catch (Exception e) {
            System.err.println(
                    "Message notification failed for report "
                            + report.getId() + ": " + e.getMessage());
        }

        return savedMessage;
    }
    
    

		@Override
		@Transactional(readOnly = true)
		public ReportMessageStatus getMessageStatus(Long reportId) {
		
		    Report report = getAccessibleReport(reportId);
		
		    boolean admin = clientAccessService.isAdmin();
		
		    long totalMessages =
		            messageRepository.countByReportId(report.getId());
		
		    long unreadMessages = admin
		            ? messageRepository
		                .countByReportIdAndSenderRoleNotAndReadByAdminFalse(
		                    report.getId(), "ADMIN")
		            : messageRepository
		                .countByReportIdAndSenderRoleNotAndReadByClientFalse(
		                    report.getId(), "CLIENT");
		
		    return new ReportMessageStatus(totalMessages, unreadMessages);
		}


    private Report getAccessibleReport(Long reportId) {

        if (reportId == null) {
            throw new IllegalArgumentException("Report ID is required");
        }

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Report not found"));

        boolean admin = clientAccessService.isAdmin();

        if (!admin) {
            clientAccessService.verifyClientAccess(report.getClientId());
        }

        return report;
    }

    private Long resolveSenderUserId(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new AccessDeniedException("User not found"))
                .getId();
    }
}
