
package com.owlsecurity.portal.controller;

import com.owlsecurity.portal.entity.ReportMessage;
import com.owlsecurity.portal.service.ReportMessageService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports/{reportId}/messages")
public class ReportMessageController {

    private final ReportMessageService reportMessageService;

    public ReportMessageController(
            ReportMessageService reportMessageService) {
        this.reportMessageService = reportMessageService;
    }

    // Get all messages for a specific report
    @GetMapping
    public List<ReportMessage> getMessages(
            @PathVariable Long reportId) {

        return reportMessageService.getMessagesByReportId(reportId);
    }

    // Send a message for a specific report
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReportMessage sendMessage(
            @PathVariable Long reportId,
            @RequestBody MessageRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Message request cannot be empty");
        }

        return reportMessageService.sendMessage(
                reportId,
                request.getMessage());
    }

    // Request body DTO
    public static class MessageRequest {

        private String message;

        public MessageRequest() {
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
