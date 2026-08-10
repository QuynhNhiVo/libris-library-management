package com.libris.controller;

import com.libris.dao.RentDAO;
import com.libris.dao.RentalOrderDAO;
import com.libris.model.Book;
import com.libris.model.RentalOrder;
import com.libris.model.RentRequest;

import java.sql.SQLException;
import java.util.List;

public class RentController {
    private RentDAO rentDAO;
    private RentalOrderDAO orderDAO;

    public RentController() {
        this.rentDAO = new RentDAO();
        this.orderDAO = new RentalOrderDAO();
    }

    public List<Book> getAvailableBooks() throws SQLException {
        return rentDAO.getAvailableBooks();
    }

    public List<RentalOrder> getCustomerRentals(int customerId) throws SQLException {
        return rentDAO.getCustomerRentals(customerId);
    }

    public boolean createRentRequest(RentRequest request) throws SQLException {
        return rentDAO.createRentRequest(request);
    }

    public boolean returnBooks(int orderId) throws SQLException {
        return orderDAO.updateOrderStatus(orderId, "Returned", java.time.LocalDateTime.now());
    }
}