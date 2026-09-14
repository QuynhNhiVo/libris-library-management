package com.libris.view.interfaces;

import com.libris.model.Book;
import java.awt.event.ActionListener;
import java.util.List;

public interface IBooksView {
    void showBooks(List<Book> books);
    void updateKPIs(int total, int available, int rented, int pending);
    void showError(String message);
    void showMessage(String message);
    Book getSelectedBook();
    Book getBookFormData();
    void setBookFormData(Book book);
    String getSearchKeyword();
    String getCategoryFilter();
    String getStatusFilter();
    void addSearchListener(ActionListener listener);
    void addSaveBookListener(ActionListener listener);
    void addDeleteBookListener(ActionListener listener);
    void addSelectBookListener(Runnable callback);
}
