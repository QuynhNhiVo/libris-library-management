package com.libris.controller;

import com.libris.dao.RentalOrderDAO;
import com.libris.model.RentalOrder;
import com.libris.model.RentalOrderDetail;
import com.libris.view.interfaces.IOrdersView;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RentalOrderController {
    private RentalOrderDAO rentalOrderDao;
    private IOrdersView ordersView;

    public RentalOrderController() {
        this.rentalOrderDao = new RentalOrderDAO();
    }

    public RentalOrderController(RentalOrderDAO dao) {
        this.rentalOrderDao = dao != null ? dao : new RentalOrderDAO();
    }

    public RentalOrderController(IOrdersView view, RentalOrderDAO dao) {
        this.ordersView = view;
        this.rentalOrderDao = dao != null ? dao : new RentalOrderDAO();

        if (this.ordersView != null) {
            this.ordersView.addFilterListener(e -> loadOrders());
            this.ordersView.addSearchListener(e -> loadOrders());
            this.ordersView.addApproveOrderListener(e -> approveOrder());
            this.ordersView.addRejectOrderListener(e -> rejectOrder());
            this.ordersView.addReturnOrderListener(e -> returnOrder());
            this.ordersView.addSelectOrderListener(this::onSelectOrder);
            loadOrders();
        }
    }

    public void loadOrders() {
        if (ordersView == null) return;
        try {
            List<RentalOrder> all = rentalOrderDao.getAllOrders();
            
            int p = 0, r = 0, ret = 0, rej = 0;
            for (RentalOrder o : all) {
                if ("Pending".equalsIgnoreCase(o.getOrderStatus())) p++;
                else if ("Renting".equalsIgnoreCase(o.getOrderStatus())) r++;
                else if ("Returned".equalsIgnoreCase(o.getOrderStatus())) ret++;
                else if ("Rejected".equalsIgnoreCase(o.getOrderStatus())) rej++;
            }
            ordersView.updateKPIs(p, r, ret, rej, all.size());

            String status = ordersView.getStatusFilter();
            String kw = ordersView.getSearchKeyword();
            List<RentalOrder> filtered = new ArrayList<>();
            for (RentalOrder o : all) {
                boolean matchStatus = status == null || status.isEmpty() || status.equalsIgnoreCase("Tất cả") || o.getOrderStatus().equalsIgnoreCase(status);
                boolean matchKw = kw == null || kw.trim().isEmpty() ||
                        (o.getOrderCode() != null && o.getOrderCode().toLowerCase().contains(kw.toLowerCase().trim())) ||
                        (o.getCustomerName() != null && o.getCustomerName().toLowerCase().contains(kw.toLowerCase().trim()));
                if (matchStatus && matchKw) {
                    filtered.add(o);
                }
            }
            ordersView.showOrders(filtered);
        } catch (Exception e) {
            ordersView.showError("Lỗi tải danh sách đơn: " + e.getMessage());
        }
    }

    public void onSelectOrder() {
        if (ordersView == null) return;
        RentalOrder selected = ordersView.getSelectedOrder();
        if (selected != null) {
            try {
                List<RentalOrderDetail> details = rentalOrderDao.getOrderDetails(selected.getOrderId());
                ordersView.showOrderDetails(selected, details);
            } catch (Exception e) {
                ordersView.showError("Lỗi đọc chi tiết đơn: " + e.getMessage());
            }
        }
    }

    public void approveOrder() {
        if (ordersView == null) return;
        RentalOrder selected = ordersView.getSelectedOrder();
        if (selected == null) {
            ordersView.showError("Vui lòng chọn đơn cần duyệt!");
            return;
        }

        if (!"Pending".equalsIgnoreCase(selected.getOrderStatus())) {
            ordersView.showError("BR-order-002: Chỉ được phép duyệt đơn ở trạng thái Pending!");
            return;
        }

        try {
            rentalOrderDao.updateOrderStatus(selected.getOrderId(), "Renting", null);
            ordersView.showSuccessMessage("Đã duyệt đơn mượn thành công!");
            loadOrders();
        } catch (SQLException e) {
            ordersView.showError("Lỗi khi duyệt đơn: " + e.getMessage());
        }
    }

    public void rejectOrder() {
        if (ordersView == null) return;
        RentalOrder selected = ordersView.getSelectedOrder();
        if (selected == null) {
            ordersView.showError("Vui lòng chọn đơn cần từ chối!");
            return;
        }

        if (!"Pending".equalsIgnoreCase(selected.getOrderStatus())) {
            ordersView.showError("BR-order-002: Chỉ từ chối đơn ở trạng thái Pending!");
            return;
        }

        try {
            rentalOrderDao.updateOrderStatus(selected.getOrderId(), "Rejected", null);
            ordersView.showSuccessMessage("Đã từ chối đơn thuê thành công!");
            loadOrders();
        } catch (SQLException e) {
            ordersView.showError("Lỗi từ chối đơn: " + e.getMessage());
        }
    }

    public void returnOrder() {
        if (ordersView == null) return;
        RentalOrder selected = ordersView.getSelectedOrder();
        if (selected == null) {
            ordersView.showError("Vui lòng chọn đơn cần hoàn tất trả sách!");
            return;
        }

        try {
            rentalOrderDao.updateOrderStatus(selected.getOrderId(), "Returned", LocalDateTime.now());
            ordersView.showSuccessMessage("Xác nhận trả sách & hoàn cọc thành công!");
            loadOrders();
        } catch (SQLException e) {
            ordersView.showError("Lỗi nhận trả sách: " + e.getMessage());
        }
    }

    // Retain legacy direct methods for existing callers/tests
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
        return rentalOrderDao.addOrder(order);
    }

    public boolean updateOrderStatus(int orderId, String newStatus, LocalDateTime returnDate) throws SQLException {
        return rentalOrderDao.updateOrderStatus(orderId, newStatus, returnDate);
    }
}
