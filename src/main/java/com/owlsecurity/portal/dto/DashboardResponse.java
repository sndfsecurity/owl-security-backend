package com.owlsecurity.portal.dto;

public class DashboardResponse {

    private long totalClients;
    private long totalReports;
    private long reportsToday;
    private long alerts;

    public DashboardResponse() {
    }

    public DashboardResponse(
            long totalClients,
            long totalReports,
            long reportsToday,
            long alerts) {

        this.totalClients = totalClients;
        this.totalReports = totalReports;
        this.reportsToday = reportsToday;
        this.alerts = alerts;
    }

    public long getTotalClients() {
        return totalClients;
    }

    public void setTotalClients(long totalClients) {
        this.totalClients = totalClients;
    }

    public long getTotalReports() {
        return totalReports;
    }

    public void setTotalReports(long totalReports) {
        this.totalReports = totalReports;
    }

    public long getReportsToday() {
        return reportsToday;
    }

    public void setReportsToday(long reportsToday) {
        this.reportsToday = reportsToday;
    }

    public long getAlerts() {
        return alerts;
    }

    public void setAlerts(long alerts) {
        this.alerts = alerts;
    }
}