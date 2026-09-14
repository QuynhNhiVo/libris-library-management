package com.libris.view.interfaces;

import com.libris.model.RentalOrder;
import com.libris.model.RentalOrderDetail;
import java.awt.event.ActionListener;
import java.util.List;

public interface IOrdersView {
    void showOrders(List<RentalOrder> orders);
    void showOrderDetails(RentalOrder order, List<RentalOrderDetail> details);
    void showError(String message);
    void showSuccessMessage(String message);
    RentalOrder getSelectedOrder();
    String getStatusFilter();
    String getSearchKeyword();
    String getRejectReason();
    void addFilterListener(ActionListener listener);
    void addSearchListener(ActionListener listener);
    void addApproveOrderListener(ActionListener listener);
    void addRejectOrderListener(ActionListener listener);
    void addReturnOrderListener(ActionListener listener);
    void addSelectOrderListener(Runnable callback);
    void updateKPIs(int pending, int renting, int returned, int rejected, int total);
}
