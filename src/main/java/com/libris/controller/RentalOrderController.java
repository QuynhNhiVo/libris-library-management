package com.libris.controller;

import com.libris.dao.RentalOrderDAO;
import com.libris.model.RentalOrder;
import com.libris.model.RentalOrderDetail;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class RentalOrderController {
    private RentalOrderDAO rentalOrderDao = new RentalOrderDAO();

    public List<RentalOrder> getAllOrders() {
        try {
            return rentalOrderDao.getAllOrders();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public RentalOrder getOrderById(int id) {
        try {
            return rentalOrderDao.getOrderById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<RentalOrderDetail> getOrderDetails(int orderId) {
        try {
            return rentalOrderDao.getOrderDetails(orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addOrder(RentalOrder order) throws SQLException {
        try {
            return rentalOrderDao.addOrder(order);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean updateOrderStatus(int orderId, String newStatus, LocalDateTime returnDate) throws SQLException {
        try {
            return rentalOrderDao.updateOrderStatus(orderId, newStatus, returnDate);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
