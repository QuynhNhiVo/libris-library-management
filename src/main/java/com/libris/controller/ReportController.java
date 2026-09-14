package com.libris.controller;

import com.libris.dao.ReportDAO;
import com.libris.model.RentalOrder;
import com.libris.model.ReportStat;
import com.libris.view.interfaces.IDashboardView;
import com.libris.view.interfaces.IReportsView;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ReportController {
    private ReportDAO reportDAO;
    private IDashboardView dashboardView;
    private IReportsView reportsView;

    public ReportController() {
        this.reportDAO = new ReportDAO();
        
    }

    public ReportController(ReportDAO reportDAO) {
        this.reportDAO = reportDAO != null ? reportDAO : new ReportDAO();
    }

    public ReportController(IDashboardView view, ReportDAO reportDAO) {
        this.dashboardView = view;
        this.reportDAO = reportDAO != null ? reportDAO : new ReportDAO();
        if (this.dashboardView != null) {
            loadDashboardData();
        }
    }

    public ReportController(IReportsView view, ReportDAO reportDAO) {
        this.reportsView = view;
        this.reportDAO = reportDAO != null ? reportDAO : new ReportDAO();
        if (this.reportsView != null) {
            this.reportsView.addReportTypeChangeListener(e -> loadReportData());
            this.reportsView.addExportExcelListener(e -> {
                ((com.libris.view.ReportsView)reportsView).handleExportExcel();
                handleExportExcel();
            });
            loadReportData();
        }
    }

    private int extractYear(String periodStr) {
        if (periodStr == null || periodStr.isEmpty()) return 2026;
        String num = periodStr.replaceAll("\\D+", ""); // Trích xuất chữ số (Năm)
        return num.isEmpty() ? 2026 : Integer.parseInt(num);
    }

    private void handleExportExcel() {
        if (reportsView == null || reportsView.getExportFile() == null) return;
        try {
            int year = extractYear(reportsView.getPeriodFilter());
            boolean ok = com.libris.utils.ExcelExporter.exportFullReport(reportsView.getExportFile(), year,
                    reportDAO.getMonthlyRevenue(year), reportDAO.getTopBooks(year, 100),
                    reportDAO.getCategoryStats(year), reportDAO.getTopCustomers(year, 100), 
                    reportDAO.getOverdueBooks(year));
            if (ok) reportsView.showError("Xuất toàn bộ 5 Sheet báo cáo ra Excel thành công!");
            else reportsView.showError("Lỗi quá trình ghi file Excel!");
        } catch (Exception e) { 
            reportsView.showError("Lỗi xuất Excel: " + e.getMessage()); 
        }
    }

    public void loadDashboardData() {
        if (dashboardView == null) return;
        try {
            Map<String, Object> stats = reportDAO.getDashboardStats();
            dashboardView.setStats(stats);
            dashboardView.setTopBookStats(reportDAO.getTopBooks(java.time.Year.now().getValue(), 5));
            List<ReportStat> revenueStats = reportDAO.getMonthlyRevenue(java.time.Year.now().getValue());
            List<ReportStat> categoryStats = reportDAO.getCategoryStats(java.time.Year.now().getValue());
            dashboardView.updateCharts(revenueStats, categoryStats);
        } catch (Exception e) {
            dashboardView.showError("Lỗi tải Tổng quan: " + e.getMessage());
        }
    }

    public void loadReportData() {
        if (reportsView == null) return;
        try {
            String type = reportsView.getSelectedReportType();
            int year = extractYear(reportsView.getPeriodFilter());

            // 1. Cập nhật 4 KPI Cards phía trên
            Map<String, Object> kpis = reportDAO.getReportKPIs(year);
            double rev = kpis.get("revenue") != null ? (Double)kpis.get("revenue") : 0;
            double dep = kpis.get("deposit") != null ? (Double)kpis.get("deposit") : 0;
            int rent = kpis.get("totalRentals") != null ? (Integer)kpis.get("totalRentals") : 0;
            double rate = kpis.get("onTimeRate") != null ? (Double)kpis.get("onTimeRate") : 0;
            reportsView.updateKPIs(rev, dep, rent, rate);

            // 2. Cập nhật Panel Tổng hợp cố định bên phải
            reportsView.updateRightSummary(
                    reportDAO.getTopBooks(year, 3), 
                    reportDAO.getTopCustomers(year, 3), 
                    reportDAO.getOverdueStats(year).stream().filter(s -> s.getLabel().contains("Quá hạn")).mapToInt(ReportStat::getValue).sum()
            );

            // 3. Cập nhật Biểu đồ & Table dựa theo loại Báo cáo đã chọn
            if (type == null || type.contains("Doanh thu")) {
                reportsView.showMonthlyRevenue(reportDAO.getMonthlyRevenue(year));
            } else if (type.contains("Tần suất") || type.contains("Xu hướng")) {
                reportsView.showTopBooks(reportDAO.getTopBooks(year, 10)); 
            } else if (type.contains("Độc giả") || type.contains("tích cực")) {
                reportsView.showTopCustomers(reportDAO.getTopCustomers(year, 10)); 
            } else if (type.contains("Quá hạn") || type.contains("Vi phạm")) {
                reportsView.showOverdueOrders(reportDAO.getOverdueStats(year), reportDAO.getOverdueBooks(year));
            }
        } catch (Exception e) {
            reportsView.showError("Lỗi khi tải Báo cáo: " + e.getMessage());
        }
    }

    // Retain legacy direct methods for existing callers/tests
    public Map<String, Object> getDashboardStats() throws SQLException {
        return reportDAO.getDashboardStats();
    }
    public List<ReportStat> getMonthlyRevenue(int year) throws SQLException {
        return reportDAO.getMonthlyRevenue(year);
    }
    public List<ReportStat> getTopBooks(int limit) throws SQLException {
        return reportDAO.getTopBooks(java.time.Year.now().getValue(), limit);
    }
    public List<ReportStat> getCategoryStats() throws SQLException {
        return reportDAO.getCategoryStats(java.time.Year.now().getValue());
    }
    public List<ReportStat> getTopCustomers(int limit) throws SQLException {
        return reportDAO.getTopCustomers(java.time.Year.now().getValue(), limit);
    }
    public List<RentalOrder> getOverdueBooks(int year) throws SQLException {
        return reportDAO.getOverdueBooks(year);
    }
}
