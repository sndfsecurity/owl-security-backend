
package com.owlsecurity.portal.service;

import com.owlsecurity.portal.entity.ReportMessage;

import java.util.List;

public interface ReportMessageService {

    List<ReportMessage> getMessagesByReportId(Long reportId);

    ReportMessage sendMessage(Long reportId, String message);
}
