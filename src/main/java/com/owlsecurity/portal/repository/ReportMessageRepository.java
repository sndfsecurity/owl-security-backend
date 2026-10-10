
package com.owlsecurity.portal.repository;

import com.owlsecurity.portal.entity.ReportMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportMessageRepository
        extends JpaRepository<ReportMessage, Long> {

    List<ReportMessage> findByReportIdOrderByCreatedAtAsc(
            Long reportId
    );
    
    boolean existsByReportIdAndSenderRole(
            Long reportId,
            String senderRole
    );
}
