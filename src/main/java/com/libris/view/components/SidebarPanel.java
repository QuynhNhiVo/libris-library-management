package com.libris.view.components;

import com.libris.config.Constants;
import com.libris.model.User;
import com.libris.utils.IconUtils;
import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;
import com.libris.view.theme.LibrisMetrics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SidebarPanel extends JPanel {
    private User currentUser;
    private JPanel navContainer;
    private JButton btnLogout;
    private JLabel lblLogoText;
    private JLabel lblSectionTitle;
    private JLabel lblVersion;
    private JPanel footerPanel;

    private List<JButton> navButtons = new ArrayList<>();
    private Map<JButton, String> buttonViewMap = new HashMap<>();
    private Consumer<String> navSelectionConsumer;

    // sidebar is now resizable via JSplitPane in MainFrame; collapse button removed

    public SidebarPanel(User user, Consumer<String> navSelectionConsumer) {
        this.currentUser = user;
        this.navSelectionConsumer = navSelectionConsumer;

        setLayout(new BorderLayout());
        applySidebarWidth(LibrisMetrics.SIDEBAR_WIDTH);
        setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, LibrisColors.HAIRLINE_BORDER));

        // Top Header: Logo + Brand + Collapse Toggle
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 12));

        JPanel logoBrandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoBrandPanel.setOpaque(false);

        JLabel lblLogoIcon = new JLabel();
        lblLogoIcon.setIcon(IconUtils.loadIconForComponent(Constants.IC_BOOKS, lblLogoIcon));
        logoBrandPanel.add(lblLogoIcon);

        lblLogoText = new JLabel("Libris");
        lblLogoText.setFont(LibrisFonts.DISPLAY_MD);
        lblLogoText.setForeground(LibrisColors.PRIMARY);
        logoBrandPanel.add(lblLogoText);

        headerPanel.add(logoBrandPanel, BorderLayout.WEST);

        // Center Nav Items Container
        navContainer = new JPanel();
        navContainer.setLayout(new BoxLayout(navContainer, BoxLayout.Y_AXIS));
        navContainer.setOpaque(false);
        navContainer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        lblSectionTitle = new JLabel("");
        lblSectionTitle.setFont(LibrisFonts.LABEL_SM);
        lblSectionTitle.setForeground(LibrisColors.PLACEHOLDER);
        lblSectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 0));

        buildNavItems();

        // Footer: Version, WAL status & Logout
        footerPanel = new JPanel(new BorderLayout(6, 6));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, LibrisColors.HAIRLINE_BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        lblVersion = new JLabel("<html>v1.0.4 • <font color='#059669'>SQLite WAL</font></html>");
        lblVersion.setFont(LibrisFonts.BODY_SM);
        lblVersion.setForeground(LibrisColors.ON_SURFACE_VARIANT);

        btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(LibrisFonts.BODY_MD);
        btnLogout.setForeground(LibrisColors.STATUS_ERROR);
        btnLogout.setBackground(LibrisColors.STATUS_ERROR_BG);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LibrisColors.STATUS_ERROR_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        footerPanel.add(lblVersion, BorderLayout.NORTH);
        footerPanel.add(btnLogout, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(navContainer, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public void updateRoleView(User user) {
        this.currentUser = user;
        buildNavItems();
        revalidate();
        repaint();
    }

    private void buildNavItems() {
        navContainer.removeAll();
        navButtons.clear();
        buttonViewMap.clear();

        navContainer.add(lblSectionTitle);

        boolean isAdmin = currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole());

        if (isAdmin) {
            addNavItem("DashboardView", "Tổng quan", Constants.IC_DASHBOARD);
            addNavItem("BooksView", "Kho sách", Constants.IC_BOOKS);
            addNavItem("OrdersView", "Quản lý đơn thuê", Constants.IC_ORDERS);
            addNavItem("CustomersView", "Khách hàng", Constants.IC_CUSTOMERS);
            addNavItem("ReportsView", "Báo cáo & Thống kê", Constants.IC_REPORTS);
            addNavItem("ProfileView", "Hồ sơ cá nhân", Constants.IC_PROFILE);
        } else {
            addNavItem("BooksView", "Kho sách", Constants.IC_BOOKS);
            addNavItem("RentView", "Thuê sách", Constants.IC_RENT);
            addNavItem("MyRentalsView", "Sách đang thuê", Constants.IC_ORDERS);
            addNavItem("ProfileView", "Hồ sơ cá nhân", Constants.IC_PROFILE);
        }
    }

    private void addNavItem(String viewName, String label, String iconName) {
        JButton btn = new JButton(label);
        btn.setFont(LibrisFonts.TITLE_MD);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIcon(IconUtils.loadIconForComponent(iconName, btn));
        btn.setIconTextGap(12);

        int width = LibrisMetrics.SIDEBAR_WIDTH - 24;
        btn.setMaximumSize(new Dimension(width, 42));
        btn.setPreferredSize(new Dimension(width, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(label);

        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        btn.setForeground(LibrisColors.ON_SURFACE);

        btn.addActionListener(e -> {
            setActiveView(viewName);
            if (navSelectionConsumer != null) {
                navSelectionConsumer.accept(viewName);
            }
        });

        navButtons.add(btn);
        buttonViewMap.put(btn, viewName);
        navContainer.add(btn);
        navContainer.add(Box.createRigidArea(new Dimension(0, 4)));
    }

    // collapse functionality removed; resizing handled by parent JSplitPane

    private void applySidebarWidth(int width) {
        setPreferredSize(new Dimension(width, 0));
        setMinimumSize(new Dimension(72, 0));
    }

    public void setActiveView(String viewName) {
        for (JButton btn : navButtons) {
            String boundView = buttonViewMap.get(btn);
            if (boundView != null && (boundView.equalsIgnoreCase(viewName) || viewName.contains(boundView))) {
                btn.setBackground(LibrisColors.PRIMARY);
                btn.setForeground(LibrisColors.ON_PRIMARY);
            } else {
                btn.setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
                btn.setForeground(LibrisColors.ON_SURFACE);
            }
        }
        repaint();
    }

    public void addLogoutListener(ActionListener listener) {
        btnLogout.addActionListener(listener);
    }
}
