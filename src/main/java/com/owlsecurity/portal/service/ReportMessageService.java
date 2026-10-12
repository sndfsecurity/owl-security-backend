
package com.owlsecurity.portal.service;

import com.owlsecurity.portal.entity.ReportMessage;

import java.util.List;

public interface ReportMessageService {

    List<ReportMessage> getMessagesByReportId(Long reportId);

    ReportMessage sendMessage(Long reportId, String message);

    ReportMessageStatus getMessageStatus(Long reportId);

    class ReportMessageStatus {

        private long totalMessages;
        private long unreadMessages;

        public ReportMessageStatus() {
        }

        public ReportMessageStatus(
                long totalMessages,
                long unreadMessages) {
            this.totalMessages = totalMessages;
            this.unreadMessages = unreadMessages;
        }

        public long getTotalMessages() {
            return totalMessages;
        }

        public void setTotalMessages(long totalMessages) {
            this.totalMessages = totalMessages;
        }

        public long getUnreadMessages() {
            return unreadMessages;
        }

        public void setUnreadMessages(long unreadMessages) {
            this.unreadMessages = unreadMessages;
        }
    }
}
