package com.libris.view.interfaces;

import com.libris.model.Book;
import com.libris.model.RentalOrder;
import com.libris.model.ReportStat;
import java.util.List;
import java.util.Map;

public interface IDashboardView {
    void setStats(Map<String, Object> stats);
    void setTopBooks(List<Book> topBooks);
    void setTopBookStats(List<ReportStat> topBooks);
    void setRecentOrders(List<RentalOrder> recentOrders);
    void showError(String message);
    void updateCharts(List<ReportStat> revenueStats, List<ReportStat> categoryStats);
}
