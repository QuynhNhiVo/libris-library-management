package com.libris.ui;

import com.libris.controller.AuthController;
import com.libris.controller.RentController;
import com.libris.model.RentalOrder;
import com.libris.model.User;
import com.libris.utils.IconUtils;
import com.libris.config.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ProfileView extends JPanel {
    private User currentUser;
    private AuthController authController;
    private RentController rentController;

    private JTextField tfUsername;
    private JTextField tfFullName;
    private JTextField tfEmail;
    private JTextField tfPhone;

    private JPasswordField pfCurrentPass;
    private JPasswordField pfNewPass;
    private JPasswordField pfConfirmPass;

    private JLabel lblRentedBooksCount;

    public ProfileView(User user) {
        this.currentUser = user;
        this.authController = new AuthController();
        this.rentController = new RentController();
        initComponents();
        loadDynamicData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);

        // 1. HEADER TITLE
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel lblTitle = new JLabel("Hồ sơ cá nhân");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(30, 41, 59));

        headerPanel.add(lblTitle);
        mainContent.add(headerPanel);

        // 2. CARD THÔNG TIN CÁ NHÂN
        JPanel profileCard = new RoundedPanel(16, Color.WHITE);
        profileCard.setLayout(new BorderLayout(20, 0));
        profileCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        profileCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        JLabel lblAvatar = new JLabel(IconUtils.loadIcon(Constants.IC_USERC));
        lblAvatar.setFont(new Font("Segoe UI", Font.PLAIN, 60));
        lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        profileCard.add(lblAvatar, BorderLayout.WEST);

        JPanel formInfoPanel = new JPanel(new GridBagLayout());
        formInfoPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lblUserTitle = new JLabel("Tên đăng nhập:");
        lblUserTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formInfoPanel.add(lblUserTitle, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        tfUsername = new JTextField(currentUser.getUsername(), 20);
        tfUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tfUsername.setPreferredSize(new Dimension(0, 34));
        tfUsername.setEditable(false);
        tfUsername.setBackground(new Color(241, 245, 249));
        tfUsername.setForeground(new Color(100, 116, 139));
        formInfoPanel.add(tfUsername, gbc);

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblNameTitle = new JLabel("Họ và tên:");
        lblNameTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formInfoPanel.add(lblNameTitle, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        tfFullName = new JTextField(currentUser.getFullName() != null ? currentUser.getFullName() : "", 20);
        tfFullName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tfFullName.setPreferredSize(new Dimension(0, 34));
        styleInputTextField(tfFullName);
        formInfoPanel.add(tfFullName, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel lblEmailTitle = new JLabel("Email:");
        lblEmailTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formInfoPanel.add(lblEmailTitle, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        tfEmail = new JTextField(currentUser.getEmail() != null ? currentUser.getEmail() : "", 20);
        tfEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tfEmail.setPreferredSize(new Dimension(0, 34));
        styleInputTextField(tfEmail);
        formInfoPanel.add(tfEmail, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel lblPhoneTitle = new JLabel("Số điện thoại:");
        lblPhoneTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formInfoPanel.add(lblPhoneTitle, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        tfPhone = new JTextField(currentUser.getPhone() != null ? currentUser.getPhone() : "", 20);
        tfPhone.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tfPhone.setPreferredSize(new Dimension(0, 34));
        styleInputTextField(tfPhone);
        formInfoPanel.add(tfPhone, gbc);

        profileCard.add(formInfoPanel, BorderLayout.CENTER);
        mainContent.add(profileCard);
        mainContent.add(Box.createVerticalStrut(12));

        JPanel saveProfilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        saveProfilePanel.setOpaque(false);
        JButton btnSaveProfile = new JButton("Lưu thay đổi") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
            }
        };
        btnSaveProfile.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSaveProfile.setForeground(Color.WHITE);
        btnSaveProfile.setBackground(new Color(16, 185, 129));
        btnSaveProfile.setContentAreaFilled(false);
        btnSaveProfile.setBorderPainted(false);
        btnSaveProfile.setFocusPainted(false);
        btnSaveProfile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSaveProfile.setPreferredSize(new Dimension(120, 38));
        btnSaveProfile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSaveProfile.setBackground(new Color(5, 150, 105));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnSaveProfile.setBackground(new Color(16, 185, 129));
            }
        });
        btnSaveProfile.addActionListener(e -> handleUpdateProfile());
        saveProfilePanel.add(btnSaveProfile);

        mainContent.add(saveProfilePanel);
        mainContent.add(Box.createVerticalStrut(20));

        // 3. LƯỚI THÔNG TIN CHI TIẾT THỐNG KÊ
        JPanel gridDetails = new JPanel(new GridLayout(2, 2, 20, 20));
        gridDetails.setOpaque(false);
        gridDetails.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        lblRentedBooksCount = new JLabel("Đang tải...");
        lblRentedBooksCount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRentedBooksCount.setForeground(new Color(30, 41, 59));

        gridDetails.add(
                createStaticInfoBox(IconUtils.loadIconForComponent(Constants.IC_EMAIL, new JLabel()), "EMAIL HỆ THỐNG",
                        currentUser.getEmail() != null && !currentUser.getEmail().isEmpty() ? currentUser.getEmail()
                                : "Chưa cập nhật"));
        gridDetails.add(
                createStaticInfoBox(IconUtils.loadIconForComponent(Constants.IC_PHONE, new JLabel()), "SỐ ĐIỆN THOẠI",
                        currentUser.getPhone() != null && !currentUser.getPhone().isEmpty() ? currentUser.getPhone()
                                : "Chưa cập nhật"));
        gridDetails.add(createStaticInfoBox(IconUtils.loadIconForComponent(Constants.IC_SHIELD, new JLabel()),
                "VAI TRÒ TÀI KHOẢN", currentUser.getRole()));

        // Hộp hiển thị tổng số sách đã mượn
        JPanel boxRented = new JPanel(new GridLayout(2, 1, 0, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(226, 232, 240));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        boxRented.setOpaque(false);
        boxRented.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTopRented = new JLabel("TỔNG SÁCH ĐÃ MƯỢN");
        lblTopRented.setIcon(IconUtils.loadIconForComponent(Constants.IC_TOTAL_BOOKS, lblTopRented));
        lblTopRented.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTopRented.setForeground(new Color(100, 116, 139));

        boxRented.add(lblTopRented);
        boxRented.add(lblRentedBooksCount);

        gridDetails.add(boxRented);
        mainContent.add(gridDetails);
        mainContent.add(Box.createVerticalStrut(20));

        // 4. CARD ĐỔI MẬT KHẨU BẢO MẬT
        JPanel passwordCard = new RoundedPanel(16, Color.WHITE);
        passwordCard.setLayout(new BorderLayout(0, 20));
        passwordCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        passwordCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        JLabel lblPassTitle = new JLabel("  Đổi mật khẩu bảo mật");
        lblPassTitle.setIcon(IconUtils.loadIconForComponent(Constants.IC_KEY, lblPassTitle));
        lblPassTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPassTitle.setForeground(new Color(30, 41, 59));
        passwordCard.add(lblPassTitle, BorderLayout.NORTH);

        JPanel passForm = new JPanel(new GridBagLayout());
        passForm.setOpaque(false);
        GridBagConstraints gbcPass = new GridBagConstraints();
        gbcPass.insets = new Insets(8, 8, 8, 8);
        gbcPass.fill = GridBagConstraints.HORIZONTAL;

        pfCurrentPass = new JPasswordField(20);
        pfNewPass = new JPasswordField(20);
        pfConfirmPass = new JPasswordField(20);

        styleTextField(pfCurrentPass);
        styleTextField(pfNewPass);
        styleTextField(pfConfirmPass);

        int r = 0;
        addFormRow(passForm, gbcPass, "Mật khẩu hiện tại", pfCurrentPass, r++);
        addFormRow(passForm, gbcPass, "Mật khẩu mới", pfNewPass, r++);
        addFormRow(passForm, gbcPass, "Xác nhận mật khẩu", pfConfirmPass, r);

        passwordCard.add(passForm, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setOpaque(false);

        JButton btnCancel = new JButton("Hủy bỏ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
            }
        };
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setForeground(new Color(100, 116, 139));
        btnCancel.setBackground(new Color(241, 245, 249));
        btnCancel.setContentAreaFilled(false);
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.setPreferredSize(new Dimension(100, 38));
        btnCancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCancel.setBackground(new Color(226, 232, 240));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnCancel.setBackground(new Color(241, 245, 249));
            }
        });
        btnCancel.addActionListener(e -> clearPasswordFields());

        JButton btnUpdatePass = new JButton("Cập nhật mật khẩu") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
            }
        };
        btnUpdatePass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnUpdatePass.setForeground(Color.WHITE);
        btnUpdatePass.setBackground(new Color(37, 99, 235));
        btnUpdatePass.setContentAreaFilled(false);
        btnUpdatePass.setBorderPainted(false);
        btnUpdatePass.setFocusPainted(false);
        btnUpdatePass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpdatePass.setPreferredSize(new Dimension(170, 38));
        btnUpdatePass.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnUpdatePass.setBackground(new Color(29, 78, 216));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnUpdatePass.setBackground(new Color(37, 99, 235));
            }
        });
        btnUpdatePass.addActionListener(e -> handleUpdatePassword());

        actionPanel.add(btnCancel);
        actionPanel.add(btnUpdatePass);
        passwordCard.add(actionPanel, BorderLayout.SOUTH);

        mainContent.add(passwordCard);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(245, 247, 250));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createStaticInfoBox(Icon icon, String title, String value) {
        JPanel box = new JPanel(new GridLayout(2, 1, 0, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(226, 232, 240));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        box.setOpaque(false);
        box.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTop = new JLabel(title);
        if (icon != null) {
            lblTop.setIcon(icon);
        }
        lblTop.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTop.setForeground(new Color(100, 116, 139));

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblVal.setForeground(new Color(30, 41, 59));

        box.add(lblTop);
        box.add(lblVal);
        return box;
    }

    // Số sách đã mượn
    private void loadDynamicData() {
        try {
            int customerId = resolveCustomerId(currentUser);
            List<RentalOrder> rentals = rentController.getCustomerRentals(customerId);
            int totalBooks = 0;
            if (rentals != null) {
                for (RentalOrder order : rentals) {
                    if (order.getDetails() != null) {
                        totalBooks += order.getDetails().size();
                    }
                }
            }
            lblRentedBooksCount.setText(totalBooks + " cuốn");
        } catch (Exception e) {
            lblRentedBooksCount.setText("0 cuốn");
        }
    }

    private int resolveCustomerId(User user) {
        if (user == null)
            return 1;
        if (user.getCustomerId() != null)
            return user.getCustomerId();
        if (user.getUsername() != null) {
            if (user.getUsername().equalsIgnoreCase("customer1"))
                return 1;
            if (user.getUsername().equalsIgnoreCase("customer2"))
                return 2;
        }
        return user.getUserId();
    }

    private void styleInputTextField(JTextField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
    }

    private void styleTextField(JPasswordField tf) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(0, 34));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(71, 85, 105));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private void handleUpdateProfile() {
        String fullName = tfFullName.getText().trim();
        String email = tfEmail.getText().trim();
        String phone = tfPhone.getText().trim();

        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ và tên không được để trống!", "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean success = authController.updateProfileDetails(currentUser.getUserId(), fullName, email, phone);
            if (success) {
                currentUser.setFullName(fullName);
                currentUser.setEmail(email);
                currentUser.setPhone(phone);
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdatePassword() {
        String currentPass = new String(pfCurrentPass.getPassword()).trim();
        String newPass = new String(pfNewPass.getPassword()).trim();
        String confirmPass = new String(pfConfirmPass.getPassword()).trim();

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin mật khẩu!", "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp với mật khẩu mới!", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean success = authController.changePassword(currentUser.getUserId(), currentPass, newPass);
            if (success) {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                clearPasswordFields();
            } else {
                JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không chính xác!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearPasswordFields() {
        pfCurrentPass.setText("");
        pfNewPass.setText("");
        pfConfirmPass.setText("");
    }

    private static class RoundedPanel extends JPanel {
        private int arc;
        private Color bgColor;

        public RoundedPanel(int arc, Color bgColor) {
            this.arc = arc;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setColor(new Color(226, 232, 240));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}