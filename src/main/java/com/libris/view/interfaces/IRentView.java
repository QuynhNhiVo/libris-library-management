package com.libris.view.interfaces;

import com.libris.model.Book;
import com.libris.model.RentRequest;
import java.awt.event.ActionListener;
import java.util.List;

public interface IRentView {
    void showAvailableBooks(List<Book> books);
    void showError(String message);
    void showSuccessMessage(String message);
    String getSearchKeyword();
    List<Book> getCartBooks();
    int getRentDays();
    String getNotes();
    void clearCart();
    void addSearchListener(ActionListener listener);
    void addSubmitRentRequestListener(ActionListener listener);
    String getAdminCustomerInput();
    String getCategoryFilter();
}
