package com.libris.view.interfaces;

import com.libris.model.RentalOrder;
import com.libris.model.ReportStat;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public interface IReportsView {
    void showMonthlyRevenue(List<ReportStat> stats);
    void showTopBooks(List<ReportStat> stats);
    void showCategoryStats(List<ReportStat> stats);
    void showTopCustomers(List<ReportStat> stats);
    void showError(String message);
    String getSelectedReportType();
    void addReportTypeChangeListener(ActionListener listener);
    void addExportExcelListener(ActionListener listener);
    String getPeriodFilter();
    void updateRightSummary(List<ReportStat> topBooks, List<ReportStat> topUsers, int overdueCount);
    void updateKPIs(double revenue, double deposit, int rentals, double onTimeRate);
    File getExportFile();
    void showOverdueOrders(List<ReportStat> chartStats, List<RentalOrder> orders);
    void handleExportExcel();
}
