package com.owlsecurity.portal.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.owlsecurity.portal.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportRepository extends JpaRepository<Report, Long> {
	
	   List<Report> findByClientId(Long clientId);
	
	
	   long countByReportDate(String reportDate);

	    long countByPriority(String priority);
	    
	    List<Report> findByClientIdOrderByCreatedAtDesc(Long clientId);

	    Long countByClientId(Long clientId);
	    
	    List<Report> findByReportDate(
	            String reportDate
	    );
	    
	    List<Report> findByClientIdAndReportDate(
	            Long clientId,
	            String reportDate
	    );
	    
	    List<Report> findAllByOrderByCreatedAtDesc();
	    
	    
	    List<Report> findByCreatedAtBetween(
	            LocalDateTime start,
	            LocalDateTime end
	    );

	    List<Report> findByClientIdAndCreatedAtBetween(
	            Long clientId,
	            LocalDateTime start,
	            LocalDateTime end
	    );
	    
	    Page<Report> findAllByOrderByCreatedAtDesc(
	            Pageable pageable
	    );

	    Page<Report> findByCreatedAtBetween(
	            LocalDateTime start,
	            LocalDateTime end,
	            Pageable pageable
	    );

	    Page<Report> findByClientIdAndCreatedAtBetween(
	            Long clientId,
	            LocalDateTime start,
	            LocalDateTime end,
	            Pageable pageable
	    );
	    
	    Page<Report> findByClientIdOrderByCreatedAtDesc(
	            Long clientId,
	            Pageable pageable
	    );

}