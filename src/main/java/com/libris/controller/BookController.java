package com.libris.controller;

import com.libris.dao.BookDAO;
import com.libris.model.Book;
import com.libris.view.interfaces.IBooksView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookController {
    private BookDAO bookDao;
    private IBooksView booksView;

    public BookController() {
        this.bookDao = new BookDAO();
    }

    public BookController(BookDAO bookDao) {
        this.bookDao = bookDao != null ? bookDao : new BookDAO();
    }

    public BookController(IBooksView view, BookDAO dao) {
        this.booksView = view;
        this.bookDao = dao != null ? dao : new BookDAO();

        if (this.booksView != null) {
            this.booksView.addSearchListener(e -> loadBooks());
            this.booksView.addSaveBookListener(e -> saveBook());
            this.booksView.addDeleteBookListener(e -> deleteBook());
            loadBooks();
        }
    }

    public void loadBooks() {
        if (booksView == null)
            return;
        try {
            String kw = booksView.getSearchKeyword();
            String cat = booksView.getCategoryFilter();
            String stat = booksView.getStatusFilter();
            List<Book> books = (kw != null && !kw.trim().isEmpty()) ? bookDao.searchBook(kw.trim())
                    : bookDao.getAllBooks();

            int total = books.size();
            int avail = 0, rented = 0, pending = 0;
            for (Book b : books) {
                if ("Available".equalsIgnoreCase(b.getBookStatus()))
                    avail++;
                else if ("Rented".equalsIgnoreCase(b.getBookStatus()))
                    rented++;
                else if ("Pending".equalsIgnoreCase(b.getBookStatus()))
                    pending++;
            }
            booksView.updateKPIs(total, avail, rented, pending);

            // Lọc hiển thị
            List<Book> filteredBooks = new ArrayList<>();
            for (Book b : books) {
                boolean matchCat = cat == null || cat.equals("Tất cả") || b.getCategory().equalsIgnoreCase(cat);
                boolean matchStat = stat == null || stat.equals("Tất cả") || b.getBookStatus().equalsIgnoreCase(stat);

                if (matchCat && matchStat) {
                    filteredBooks.add(b);
                }
            }
            booksView.showBooks(filteredBooks);
        } catch (Exception e) {
            booksView.showError("Lỗi tải danh sách sách: " + e.getMessage());
        }
    }

    public void saveBook() {
        if (booksView == null)
            return;
        Book book = booksView.getBookFormData();
        if (book == null)
            return;

        try {
            if (book.getBookId() > 0) {
                bookDao.updateBook(book);
                booksView.showMessage("Cập nhật sách thành công!");
            } else {
                bookDao.addBook(book);
                booksView.showMessage("Thêm sách mới thành công!");
            }
            loadBooks();
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                booksView.showError("E-book-002: Mã sách bị trùng lặp trong hệ thống!");
            } else {
                booksView.showError("Lỗi lưu thông tin sách: " + e.getMessage());
            }
        }
    }

    public void deleteBook() {
        if (booksView == null)
            return;
        Book selected = booksView.getSelectedBook();
        if (selected == null) {
            booksView.showError("Vui lòng chọn một sách để xóa!");
            return;
        }

        try {
            bookDao.deleteBook(selected.getBookId());
            booksView.showMessage("Xóa sách thành công!");
            loadBooks();
        } catch (SQLException e) {
            booksView.showError("Không thể xóa sách đang có người thuê!");
        }
    }

    // Retain legacy direct methods for existing callers/tests
    public List<Book> getAllBooks() {
        try {
            return bookDao.getAllBooks();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addBook(Book book) throws SQLException {
        return bookDao.addBook(book);
    }

    public boolean updateBook(Book book) throws SQLException {
        return bookDao.updateBook(book);
    }

    public boolean deleteBook(int bookId) throws SQLException {
        return bookDao.deleteBook(bookId);
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
