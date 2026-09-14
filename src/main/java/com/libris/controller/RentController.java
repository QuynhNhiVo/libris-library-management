package com.libris.controller;

import com.libris.dao.RentDAO;
import com.libris.dao.RentalOrderDAO;
import com.libris.model.Book;
import com.libris.model.RentalOrder;
import com.libris.model.RentRequest;
import com.libris.model.User;
import com.libris.view.interfaces.IMyRentalsView;
import com.libris.view.interfaces.IRentView;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RentController {
    private RentDAO rentDAO;
    private RentalOrderDAO orderDAO;
    private IRentView rentView;
    private IMyRentalsView myRentalsView;
    private User currentUser;

    public RentController() {
        this.rentDAO = new RentDAO();
        this.orderDAO = new RentalOrderDAO();
    }

    public RentController(RentDAO rentDAO, RentalOrderDAO orderDAO) {
        this.rentDAO = rentDAO != null ? rentDAO : new RentDAO();
        this.orderDAO = orderDAO != null ? orderDAO : new RentalOrderDAO();
    }

    public RentController(IRentView view, User user, RentDAO rentDAO) {
        this.rentView = view;
        this.currentUser = user;
        this.rentDAO = rentDAO != null ? rentDAO : new RentDAO();
        this.orderDAO = new RentalOrderDAO();

        if (this.rentView != null) {
            this.rentView.addSearchListener(e -> loadAvailableBooks());
            this.rentView.addSubmitRentRequestListener(e -> submitRentRequest());
            loadAvailableBooks();
        }
    }

    public RentController(IMyRentalsView view, User user, RentalOrderDAO orderDAO) {
        this.myRentalsView = view;
        this.currentUser = user;
        this.orderDAO = orderDAO != null ? orderDAO : new RentalOrderDAO();
        this.rentDAO = new RentDAO();

        if (this.myRentalsView != null) {
            this.myRentalsView.addReturnBookListener(e -> handleReturnBook());
            loadMyRentals();
        }
    }

    public void loadAvailableBooks() {if (rentView == null) return;
        try {
            List<Book> available = rentDAO.getAvailableBooks();
            
            java.util.Set<String> catSet = new java.util.LinkedHashSet<>();
            for (Book b : available) {
                if (b.getCategory() != null && !b.getCategory().isEmpty()) {
                    catSet.add(b.getCategory());
                }
            }
            ((com.libris.view.RentView) rentView).setCategories(new ArrayList<>(catSet));
            
            String kw = rentView.getSearchKeyword() != null ? rentView.getSearchKeyword().toLowerCase().trim() : "";
            String cat = ((com.libris.view.RentView) rentView).getCategoryFilter();
            
            List<Book> filtered = new ArrayList<>();
            for (Book b : available) {
                boolean matchKw = kw.isEmpty() || 
                        (b.getTitle() != null && b.getTitle().toLowerCase().contains(kw)) ||
                        (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(kw)) ||
                        (b.getBookCode() != null && b.getBookCode().toLowerCase().contains(kw));
                boolean matchCat = cat == null || cat.equals("Tất cả") || 
                        (b.getCategory() != null && b.getCategory().equals(cat));
                
                if (matchKw && matchCat) {
                    filtered.add(b);
                }
            }
            rentView.showAvailableBooks(filtered);
        } catch (Exception e) {
            rentView.showError("Lỗi tải danh sách sách: " + e.getMessage());
        }
    }

    public void submitRentRequest() {
        if (rentView == null)
            return;
        List<Book> cart = rentView.getCartBooks();
        if (cart == null || cart.isEmpty()) {
            rentView.showError("Giỏ hàng đang trống! Vui lòng chọn ít nhất 1 cuốn.");
            return;
        }

        int custId = -1;
        if (currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole())) {
            String custCode = rentView.getAdminCustomerInput();
            if (custCode == null || custCode.isEmpty()) {
                rentView.showError("Admin cần nhập Mã khách hàng (VD: C001, C002) để tạo đơn!");
                return;
            }
            try {
                // Lookup Customer bằng CustomerDAO
                com.libris.dao.CustomerDAO custDao = new com.libris.dao.CustomerDAO();
                com.libris.model.Customer c = custDao.getAllCustomers().stream()
                        .filter(x -> x.getCustomerCode().equalsIgnoreCase(custCode)).findFirst().orElse(null);
                if (c == null) {
                    rentView.showError("Không tìm thấy khách hàng với mã: " + custCode);
                    return;
                }
                custId = c.getCustomerId();
            } catch (Exception ex) {
                rentView.showError("Lỗi hệ thống khi tra cứu khách hàng!");
                return;
            }
        } else if (currentUser != null && currentUser.getCustomerId() != null && currentUser.getCustomerId() > 0) {
            custId = currentUser.getCustomerId();
        } else {
            rentView.showError("Tài khoản chưa có thông tin khách hàng!");
            return;
        }

        RentRequest req = new RentRequest();
        req.setCustomerId(custId);
        req.setBooks(cart);
        req.setExpectedReturnDate(LocalDateTime.now().plusDays(rentView.getRentDays()));
        try {
            boolean ok = rentDAO.createRentRequest(req);
            if (ok) {
                rentView.showSuccessMessage("Tạo phiếu thuê thành công! Đơn đang chờ duyệt.");
                rentView.clearCart();
                loadAvailableBooks();
            } else {
                rentView.showError("Không thể tạo đơn thuê!");
            }
        } catch (SQLException e) {
            rentView.showError("Lỗi khi tạo đơn: " + e.getMessage());
        }
    }

    public void loadMyRentals() {
        if (myRentalsView == null || currentUser == null)
            return;
        try {
            Integer custId = currentUser.getCustomerId();
            if (custId == null) {
                // Admin accounts have no linked customer; show empty list instead of throwing
                myRentalsView.showMyRentals(new java.util.ArrayList<>());
                return;
            }
            List<RentalOrder> list = rentDAO.getCustomerRentals(custId);
            myRentalsView.showMyRentals(list);
        } catch (Exception e) {
            myRentalsView.showError("Lỗi tải đơn thuê cá nhân: " + e.getMessage());
        }
    }

    public void handleReturnBook() {
        if (myRentalsView == null) return;
        RentalOrder selected = myRentalsView.getSelectedRentalOrder();
        if (selected == null) {
            myRentalsView.showError("Vui lòng chọn đơn!");
            return;
        }
        try {

            if ("Pending".equalsIgnoreCase(selected.getOrderStatus())) {
                orderDAO.updateOrderStatus(selected.getOrderId(), "Rejected", null);
                myRentalsView.showSuccessMessage("Đã hủy yêu cầu mượn sách thành công!");
            } else {
                orderDAO.updateOrderStatus(selected.getOrderId(), "Returned", LocalDateTime.now());
                myRentalsView.showSuccessMessage("Gửi yêu cầu trả sách thành công!");
            }
            loadMyRentals();
        } catch (SQLException e) {
            myRentalsView.showError("Lỗi xử lý: " + e.getMessage());
        }
    }

    // Retain legacy direct methods for existing callers/tests
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
        return orderDAO.updateOrderStatus(orderId, "Returned", LocalDateTime.now());
    }
}