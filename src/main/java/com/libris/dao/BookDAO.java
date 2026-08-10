package com.libris.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.libris.utils.DatabaseConnection;
import com.libris.model.Book;


import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class BookDAO {

    /** Lấy tất cả sách */
    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM Books ORDER BY BookID DESC";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {conn = DatabaseConnection.getConnection();
                stmt = conn.prepareStatement(query);
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
        }
        return books;
    }

    /** Thêm sách mới */
    public boolean addBook(Book book) throws SQLException {
        String query = "INSERT INTO Books (BookCode, Title, Author, Category, Publisher, PublishYear, BookStatus, RentalPrice, DepositPrice) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setString(1, book.getBookCode());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getCategory());
            stmt.setString(5, book.getPublisher());
            stmt.setInt(6, book.getPublishYear());
            stmt.setString(7, book.getBookStatus());
            stmt.setInt(8, book.getRentalPrice());
            stmt.setInt(9, book.getDepositPrice());
            return stmt.executeUpdate() > 0;
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
        }
    }

    /** Cập nhật thông tin sách */
    public boolean updateBook (Book book) throws SQLException {
        String query = "UPDATE Books SET BookCode = ?, Title = ?, Author = ?, Category = ?, Publisher = ?, PublishYear = ?, BookStatus = ?, RentalPrice = ?, DepositPrice = ? WHERE BookID = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setString(1, book.getBookCode());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getCategory());
            stmt.setString(5, book.getPublisher());
            stmt.setInt(6, book.getPublishYear());
            stmt.setString(7, book.getBookStatus());
            stmt.setInt(8, book.getRentalPrice());
            stmt.setInt(9, book.getDepositPrice());
            stmt.setInt(10, book.getBookid());
            return stmt.executeUpdate() > 0;
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
        }
    }

    /** Xóa sách */
    public boolean deleteBook(int bookId) throws SQLException {
        String query = "DELETE FROM Books WHERE BookID = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setInt(1, bookId);
            return stmt.executeUpdate() > 0;
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
        }
    }

    /** Tìm sách theo từ khóa (Title, Author, BookCode) */
    public List<Book> searchBook(String keyword) throws SQLException {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM Books WHERE Title LIKE ? OR Author LIKE ? OR BookCode LIKE ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.prepareStatement(query);
            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
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
            }
        } finally {
            if (stmt != null) try { stmt.close(); } catch (SQLException e) {}
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
        return books;
    }
}
