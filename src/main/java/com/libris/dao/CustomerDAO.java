package com.libris.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.libris.model.Customer;
import com.libris.utils.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class CustomerDAO {
    
    /** Lấy danh sách tất cả khách hàng */
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM Customers ORDER BY CustomerID DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("CustomerID"));
                customer.setCustomerCode(rs.getString("CustomerCode"));
                customer.setName(rs.getString("FullName"));
                customer.setPhone(rs.getString("Phone"));
                customer.setAddress(rs.getString("Address"));
                customer.setEmail(rs.getString("Email"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách khách hàng: " + e.getMessage());
            throw e;
        }
        return customers;
    }

    /** Thêm khách hàng mới */
    public boolean addCustomer(Customer customer) throws SQLException {
        String query = "INSERT INTO Customers (CustomerCode, FullName, Phone, Address, Email) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, customer.getCustomerCode());
            stmt.setString(2, customer.getName());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getAddress());
            stmt.setString(5, customer.getEmail());
            return stmt.executeUpdate() > 0;
        }
    }

    /** Cập nhật thông tin khách hàng */
    public boolean updateCustomer(Customer customer) throws SQLException {
        String query = "UPDATE Customers SET CustomerCode = ?, FullName = ?, Phone = ?, Address = ?, Email = ? WHERE CustomerID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, customer.getCustomerCode());
            stmt.setString(2, customer.getName());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getAddress());
            stmt.setString(5, customer.getEmail());
            stmt.setInt(6, customer.getCustomerId());
            return stmt.executeUpdate() > 0;
        }
    }

    /** Xóa khách hàng */
    public boolean deleteCustomer(int customerId) throws SQLException {
        String query = "DELETE FROM Customers WHERE CustomerID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            return stmt.executeUpdate() > 0;
        }
    }

    /** Lấy thông tin khách hàng theo ID, Name */
    public List<Customer> searchCustomer(String keyword) throws SQLException {
        String query = "SELECT * FROM Customers WHERE CustomerID = ? OR FullName LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            try {
                // Thử convert sang int để tìm theo CustomerID
                int customerId = Integer.parseInt(keyword);
                stmt.setInt(1, customerId);
            } catch (NumberFormatException e) {
                // Nếu không phải số, set -1 (sẽ không có kết quả từ điều kiện ID)
                stmt.setInt(1, -1);
            }
            stmt.setString(2, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                List<Customer> customers = new ArrayList<>();
                while (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("CustomerID"));
                    customer.setCustomerCode(rs.getString("CustomerCode"));
                    customer.setName(rs.getString("FullName"));
                    customer.setPhone(rs.getString("Phone"));
                    customer.setAddress(rs.getString("Address"));
                    customer.setEmail(rs.getString("Email"));
                    customers.add(customer);
                }
                return customers;
            }
        }
    }
}
