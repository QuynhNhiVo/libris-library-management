package com.libris.controller;

import com.libris.dao.CustomerDAO;
import com.libris.model.Customer;
import com.libris.view.interfaces.ICustomersView;
import com.libris.dao.RentalOrderDAO;
import com.libris.model.RentalOrder;

import java.sql.SQLException;
import java.util.List;

public class CustomerController {
    private CustomerDAO customerDao;
    private ICustomersView customersView;

    public CustomerController() {
        this.customerDao = new CustomerDAO();
    }

    public CustomerController(CustomerDAO customerDao) {
        this.customerDao = customerDao != null ? customerDao : new CustomerDAO();
    }

    public CustomerController(ICustomersView view, CustomerDAO dao) {
        this.customersView = view;
        this.customerDao = dao != null ? dao : new CustomerDAO();

        if (this.customersView != null) {
            this.customersView.addSearchListener(e -> loadCustomers());
            this.customersView.addSaveCustomerListener(e -> saveCustomer());
            this.customersView.addDeleteCustomerListener(e -> deleteCustomer());
            loadCustomers();
        }
    }

    public void loadCustomers() {
        if (customersView == null) return;
        try {
            // Lấy sách đang thuê và cảnh báo
            com.libris.dao.RentalOrderDAO roDao = new com.libris.dao.RentalOrderDAO();
            List<com.libris.model.RentalOrder> allOrders = roDao.getAllOrders();
            int active = 0, warning = 0;
            for(com.libris.model.RentalOrder o : allOrders) {
                if("Renting".equalsIgnoreCase(o.getOrderStatus())) {
                    active++;
                    if (o.getExpectedReturnDate() != null && o.getExpectedReturnDate().isBefore(java.time.LocalDateTime.now())) {
                        warning++;
                    }
                }
            }

            String kw = customersView.getSearchKeyword();
            List<Customer> list = (kw != null && !kw.trim().isEmpty()) ? customerDao.searchCustomer(kw.trim()) : customerDao.getAllCustomers();
            
            customersView.updateKPIs(list.size(), 0, active, warning); // Truyền KPI ra View
            customersView.showCustomers(list);
        } catch (Exception e) {
            customersView.showError("Lỗi tải khách hàng: " + e.getMessage());
        }
    }

    public void saveCustomer() {
        if (customersView == null) return;
        Customer c = customersView.getCustomerFormData();
        if (c == null) return;

        try {
            if (c.getCustomerId() > 0) {
                customerDao.updateCustomer(c);
                customersView.showMessage("Cập nhật thông tin khách hàng thành công!");
            } else {
                customerDao.addCustomer(c);
                customersView.showMessage("Thêm khách hàng thành công!");
            }
            loadCustomers();
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                customersView.showError("E-cust-001: Mã khách hàng đã tồn tại!");
            } else {
                customersView.showError("Lỗi lưu thông tin khách hàng: " + e.getMessage());
            }
        }
    }

    public void deleteCustomer() {
        if (customersView == null) return;
        Customer selected = customersView.getSelectedCustomer();
        if (selected == null) {
            customersView.showError("Vui lòng chọn khách hàng cần xóa!");
            return;
        }

        try {
            customerDao.deleteCustomer(selected.getCustomerId());
            customersView.showMessage("Xóa khách hàng thành công!");
            loadCustomers();
        } catch (SQLException e) {
            customersView.showError("Không thể xóa khách hàng đang có đơn thuê active!");
        }
    }

    // Retain legacy direct methods for existing callers/tests
    public List<Customer> getAllCustomers() {
        try {
            return customerDao.getAllCustomers();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addCustomer(Customer customer) throws SQLException {
        return customerDao.addCustomer(customer);
    }

    public boolean updateCustomer(Customer customer) throws SQLException {
        return customerDao.updateCustomer(customer);
    }

    public boolean deleteCustomer(int customerId) throws SQLException {
        return customerDao.deleteCustomer(customerId);
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
