package com.libris.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.libris.model.User;
import com.libris.utils.DatabaseConnection;

public class UserDAO {
    /** Login: Xác thực người dùng */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT u.*, c.FullName, c.Email, c.Phone " +
                     "FROM Users u LEFT JOIN Customers c ON u.CustomerID = c.CustomerID " +
                     "WHERE u.Username = ? AND u.Password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("UserID"));
                    user.setUsername(rs.getString("Username"));
                    user.setPassword(rs.getString("Password"));
                    user.setRole(rs.getString("Role"));

                    int customerId = rs.getInt("CustomerID");
                    if (!rs.wasNull()) {
                        user.setCustomerId(customerId);
                        user.setFullName(rs.getString("FullName"));
                        user.setEmail(rs.getString("Email"));
                        user.setPhone(rs.getString("Phone"));
                    } else {
                        user.setCustomerId(null);
                    }
                    return user;
                }
            }
        }
        return null;
    }

    public User getUserById(int userId) {
        String sql = "SELECT u.*, c.FullName, c.Email, c.Phone " +
                "FROM Users u LEFT JOIN Customers c ON u.CustomerID = c.CustomerID " +
                "WHERE u.UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("UserID"));
                    user.setUsername(rs.getString("Username"));
                    user.setPassword(rs.getString("Password"));
                    user.setRole(rs.getString("Role"));

                    int customerId = rs.getInt("CustomerID");
                    if (!rs.wasNull()) {
                        user.setCustomerId(customerId);
                        user.setFullName(rs.getString("FullName"));
                        user.setEmail(rs.getString("Email"));
                        user.setPhone(rs.getString("Phone"));
                    } else {
                        user.setCustomerId(null);
                    }
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserInfo(int userId, String newUsername) {
        String sql = "UPDATE Users SET Username = ? WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newUsername);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProfileDetails(int userId, String fullName, String email, String phone) {
        String getCustIdSql = "SELECT CustomerID FROM Users WHERE UserID = ?";
        String updateCustSql = "UPDATE Customers SET FullName = ?, Email = ?, Phone = ? WHERE CustomerID = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            Integer customerId = null;
            try (PreparedStatement stmt1 = conn.prepareStatement(getCustIdSql)) {
                stmt1.setInt(1, userId);
                try (ResultSet rs = stmt1.executeQuery()) {
                    if (rs.next()) {
                        customerId = (Integer) rs.getObject("CustomerID");
                    }
                }
            }

            // Chỉ cập nhật nếu tài khoản có liên kết với bảng Customers
            if (customerId != null) {
                try (PreparedStatement stmt2 = conn.prepareStatement(updateCustSql)) {
                    stmt2.setString(1, fullName);
                    stmt2.setString(2, email);
                    stmt2.setString(3, phone);
                    stmt2.setInt(4, customerId);
                    stmt2.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        try {
            User user = getUserById(userId);
            if (user == null) {
                return false;
            }

            if (!user.getPassword().equals(currentPassword)) {
                return false; // Mật khẩu hiện tại không khớp
            }

            return updatePassword(userId, newPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE Users SET Password = ? WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
