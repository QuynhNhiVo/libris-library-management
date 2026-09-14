package com.libris.view.interfaces;

import com.libris.model.Customer;
import java.awt.event.ActionListener;
import java.util.List;

public interface ICustomersView {
    void showCustomers(List<Customer> customers);
    void showError(String message);
    void showMessage(String message);
    Customer getSelectedCustomer();
    Customer getCustomerFormData();
    void setCustomerFormData(Customer customer);
    String getSearchKeyword();
    void addSearchListener(ActionListener listener);
    void addSaveCustomerListener(ActionListener listener);
    void addDeleteCustomerListener(ActionListener listener);
    void addSelectCustomerListener(Runnable callback);
    void updateKPIs(int total, int vip, int active, int warning);
}
