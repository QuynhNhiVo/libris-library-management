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
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) dateStr += " 00:00:00";
            return LocalDateTime.parse(dateStr.replace(" ", "T"));
        } catch (Exception e) {
            try { return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); } 
            catch (Exception ex) { return null; }
        }
    }

    // Thống kê tổng quan
    public Map<String, Object> getDashboardStats() throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT (SELECT COUNT(*) FROM Customers) as totalCustomers, " +
                "(SELECT COUNT(*) FROM RentalOrders WHERE OrderStatus = 'Pending') as pendingOrders, " +
                "(SELECT COUNT(*) FROM RentalOrders WHERE OrderStatus = 'Renting' AND ExpectedReturnDate < datetime('now')) as overdueBooks, " +
                "(SELECT COUNT(*) FROM RentalOrders WHERE OrderStatus = 'Renting') as rentingOrders";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                stats.put("totalCustomers", rs.getInt("totalCustomers"));
                stats.put("pendingOrders", rs.getInt("pendingOrders"));
                stats.put("overdueBooks", rs.getInt("overdueBooks"));
                stats.put("rentingOrders", rs.getInt("rentingOrders"));
            }
        }
        return stats;
    }

    // Doanh thu theo tháng
    public List<ReportStat> getMonthlyRevenue(int year) throws SQLException {
        Map<Integer, ReportStat> map = new HashMap<>();
        String sql = "SELECT CAST(strftime('%m', RentDate) AS INTEGER) as month, SUM(TotalAmount) as revenue, COUNT(OrderID) as orderCount " +
                "FROM RentalOrders WHERE CAST(strftime('%Y', RentDate) AS INTEGER) = ? AND OrderStatus = 'Returned' GROUP BY CAST(strftime('%m', RentDate) AS INTEGER)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year); ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ReportStat st = new ReportStat(); st.setDoubleValue(rs.getDouble("revenue")); st.setValue(rs.getInt("orderCount"));
                map.put(rs.getInt("month"), st);
            }
        }
        List<ReportStat> data = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            ReportStat st = map.getOrDefault(i, new ReportStat(String.valueOf(i), 0));
            st.setLabel(String.valueOf(i)); data.add(st);
        }
        return data;
    }

    // Top sách được thuê nhiều nhất
    public List<ReportStat> getTopBooks(int year, int limit) throws SQLException {
        List<ReportStat> data = new ArrayList<>();
        String sql = "SELECT b.BookCode, b.Title, b.Author, b.Category, COUNT(od.BookID) as rentCount, SUM(b.RentalPrice) as bookRev " +
                "FROM RentalOrderDetails od JOIN Books b ON od.BookID = b.BookID JOIN RentalOrders o ON od.OrderID = o.OrderID " +
                "WHERE CAST(strftime('%Y', o.RentDate) AS INTEGER) = ? GROUP BY od.BookID ORDER BY rentCount DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year); stmt.setInt(2, limit); ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ReportStat stat = new ReportStat(rs.getString("Title"), rs.getInt("rentCount"));
                stat.setDoubleValue(rs.getDouble("bookRev"));
                stat.setStringValue(rs.getString("BookCode") + "||" + rs.getString("Author") + "||" + rs.getString("Category"));
                data.add(stat);
            }
        }
        return data;
    }

    // Thống kê theo thể loại
    public List<ReportStat> getCategoryStats(int year) throws SQLException {
        List<ReportStat> allStats = new ArrayList<>();
        String sql = "SELECT b.Category, COUNT(od.BookID) as rentCount FROM Books b LEFT JOIN RentalOrderDetails od ON b.BookID = od.BookID " +
                "LEFT JOIN RentalOrders o ON od.OrderID = o.OrderID AND CAST(strftime('%Y', o.RentDate) AS INTEGER) = ? GROUP BY b.Category ORDER BY rentCount DESC";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year); ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ReportStat stat = new ReportStat(rs.getString("Category"), rs.getInt("rentCount"));
                stat.setDoubleValue(rs.getDouble("rentCount")); 
                allStats.add(stat);
            }
        }
        
        List<ReportStat> grouped = new ArrayList<>();
        int otherCount = 0;
        for (int i = 0; i < allStats.size(); i++) {
            if (i < 4) grouped.add(allStats.get(i));
            else otherCount += allStats.get(i).getValue();
        }
        if (otherCount > 0) {
            ReportStat other = new ReportStat("Khác", otherCount);
            other.setDoubleValue(otherCount); grouped.add(other);
        }
        return grouped;
    }

    // Khách hàng thuê nhiều nhất
    public List<ReportStat> getTopCustomers(int year, int limit) throws SQLException {
        List<ReportStat> data = new ArrayList<>();
        String sql = "SELECT c.FullName, COUNT(o.OrderID) as orderCount, SUM(o.TotalAmount) as totalSpent " +
                "FROM RentalOrders o JOIN Customers c ON o.CustomerID = c.CustomerID WHERE CAST(strftime('%Y', o.RentDate) AS INTEGER) = ? AND o.OrderStatus != 'Rejected' " +
                "GROUP BY o.CustomerID ORDER BY orderCount DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year); stmt.setInt(2, limit); ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ReportStat stat = new ReportStat(rs.getString("FullName"), rs.getInt("orderCount"));
                stat.setDoubleValue(rs.getDouble("totalSpent")); data.add(stat);
            }
        }
        return data;
    }

    // // Thống kê đơn Quá hạn
    public List<ReportStat> getOverdueStats(int year) throws SQLException {
        List<ReportStat> data = new ArrayList<>();
        String sql = "SELECT OrderStatus, ExpectedReturnDate, ReturnDate FROM RentalOrders WHERE CAST(strftime('%Y', RentDate) AS INTEGER) = ? AND OrderStatus IN ('Returned', 'Renting')";
        
        int dungHan = 0, chuaDenHan = 0, quaHan = 0;
        LocalDateTime now = LocalDateTime.now();

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String status = rs.getString("OrderStatus");
                LocalDateTime exp = parseDateTime(rs.getString("ExpectedReturnDate"));
                LocalDateTime ret = parseDateTime(rs.getString("ReturnDate"));
                
                if ("Returned".equalsIgnoreCase(status)) {
                    if (exp != null && ret != null && ret.isAfter(exp)) quaHan++;
                    else dungHan++;
                } else if ("Renting".equalsIgnoreCase(status)) {
                    if (exp != null && exp.isBefore(now)) quaHan++;
                    else chuaDenHan++;
                }
            }
        }
        
        data.add(new ReportStat("Đúng hạn", dungHan));
        data.add(new ReportStat("Chưa đến hạn", chuaDenHan));
        data.add(new ReportStat("Quá hạn / Vi phạm", quaHan));
        return data;
    }

    // Lấy danh sách chi tiết các đơn Quá hạn
    public List<RentalOrder> getOverdueBooks(int year) throws SQLException {
        List<RentalOrder> orders = new ArrayList<>();
        String sql = "SELECT o.*, c.FullName as CustomerName FROM RentalOrders o JOIN Customers c ON o.CustomerID = c.CustomerID " +
                     "WHERE CAST(strftime('%Y', o.RentDate) AS INTEGER) = ? AND o.OrderStatus IN ('Returned', 'Renting') ORDER BY o.RentDate DESC";
                     
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            try (ResultSet rs = stmt.executeQuery()) {
                LocalDateTime now = LocalDateTime.now();
                while (rs.next()) {
                    RentalOrder order = new RentalOrder();
                    order.setOrderId(rs.getInt("OrderID")); 
                    order.setOrderCode(rs.getString("OrderCode"));
                    order.setCustomerName(rs.getString("CustomerName")); 
                    order.setRentDate(parseDateTime(rs.getString("RentDate")));
                    
                    LocalDateTime exp = parseDateTime(rs.getString("ExpectedReturnDate"));
                    LocalDateTime ret = parseDateTime(rs.getString("ReturnDate"));
                    order.setExpectedReturnDate(exp);
                    order.setReturnDate(ret);
                    
                    String dbStatus = rs.getString("OrderStatus");
                    order.setOrderStatus(dbStatus); 
                    
                    String phanLoai = "Không xác định";
                    if ("Returned".equalsIgnoreCase(dbStatus)) {
                        if (exp != null && ret != null && ret.isAfter(exp)) phanLoai = "Quá hạn / Vi phạm";
                        else phanLoai = "Đúng hạn";
                    } else if ("Renting".equalsIgnoreCase(dbStatus)) {
                        if (exp != null && exp.isBefore(now)) phanLoai = "Quá hạn / Vi phạm";
                        else phanLoai = "Chưa đến hạn";
                    }
                    
                    order.setCustomerCode(phanLoai); // Tái sử dụng biến CustomerCode để truyền string Phân loại
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    //KPI
    public Map<String, Object> getReportKPIs(int year) throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT (SELECT SUM(TotalAmount) FROM RentalOrders WHERE OrderStatus = 'Returned' AND CAST(strftime('%Y', RentDate) AS INTEGER) = ?) as revenue, " +
            "(SELECT SUM(TotalDeposit) FROM RentalOrders WHERE OrderStatus IN ('Renting', 'Pending') AND CAST(strftime('%Y', RentDate) AS INTEGER) = ?) as deposit, " +
            "(SELECT COUNT(*) FROM RentalOrders WHERE OrderStatus IN ('Renting', 'Returned') AND CAST(strftime('%Y', RentDate) AS INTEGER) = ?) as totalRentals, " +
            "(SELECT COUNT(*) FROM RentalOrders WHERE OrderStatus = 'Returned' AND ReturnDate <= ExpectedReturnDate AND CAST(strftime('%Y', RentDate) AS INTEGER) = ?) as onTimeReturns, " +
            "(SELECT COUNT(*) FROM RentalOrders WHERE OrderStatus = 'Returned' AND CAST(strftime('%Y', RentDate) AS INTEGER) = ?) as finishedReturns";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year); stmt.setInt(2, year); stmt.setInt(3, year); stmt.setInt(4, year); stmt.setInt(5, year);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stats.put("revenue", rs.getDouble("revenue")); stats.put("deposit", rs.getDouble("deposit"));
                stats.put("totalRentals", rs.getInt("totalRentals"));
                int onTime = rs.getInt("onTimeReturns"), finished = rs.getInt("finishedReturns");
                stats.put("onTimeRate", finished > 0 ? (onTime * 100.0 / finished) : 100.0);
            }
        }
        return stats;
    }
}
