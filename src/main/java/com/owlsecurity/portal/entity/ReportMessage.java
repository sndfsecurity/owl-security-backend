
package com.owlsecurity.portal.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "report_messages")
public class ReportMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reportId;

    @Column(nullable = false)
    private Long clientId;

    @Column(nullable = false)
    private Long senderUserId;

    @Column(nullable = false, length = 20)
    private String senderRole;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private LocalDateTime createdAt =
            LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean readByAdmin = true;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean readByClient = true;

    public ReportMessage() {
    }

    public Long getId() {
        return id;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(Long senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isReadByAdmin() {
        return readByAdmin;
    }

    public void setReadByAdmin(boolean readByAdmin) {
        this.readByAdmin = readByAdmin;
    }

    public boolean isReadByClient() {
        return readByClient;
    }

    public void setReadByClient(boolean readByClient) {
        this.readByClient = readByClient;
    }
}
