package com.libris.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.libris.model.Book;
import com.libris.model.RentalOrderDetail;
import com.libris.model.RentalOrder;

import com.libris.utils.DatabaseConnection;

public class RentalOrderDAO {

    // Parse datetime
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
                System.err.println("⚠️ Không thể parse datetime: " + dateStr);
                return null;
            }
        }
    }

    // Lấy tất cả đơn thuê 
    public List<RentalOrder> getAllOrders() throws SQLException {
        List<RentalOrder> orders = new ArrayList<>();
        String query = "SELECT o.*, c.CustomerCode as CustomerCode, c.FullName as CustomerName FROM RentalOrders o " +
                   "LEFT JOIN Customers c ON o.CustomerID = c.CustomerID " +
                   "ORDER BY o.OrderID DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            while (rs.next()) {
                RentalOrder order = new RentalOrder();
                order.setOrderId(rs.getInt("OrderID"));
                order.setOrderCode(rs.getString("OrderCode"));
                order.setCustomerId(rs.getInt("CustomerID"));
                order.setCustomerCode(rs.getString("CustomerCode"));
                order.setCustomerName(rs.getString("CustomerName"));
                order.setRentDate(parseDateTime(rs.getString("RentDate")));
                order.setExpectedReturnDate(parseDateTime(rs.getString("ExpectedReturnDate")));
                order.setReturnDate(parseDateTime(rs.getString("ReturnDate")));
                order.setOrderStatus(rs.getString("OrderStatus"));
                order.setTotalDeposit(rs.getInt("TotalDeposit"));
                order.setTotalRentalFee(rs.getInt("TotalRentalFee"));
                order.setLateFee(rs.getInt("LateFee"));
                order.setTotalAmount(rs.getInt("TotalAmount"));
                //order.setDetails(getOrderDetails(order.getOrderId()));
                orders.add(order);
            }
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            if (stmt != null)
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                }
        }
        return orders;
    }

    public RentalOrder getOrderById(int orderId) throws SQLException {
        String query = "SELECT o.*, c.CustomerCode as CustomerCode, c.FullName as CustomerName FROM RentalOrders o " +
                   "LEFT JOIN Customers c ON o.CustomerID = c.CustomerID " +
                   "WHERE o.OrderID = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, orderId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                RentalOrder order = new RentalOrder();
                order.setOrderId(rs.getInt("OrderID"));
                order.setOrderCode(rs.getString("OrderCode"));
                order.setCustomerId(rs.getInt("CustomerID"));
                order.setCustomerName(rs.getString("CustomerName"));
                order.setCustomerCode(rs.getString("CustomerCode"));
                order.setRentDate(parseDateTime(rs.getString("RentDate")));
                order.setExpectedReturnDate(parseDateTime(rs.getString("ExpectedReturnDate")));
                order.setReturnDate(parseDateTime(rs.getString("ReturnDate")));
                order.setOrderStatus(rs.getString("OrderStatus"));
                order.setTotalDeposit(rs.getInt("TotalDeposit"));
                order.setTotalRentalFee(rs.getInt("TotalRentalFee"));
                order.setLateFee(rs.getInt("LateFee"));
                order.setTotalAmount(rs.getInt("TotalAmount"));
                order.setDetails(getOrderDetails(orderId));
                return order;
            }
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            if (stmt != null)
                try {
                    stmt.close();
                } catch (SQLException e) {
                }
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                }
        }
        return null;
    }

    public List<RentalOrderDetail> getOrderDetails(int orderId) throws SQLException {
        List<RentalOrderDetail> details = new ArrayList<>();
        String query = "SELECT d.*, b.* FROM RentalOrderDetails d " +
                     "JOIN Books b ON d.BookID = b.BookID " +
                     "WHERE d.OrderID = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(query);
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
                    book.setCategory(rs.getString("Category"));
                    book.setPublisher(rs.getString("Publisher"));
                    book.setPublishYear(rs.getInt("PublishYear"));
                    book.setBookStatus(rs.getString("BookStatus"));
                    book.setRentalPrice(rs.getInt("RentalPrice"));
                    book.setDepositPrice(rs.getInt("DepositPrice"));
                    
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

    // Thêm đơn thuê mới (có chi tiết)
    public boolean addOrder(RentalOrder order) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtOrder = null;
        PreparedStatement stmtDetail = null;
        ResultSet generatedKeys = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // bắt đầu transaction

            // 1. Chèn vào RentalOrders
            String query = "INSERT INTO RentalOrders (OrderCode, CustomerID, RentDate, ExpectedReturnDate, " +
                    "OrderStatus, TotalDeposit, TotalRentalFee, LateFee, TotalAmount) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmtOrder = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmtOrder.setString(1, order.getOrderCode());
            stmtOrder.setInt(2, order.getCustomerId());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            if (order.getRentDate() != null) {
                stmtOrder.setString(3, order.getRentDate().format(formatter));
            } else {
                stmtOrder.setString(3, LocalDateTime.now().format(formatter));
            }
            if (order.getExpectedReturnDate() != null) {
                stmtOrder.setString(4, order.getExpectedReturnDate().format(formatter));
            } else {
                stmtOrder.setString(4, LocalDateTime.now().plusDays(7).format(formatter));
            }

            stmtOrder.setString(5, order.getOrderStatus());
            stmtOrder.setInt(6, order.getTotalDeposit());
            stmtOrder.setInt(7, order.getTotalRentalFee());
            stmtOrder.setInt(8, order.getLateFee());
            stmtOrder.setInt(9, order.getTotalAmount());

            int affected = stmtOrder.executeUpdate();
            if (affected == 0) {
                conn.rollback();
                return false;
            }

            generatedKeys = stmtOrder.getGeneratedKeys();
            int orderId;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
                order.setOrderId(orderId);
            } else {
                conn.rollback();
                return false;
            }

            // 2. Chèn chi tiết đơn vào RentalOrderDetails
            String queryDetail = "INSERT INTO RentalOrderDetails (OrderID, BookID) VALUES (?, ?)";
            stmtDetail = conn.prepareStatement(queryDetail);
            for (RentalOrderDetail detail : order.getDetails()) {
                stmtDetail.setInt(1, orderId);
                stmtDetail.setInt(2, detail.getBookId());
                stmtDetail.addBatch();
            }
            stmtDetail.executeBatch();

            // 3. Cập nhật trạng thái sách thành 'Rented' cho các sách trong đơn
            String queryUpdateBook = "UPDATE Books SET BookStatus = 'Rented' WHERE BookID = ?";
            try (PreparedStatement stmtUpdate = conn.prepareStatement(queryUpdateBook)) {
                for (RentalOrderDetail detail : order.getDetails()) {
                    stmtUpdate.setInt(1, detail.getBookId());
                    stmtUpdate.addBatch();
                }
                stmtUpdate.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
            throw e;
        } finally {
            if (stmtOrder != null) try { stmtOrder.close(); } catch (SQLException e) {}
            if (stmtDetail != null) try { stmtDetail.close(); } catch (SQLException e) {}
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    // Cập nhật trạng thái đơn (Ví dụ: duyệt, trả sách)
    public boolean updateOrderStatus(int orderId, String newStatus, LocalDateTime returnDate) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Cập nhật order
            String query = "UPDATE RentalOrders SET OrderStatus = ?, ReturnDate = ? WHERE OrderID = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, newStatus);
            if (returnDate != null)
                stmt.setString(2, returnDate.toString());
            else
                stmt.setNull(2, Types.VARCHAR);
            stmt.setInt(3, orderId);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                conn.rollback();
                return false;
            }

            // Nếu trạng thái là 'Returned' thì cập nhật lại BookStatus thành 'Available'
            if ("Returned".equals(newStatus)) {
                // Lấy danh sách BookID trong đơn
                String queryGetBooks = "SELECT BookID FROM RentalOrderDetails WHERE OrderID = ?";
                try (PreparedStatement stmtGet = conn.prepareStatement(queryGetBooks)) {
                    stmtGet.setInt(1, orderId);
                    ResultSet rs = stmtGet.executeQuery();
                    String queryUpdateBook = "UPDATE Books SET BookStatus = 'Available' WHERE BookID = ?";
                    try (PreparedStatement stmtUpdate = conn.prepareStatement(queryUpdateBook)) {
                        while (rs.next()) {
                            stmtUpdate.setInt(1, rs.getInt("BookID"));
                            stmtUpdate.addBatch();
                        }
                        stmtUpdate.executeBatch();
                    }
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null)
                conn.rollback();
            throw e;
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }
}
