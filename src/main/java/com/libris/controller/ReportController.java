package com.libris.controller;

import com.libris.dao.ReportDAO;
import com.libris.model.RentalOrder;
import com.libris.model.ReportStat;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ReportController {
    private ReportDAO reportDAO;

    public ReportController() {
        this.reportDAO = new ReportDAO();
    }

    public Map<String, Object> getDashboardStats() throws SQLException {
        return reportDAO.getDashboardStats();
    }

    public List<ReportStat> getMonthlyRevenue(int year) throws SQLException {
        return reportDAO.getMonthlyRevenue(year);
    }

    public List<ReportStat> getTopBooks(int limit) throws SQLException {
        return reportDAO.getTopBooks(limit);
    }

    public List<ReportStat> getCategoryStats() throws SQLException {
        return reportDAO.getCategoryStats();
    }

    public List<ReportStat> getTopCustomers(int limit) throws SQLException {
        return reportDAO.getTopCustomers(limit);
    }

    public List<RentalOrder> getOverdueBooks() throws SQLException {
        return reportDAO.getOverdueBooks();
    }
}