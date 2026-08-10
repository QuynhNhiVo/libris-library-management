package com.libris.ui;

import com.libris.model.User;
import com.libris.utils.IconUtils;
import com.libris.config.Constants;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class SidebarPanel extends JPanel {
    private User currentUser;
    private Consumer<String> navigator;
    private String activePage = "";
    private JPanel navPanel;

    public SidebarPanel(User user, Consumer<String> navigator) {
        this.currentUser = user;
        this.navigator = navigator;
        initComponents();
    }

    private void initComponents() {
        setPreferredSize(new Dimension(196, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(226, 232, 240)));
        setLayout(new BorderLayout());

        // Header 
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        logoPanel.setOpaque(false);

        JLabel lblLogoIcon = new JLabel();
        lblLogoIcon.setIcon(IconUtils.loadIconForComponent(Constants.IC_BOOKS, logoPanel));
        logoPanel.add(lblLogoIcon);
        
        JLabel lblBrand = new JLabel(" Libris");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblBrand.setForeground(new Color(37, 99, 235));
        logoPanel.add(lblBrand);
        
        headerPanel.add(logoPanel, BorderLayout.WEST);

        JLabel lblRole = new JLabel(isAdminRole() ? Constants.UI_LABEL_ADMIN_ROLE : Constants.UI_LABEL_CUSTOMER_ROLE);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRole.setForeground(new Color(148, 163, 184));
        headerPanel.add(lblRole, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // Navigation
        navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        if (isAdminRole()) {
            addNavItem(Constants.IC_DASHBOARD, Constants.UI_LABEL_DASHBOARD, "dashboard");
            addNavItem(Constants.IC_MBOOK, Constants.UI_LABEL_BOOKS, "books");
            addNavItem(Constants.IC_USERS, Constants.UI_LABEL_CUSTOMERS, "customers");
            addNavItem(Constants.IC_RENTALS, Constants.UI_LABEL_ORDERS, "orders");
            addNavItem(Constants.IC_REPORTS, Constants.UI_LABEL_REPORTS, "reports");
        } else {
            addNavItem(Constants.IC_RENT_BOOK, Constants.UI_LABEL_RENT, "rent");
            addNavItem(Constants.IC_RENTED_BOOKS, Constants.UI_LABEL_MY_RENTALS, "myrentals");
            addNavItem(Constants.IC_PROFILE, Constants.UI_LABEL_PROFILE, "profile");
        }

        add(navPanel, BorderLayout.CENTER);

        // --- FOOTER ---

        // User Info
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 12, 20, 12));

        // Panel user 
        JPanel userInfoPanel = new JPanel(new BorderLayout(10, 0));
        userInfoPanel.setBackground(new Color(248, 250, 252)); 
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        
        userInfoPanel.setMaximumSize(new Dimension(172, 50));
        userInfoPanel.setPreferredSize(new Dimension(172, 50));
        userInfoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblUserAvatar = new JLabel();
        lblUserAvatar.setIcon(IconUtils.loadIconForComponent(Constants.IC_USERS, lblUserAvatar));
        userInfoPanel.add(lblUserAvatar, BorderLayout.WEST);

        JPanel textUser = new JPanel(new GridLayout(2, 1, 0, 1));
        textUser.setOpaque(false);
        
        JLabel lblUserName = new JLabel(currentUser.getUsername());
        lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUserName.setForeground(new Color(30, 41, 59));
        
        JLabel lblUserRoleSub = new JLabel(currentUser.getRole());
        lblUserRoleSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblUserRoleSub.setForeground(new Color(148, 163, 184));
        
        textUser.add(lblUserName);
        textUser.add(lblUserRoleSub);
        userInfoPanel.add(textUser, BorderLayout.CENTER);

        footerPanel.add(userInfoPanel);
        footerPanel.add(Box.createVerticalStrut(10));

        // Nút Đăng xuất
        JButton btnLogout = new JButton(Constants.UI_LABEL_LOGOUT);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setMaximumSize(new Dimension(172, 38));
        btnLogout.setPreferredSize(new Dimension(172, 38));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setForeground(new Color(220, 38, 38));
        btnLogout.setContentAreaFilled(false);
        btnLogout.setOpaque(true);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (Constants.IC_LOGOUT != null && !Constants.IC_LOGOUT.isEmpty()) {
            btnLogout.setIcon(IconUtils.loadIconForComponent(Constants.IC_LOGOUT, btnLogout));
            btnLogout.setIconTextGap(10);
        }

        btnLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogout.setBackground(new Color(254, 242, 242)); 
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnLogout.setBackground(Color.WHITE);
            }
        });

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window != null) {
                    window.dispose(); // Close Current MainFrame 
                }
                new LoginView().setVisible(true); // Open LoginView
            }
        });

        footerPanel.add(btnLogout);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private boolean isAdminRole() {
        return Constants.ROLE_ADMIN.equalsIgnoreCase(currentUser.getRole());
    }

    private void addNavItem(String iconPath, String label, String page) {
        JButton btn = createNavButton(iconPath, label);
        btn.setName(page); 
        btn.addActionListener(e -> {
            activePage = page;
            navigator.accept(page);
            highlightActive();
        });
        navPanel.add(btn);
        navPanel.add(Box.createVerticalStrut(6));
    }

    private JButton createNavButton(String iconPath, String label) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.setMaximumSize(new Dimension(196, 42));
        btn.setPreferredSize(new Dimension(196, 42));

        btn.setBackground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (iconPath != null && !iconPath.isEmpty()) {
            btn.setIcon(IconUtils.loadIconForComponent(iconPath, btn));
            btn.setIconTextGap(12);
        }
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.getName() == null || !btn.getName().equals(activePage)) {
                    btn.setBackground(new Color(241, 245, 249));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.getName() == null || !btn.getName().equals(activePage)) {
                    btn.setBackground(Color.WHITE);
                }
            }
        });

        return btn;
    }

    private void highlightActive() {
        for (Component comp : navPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (btn.getName() != null && btn.getName().equals(activePage)) {
                    btn.setBackground(new Color(239, 246, 255));
                    btn.setForeground(new Color(37, 99, 235));
                    btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else {
                    btn.setBackground(Color.WHITE);
                    btn.setForeground(new Color(100, 116, 139));
                    btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                }
            }
        }
    }

    public void setActive(String page) {
        this.activePage = page;
        highlightActive();
    }
}