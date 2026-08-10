package com.libris.ui;

import com.libris.config.Constants;
import com.libris.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class MainFrame extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private SidebarPanel sidebarPanel;

    private RentView rentView;
    private MyRentalsView myRentalsView;
    private BooksView booksView;
    private CustomersView customersView;
    private OrdersView ordersView;
    private ReportsView reportsView;
    private DashboardView dashboardView;
    private final Set<String> loadedPages = new HashSet<>();

    public MainFrame(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Libris - Hệ thống quản lý thư viện");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 800));
        setLocationRelativeTo(null);

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(241, 245, 249));

        // Sidebar
        sidebarPanel = new SidebarPanel(currentUser, this::navigateTo);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Content với CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(241, 245, 249));

        // Khởi tạo các View
        booksView = new BooksView();
        customersView = new CustomersView();
        ordersView = new OrdersView();
        reportsView = new ReportsView();
        rentView = new RentView(currentUser);
        myRentalsView = new MyRentalsView(currentUser);

        // Thêm các view quản lý (Admin)
        dashboardView = new DashboardView(this::navigateTo);
        contentPanel.add(dashboardView, "dashboard");
        contentPanel.add(booksView, "books");
        contentPanel.add(customersView, "customers");
        contentPanel.add(ordersView, "orders");
        contentPanel.add(reportsView, "reports");
        
        // View cho Customer
        contentPanel.add(rentView, "rent");
        contentPanel.add(myRentalsView, "myrentals");
        
        // View Profile chung
        contentPanel.add(new ProfileView(currentUser), "profile");

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Hiển thị mặc định theo role
        String defaultPage = Constants.ROLE_ADMIN.equalsIgnoreCase(currentUser.getRole()) ? "dashboard" : "rent";
        navigateTo(defaultPage);
    }

    // Tinh chỉnh hàm navigateTo: Tự động gọi refresh/loadData() khi sang trang mới
    private void navigateTo(String page) {
        cardLayout.show(contentPanel, page);
        sidebarPanel.setActive(page);

        if (loadedPages.contains(page)) {
            return;
        }

        loadedPages.add(page);

        switch (page) {
            case "myrentals":
                if (myRentalsView != null) myRentalsView.refreshData();
                break;
            case "rent":
                if (rentView != null) rentView.refreshData();
                break;
            case "orders":
                if (ordersView != null) ordersView.refreshData();
                break;
            case "books":
                if (booksView != null) booksView.refreshData();
                break;
            case "customers":
                if (customersView != null) customersView.refreshData();
                break;
            case "reports":
                if (reportsView != null) reportsView.refreshData();
                break;
            case "dashboard":
                if (dashboardView != null) dashboardView.refreshData();
                break;
        }
    }
}