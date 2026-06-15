package com.owlsecurity.portal.dto;

public class ClientDashboardResponse {

    private String companyName;
    private Long totalReports;
    private String latestStatus;

    public ClientDashboardResponse(
            String companyName,
            Long totalReports,
            String latestStatus
    ) {
        this.companyName = companyName;
        this.totalReports = totalReports;
        this.latestStatus = latestStatus;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Long getTotalReports() {
        return totalReports;
    }

    public String getLatestStatus() {
        return latestStatus;
    }
}
