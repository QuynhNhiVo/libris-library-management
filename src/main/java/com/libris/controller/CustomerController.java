package com.libris.controller;

import java.sql.SQLException;
import java.util.List;

import com.libris.dao.CustomerDAO;
import com.libris.model.Customer;

public class CustomerController {
    private CustomerDAO customerDao = new CustomerDAO();

    public List<Customer> getAllCustomers() {
        try {
            return customerDao.getAllCustomers();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addCustomer(Customer customer) throws SQLException {
        try {
            return customerDao.addCustomer(customer);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean updateCustomer(Customer customer) throws SQLException {
        try {
            return customerDao.updateCustomer(customer);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean deleteCustomer(int customerId) throws SQLException {
        try {
            return customerDao.deleteCustomer(customerId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<Customer> searchCustomer(String keyword) {
        try {
            return customerDao.searchCustomer(keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
