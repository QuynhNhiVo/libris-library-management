package com.libris.controller;

import com.libris.dao.BookDAO;
import com.libris.model.Book;

import java.util.List;
import java.sql.SQLException;

public class BookController {
    private BookDAO bookDao = new BookDAO();

    public List<Book> getAllBooks() {
        try {
            return bookDao.getAllBooks();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addBook (Book book) throws SQLException {
        try {
            return bookDao.addBook(book);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean updateBook(Book book) throws SQLException {
        try {
            return bookDao.updateBook(book);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean deleteBook(int bookId) throws SQLException {
        try {
            return bookDao.deleteBook(bookId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<Book> searchBook(String keyword) {
        try {
            return bookDao.searchBook(keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
