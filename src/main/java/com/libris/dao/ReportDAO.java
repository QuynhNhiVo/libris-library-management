package com.libris.dao;

import com.libris.model.RentalOrder;
import com.libris.model.ReportStat;
import com.libris.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO {

    // Helper parse datetime
    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                dateStr += " 00:00:00";
            }
            return LocalDateTime.parse(dateStr.replace(" ", "T"));
        } catch (Exception e) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return LocalDateTime.parse(dateStr, formatter);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    // Thống kê tổng quan
    public Map<String, Object> getDashboardStats() throws SQLException {
        Map<String, Object> stats = new HashMap<>();

        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM Books) as totalBooks, " +
                "(SELECT COUNT(*) FROM Customers) as totalCustomers, " +
                "(SELECT COUNT(*) FROM RentalOrders WHERE OrderStatus = 'Renting') as rentingOrders, " +
                "(SELECT COUNT(*) FROM RentalOrders WHERE OrderStatus = 'Returned') as returnedOrders, " +
                "(SELECT SUM(TotalAmount) FROM RentalOrders WHERE OrderStatus = 'Returned' " +
                "AND CAST(strftime('%m', RentDate) AS INTEGER) = CAST(strftime('%m', date('now')) AS INTEGER) " +
                "AND CAST(strftime('%Y', RentDate) AS INTEGER) = CAST(strftime('%Y', date('now')) AS INTEGER)) as monthlyRevenue";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                stats.put("totalBooks", rs.getInt("totalBooks"));
                stats.put("totalCustomers", rs.getInt("totalCustomers"));
                stats.put("rentingOrders", rs.getInt("rentingOrders"));
                stats.put("returnedOrders", rs.getInt("returnedOrders"));
                stats.put("monthlyRevenue", rs.getInt("monthlyRevenue"));
            }
        }
        return stats;
    }

    // Doanh thu theo tháng
    public List<ReportStat> getMonthlyRevenue(int year) throws SQLException {
        java.util.Map<Integer, Double> revenueMap = new java.util.HashMap<>();
        String sql = "SELECT CAST(strftime('%m', RentDate) AS INTEGER) as month, SUM(TotalAmount) as revenue " +
                "FROM RentalOrders " +
                "WHERE CAST(strftime('%Y', RentDate) AS INTEGER) = ? AND OrderStatus = 'Returned' " +
                "GROUP BY CAST(strftime('%m', RentDate) AS INTEGER)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                revenueMap.put(rs.getInt("month"), rs.getDouble("revenue"));
            }
        }
        List<ReportStat> data = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            double rev = revenueMap.getOrDefault(i, 0.0);
            data.add(new ReportStat("" + i, rev));
        }
        return data;
    }

    // Top sách được thuê nhiều nhất
    public List<ReportStat> getTopBooks(int limit) throws SQLException {
        List<ReportStat> data = new ArrayList<>();
        String sql = "SELECT b.Title, COUNT(od.BookID) as rentCount " +
                "FROM RentalOrderDetails od " +
                "JOIN Books b ON od.BookID = b.BookID " +
                "GROUP BY od.BookID " +
                "ORDER BY rentCount DESC " +
                "LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                data.add(new ReportStat(rs.getString("Title"), rs.getInt("rentCount")));
            }
        }
        return data;
    }

    // Thống kê theo thể loại
    public List<ReportStat> getCategoryStats() throws SQLException {
        List<ReportStat> data = new ArrayList<>();
        String sql = "SELECT b.Category, COUNT(od.BookID) as count " +
                "FROM RentalOrderDetails od " +
                "JOIN Books b ON od.BookID = b.BookID " +
                "GROUP BY b.Category " +
                "ORDER BY count DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                data.add(new ReportStat(rs.getString("Category"), rs.getInt("count")));
            }
        }
        return data;
    }

    // Khách hàng thuê nhiều nhất
    public List<ReportStat> getTopCustomers(int limit) throws SQLException {
        List<ReportStat> data = new ArrayList<>();
        String sql = "SELECT c.FullName, COUNT(o.OrderID) as orderCount " +
                "FROM RentalOrders o " +
                "JOIN Customers c ON o.CustomerID = c.CustomerID " +
                "GROUP BY o.CustomerID " +
                "ORDER BY orderCount DESC " +
                "LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                data.add(new ReportStat(rs.getString("FullName"), rs.getInt("orderCount")));
            }
        }
        return data;
    }

    // Sách quá hạn
    public List<RentalOrder> getOverdueBooks() throws SQLException {
        List<RentalOrder> orders = new ArrayList<>();
        String sql = "SELECT o.*, c.FullName as CustomerName " +
                "FROM RentalOrders o " +
                "JOIN Customers c ON o.CustomerID = c.CustomerID " +
                "WHERE o.OrderStatus = 'Renting' " +
                "AND o.ExpectedReturnDate < datetime('now')";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                RentalOrder order = new RentalOrder();
                order.setOrderId(rs.getInt("OrderID"));
                order.setOrderCode(rs.getString("OrderCode"));
                order.setCustomerName(rs.getString("CustomerName"));
                order.setRentDate(parseDateTime(rs.getString("RentDate")));
                order.setExpectedReturnDate(parseDateTime(rs.getString("ExpectedReturnDate")));
                order.setOrderStatus(rs.getString("OrderStatus"));
                order.setTotalAmount(rs.getInt("TotalAmount"));
                orders.add(order);
            }
        }
        return orders;
    }
}