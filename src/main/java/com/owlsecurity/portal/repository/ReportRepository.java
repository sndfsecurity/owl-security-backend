package com.owlsecurity.portal.repository;

import java.time.LocalDate;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.owlsecurity.portal.entity.Report;


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

}