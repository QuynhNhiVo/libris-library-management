package com.libris.view;

import com.libris.config.Constants;
import com.libris.model.User;
import com.libris.view.components.SidebarPanel;
import com.libris.view.components.TopBar;
import com.libris.view.theme.LibrisColors;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private SidebarPanel sidebarPanel;
    private TopBar topBar;

    private BooksView booksView;
    private CustomersView customersView;
    private OrdersView ordersView;
    private ReportsView reportsView;
    private DashboardView dashboardView;
    private RentView rentView;
    private MyRentalsView myRentalsView;
    private ProfileView profileView;

    public MainFrame(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Libris — System Management (Editorial Archive)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 780));
        //setSize(1320, 860);
        setSize(1587, 907);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LibrisColors.CANVAS);

        // Sidebar & TopBar
        sidebarPanel = new SidebarPanel(currentUser, this::navigateTo);
        sidebarPanel.addLogoutListener(e -> handleLogout());

        topBar = new TopBar(currentUser);

        // Content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(LibrisColors.CANVAS);

        // Instantiate sub-views
        booksView = new BooksView(currentUser);
        customersView = new CustomersView();
        ordersView = new OrdersView();
        reportsView = new ReportsView();
        rentView = new RentView(currentUser);
        myRentalsView = new MyRentalsView(currentUser);
        profileView = new ProfileView(currentUser);
        dashboardView = new DashboardView(this::navigateTo);

        contentPanel.add(dashboardView, "DashboardView");
        contentPanel.add(booksView, "BooksView");
        contentPanel.add(customersView, "CustomersView");
        contentPanel.add(ordersView, "OrdersView");
        contentPanel.add(reportsView, "ReportsView");
        contentPanel.add(rentView, "RentView");
        contentPanel.add(myRentalsView, "MyRentalsView");
        contentPanel.add(profileView, "ProfileView");

        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.add(topBar, BorderLayout.NORTH);
        rightContainer.add(contentPanel, BorderLayout.CENTER);

        // Use JSplitPane to allow user to drag-resize sidebar instead of a collapse button
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarPanel, rightContainer);
        split.setOneTouchExpandable(false);
        split.setDividerSize(8);
        split.setDividerLocation(com.libris.view.theme.LibrisMetrics.SIDEBAR_WIDTH);
        split.setBorder(null);

        mainPanel.add(split, BorderLayout.CENTER);

        add(mainPanel);

        // Default screen
        boolean isAdmin = currentUser != null && Constants.ROLE_ADMIN.equalsIgnoreCase(currentUser.getRole());
        String defaultView = isAdmin ? "DashboardView" : "RentView";
        navigateTo(defaultView);
    }

    public void navigateTo(String viewName) {
        cardLayout.show(contentPanel, viewName);
        sidebarPanel.setActiveView(viewName);

        String breadcrumbLabel = viewName;
        switch (viewName) {
            case "DashboardView": breadcrumbLabel = "Tổng quan"; dashboardView.refreshData(); break;
            case "BooksView": breadcrumbLabel = "Kho sách"; booksView.refreshData(); break;
            case "OrdersView": breadcrumbLabel = "Quản lý đơn thuê"; ordersView.refreshData(); break;
            case "CustomersView": breadcrumbLabel = "Khách hàng"; customersView.refreshData(); break;
            case "ReportsView": breadcrumbLabel = "Báo cáo & Thống kê"; reportsView.refreshData(); break;
            case "RentView": breadcrumbLabel = "Cổng thuê sách"; rentView.refreshData(); break;
            case "MyRentalsView": breadcrumbLabel = "Sách đang thuê & Hoàn trả"; myRentalsView.refreshData(); break;
            case "ProfileView": breadcrumbLabel = "Hồ sơ cá nhân"; break;
        }
        topBar.setBreadcrumb(breadcrumbLabel);
    }

    public void navigateToRentWithCustomer(String customerCode) {
        rentView.setAdminCustomerInput(customerCode);
        navigateTo("RentView");
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?", "Đăng xuất Libris", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginView().setVisible(true);
        }
    }
}
