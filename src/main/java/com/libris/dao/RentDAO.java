package com.libris.dao;

import com.libris.model.Book;
import com.libris.model.RentalOrder;
import com.libris.model.RentalOrderDetail;
import com.libris.utils.DatabaseConnection;

import com.libris.model.RentRequest;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RentDAO {
    
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

    // Lấy sách đang có sẵn
    public List<Book> getAvailableBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM Books WHERE BookStatus = 'Available'";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Book book = new Book();
                book.setBookid(rs.getInt("BookID"));
                book.setBookCode(rs.getString("BookCode"));
                book.setTitle(rs.getString("Title"));
                book.setAuthor(rs.getString("Author"));
                book.setCategory(rs.getString("Category"));
                book.setPublisher(rs.getString("Publisher"));
                book.setPublishYear(rs.getInt("PublishYear"));
                book.setBookStatus(rs.getString("BookStatus"));
                book.setRentalPrice(rs.getInt("RentalPrice"));
                book.setDepositPrice(rs.getInt("DepositPrice"));
                books.add(book);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
        return books;
    }

    // Lấy sách đang thuê của khách hàng
    public List<RentalOrder> getCustomerRentals(int customerId) throws SQLException {
        List<RentalOrder> orders = new ArrayList<>();
        String sql = "SELECT o.* FROM RentalOrders o " +
                    "WHERE o.CustomerID = ? AND o.OrderStatus IN ('Pending', 'Renting') " +
                    "ORDER BY o.RentDate DESC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                RentalOrder order = new RentalOrder();
                order.setOrderId(rs.getInt("OrderID"));
                order.setOrderCode(rs.getString("OrderCode"));
                order.setCustomerId(rs.getInt("CustomerID"));
                order.setRentDate(parseDateTime(rs.getString("RentDate")));
                order.setExpectedReturnDate(parseDateTime(rs.getString("ExpectedReturnDate")));
                order.setReturnDate(parseDateTime(rs.getString("ReturnDate")));
                order.setOrderStatus(rs.getString("OrderStatus"));
                order.setTotalAmount(rs.getInt("TotalAmount"));
                
                // Lấy chi tiết sách
                order.setDetails(getOrderDetails(order.getOrderId()));
                orders.add(order);
            }
        } finally { 
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
        return orders;
    }

    // Lấy chi tiết đơn hàng
    private List<RentalOrderDetail> getOrderDetails(int orderId) throws SQLException {
        List<RentalOrderDetail> details = new ArrayList<>();
        String sql = "SELECT d.*, b.* FROM RentalOrderDetails d " +
                    "JOIN Books b ON d.BookID = b.BookID " +
                    "WHERE d.OrderID = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, orderId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                RentalOrderDetail detail = new RentalOrderDetail();
                detail.setOrderDetailId(rs.getInt("OrderDetailID"));
                detail.setOrderId(rs.getInt("OrderID"));
                detail.setBookId(rs.getInt("BookID"));
                
                Book book = new Book();
                book.setBookid(rs.getInt("BookID"));
                book.setBookCode(rs.getString("BookCode"));
                book.setTitle(rs.getString("Title"));
                book.setAuthor(rs.getString("Author"));
                book.setBookStatus(rs.getString("BookStatus"));
                detail.setBook(book);
                
                details.add(detail);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
        return details;
    }

    // Tạo yêu cầu thuê sách
    public boolean createRentRequest(RentRequest request) throws SQLException {
        // Tạo đơn thuê mới
        RentalOrder order = new RentalOrder();
        order.setOrderCode("REQ" + System.currentTimeMillis());
        order.setCustomerId(request.getCustomerId());
        order.setRentDate(LocalDateTime.now());
        order.setExpectedReturnDate(request.getExpectedReturnDate());
        order.setOrderStatus("Pending");
        order.setTotalAmount(request.getBooks().size() * 10000); // Giá mặc định
        
        // Tạo chi tiết
        for (Book book : request.getBooks()) {
            RentalOrderDetail detail = new RentalOrderDetail();
            detail.setBookId(book.getBookid());
            detail.setBook(book);
            order.getDetails().add(detail);
        }
        
        RentalOrderDAO orderDAO = new RentalOrderDAO();
        return orderDAO.addOrder(order);
    }

    public boolean returnBooks(int orderId) throws SQLException {
        RentalOrderDAO orderDAO = new RentalOrderDAO();
        return orderDAO.updateOrderStatus(orderId, "Returned", LocalDateTime.now());
    }
}