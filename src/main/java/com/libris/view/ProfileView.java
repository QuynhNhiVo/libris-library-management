package com.libris.view;

import com.libris.controller.AuthController;
import com.libris.model.User;
import com.libris.view.components.FilterToolbar;
import com.libris.view.components.ToastPanel;
import com.libris.view.interfaces.IProfileView;
import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ProfileView extends JPanel implements IProfileView {
    // Chỉ giữ các trường có trong DB
    private JTextField txtUsername, txtFullName, txtEmail, txtPhone;
    private JPasswordField pfCurrentPass, pfNewPass, pfConfirmPass;
    private JProgressBar passStrengthBar;
    private JLabel lblMeter; // Để cập nhật độ mạnh mật khẩu
    private JButton btnUpdateProfile, btnChangePassword;
    private JLabel lblName, lblRoleTitle, lblAvatar, lblDate;
    private JPanel rightStats, auditCard;

    private User currentUser;
    private AuthController controller;
    private ActionListener updateProfileListener;
    private ActionListener changePasswordListener;

    public ProfileView(User user) {
        this.currentUser = user;
        initComponents();
        this.controller = new AuthController(this, null);
        setUserProfile(user);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(LibrisColors.CANVAS);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // ----------------------------------------------------
        // TOP USER PROFILE BANNER
        // ----------------------------------------------------
        JPanel bannerCard = new JPanel(new BorderLayout(16, 0));
        bannerCard.setBackground(LibrisColors.PRIMARY);
        bannerCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)));

        JPanel leftUserInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        leftUserInfo.setOpaque(false);

        lblAvatar = new JLabel("US", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                String text = getText();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, x, y);
                g2.dispose();
            }
            @Override
            public boolean isOpaque() { return false; }
        };
        lblAvatar.setFont(LibrisFonts.DISPLAY_MD);
        lblAvatar.setBackground(LibrisColors.PRIMARY);
        lblAvatar.setForeground(LibrisColors.ON_PRIMARY);
        lblAvatar.setPreferredSize(new Dimension(64, 64));

        JPanel textGroup = new JPanel(new GridLayout(3, 1, 0, 2));
        textGroup.setOpaque(false);

        lblName = new JLabel("Nguyễn Thu Trang");
        lblName.setFont(LibrisFonts.DISPLAY_MD);
        lblName.setForeground(LibrisColors.ON_PRIMARY);

        lblRoleTitle = new JLabel("Thủ thư trưởng / Quản trị viên • Mã số: LIB-8802");
        lblRoleTitle.setFont(LibrisFonts.BODY_MD);
        lblRoleTitle.setForeground(LibrisColors.SECONDARY_SUPPORTING);

        lblDate = new JLabel("Ngày kích hoạt tài khoản: 15/01/2023 • Trạng thái: 🟢 Hoạt động");
        lblDate.setFont(LibrisFonts.BODY_SM);
        lblDate.setForeground(LibrisColors.STATUS_SUCCESS_BORDER);

        textGroup.add(lblName);
        textGroup.add(lblRoleTitle);
        textGroup.add(lblDate);

        leftUserInfo.add(lblAvatar);
        leftUserInfo.add(textGroup);

        // 3 Activity Summary Badges
        rightStats = new JPanel(new GridLayout(1, 3, 10, 0));
        rightStats.setOpaque(false);
        rightStats.add(createBannerBadge("KHO QUẢN LÝ", "1.420"));
        rightStats.add(createBannerBadge("PHIẾU HOÀN TẤT", "191"));
        rightStats.add(createBannerBadge("CHỈ SỐ DUYỆT", "99.2%"));

        bannerCard.add(leftUserInfo, BorderLayout.WEST);
        bannerCard.add(rightStats, BorderLayout.EAST);

        // ----------------------------------------------------
        // 50/50 SPLIT FORMS
        // ----------------------------------------------------
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        splitPanel.setOpaque(false);

        // Left 50%: Personnel & Account Form + Audit Trail
        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setOpaque(false);

        JPanel infoFormCard = new JPanel(new GridBagLayout());
        infoFormCard.setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        infoFormCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel lblForm1 = new JLabel("THÔNG TIN NHÂN SỰ & TÀI KHOẢN");
        lblForm1.setFont(LibrisFonts.TITLE_MD);
        lblForm1.setForeground(LibrisColors.PRIMARY);
        gbc.gridy = 0;
        infoFormCard.add(lblForm1, gbc);

        // Khởi tạo các trường (chỉ giữ những trường có trong DB)
        txtUsername = new JTextField();
        txtUsername.setEditable(false); // Immutable
        txtFullName = new JTextField();
        txtEmail = new JTextField();
        txtPhone = new JTextField();

        addFormField(infoFormCard, gbc, 1, "Tên đăng nhập (Immutable):", txtUsername);
        addFormField(infoFormCard, gbc, 3, "Họ tên:", txtFullName);
        addFormField(infoFormCard, gbc, 5, "Email liên lạc:", txtEmail);
        addFormField(infoFormCard, gbc, 7, "Số điện thoại:", txtPhone);

        gbc.gridy = 9;
        btnUpdateProfile = FilterToolbar.createPrimaryButton("Lưu thay đổi hồ sơ");
        btnUpdateProfile.setBackground(LibrisColors.PRIMARY);
        btnUpdateProfile.addActionListener(e -> {
            if (updateProfileListener != null)
                updateProfileListener.actionPerformed(e);
            else
                handleSelfUpdateProfile();
        });
        infoFormCard.add(btnUpdateProfile, gbc);

        // Audit Trail Log Box
        auditCard = new JPanel(new BorderLayout(0, 6));
        auditCard.setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        auditCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JLabel lblAuditTitle = new JLabel("LỊCH SỬ TÁC VỤ GẦN NHẤT (AUDIT TRAIL)");
        lblAuditTitle.setFont(LibrisFonts.LABEL_SM);
        lblAuditTitle.setForeground(LibrisColors.PLACEHOLDER);

        JTextArea txtAuditLog = new JTextArea(
                "• 10/09/2026 17:30 - Phê duyệt đơn mượn #ORD-0104 cho độc giả Trần Văn An\n" +
                        "• 10/09/2026 14:15 - Xuất báo cáo tài chính Quý IV/2024 sang Excel (POI)\n" +
                        "• 09/09/2026 09:00 - Đăng nhập hệ thống thành công từ IP 192.168.1.15");
        txtAuditLog.setFont(LibrisFonts.CODE_SM);
        txtAuditLog.setEditable(false);
        txtAuditLog.setBackground(LibrisColors.CANVAS);

        auditCard.add(lblAuditTitle, BorderLayout.NORTH);
        auditCard.add(txtAuditLog, BorderLayout.CENTER);

        leftCol.add(infoFormCard);
        leftCol.add(Box.createRigidArea(new Dimension(0, 12)));
        leftCol.add(auditCard);

        // Right 50%: Security & Password
        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setOpaque(false);

        JPanel passFormCard = new JPanel(new GridBagLayout());
        passFormCard.setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        passFormCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        GridBagConstraints cgbc = new GridBagConstraints();
        cgbc.insets = new Insets(4, 4, 4, 4);
        cgbc.fill = GridBagConstraints.HORIZONTAL;
        cgbc.gridx = 0;
        cgbc.weightx = 1.0;

        JLabel lblForm2 = new JLabel("BẢO MẬT & ĐỔI MẬT KHẨU");
        lblForm2.setFont(LibrisFonts.TITLE_MD);
        lblForm2.setForeground(LibrisColors.PRIMARY);
        cgbc.gridy = 0;
        passFormCard.add(lblForm2, cgbc);

        pfCurrentPass = new JPasswordField();
        pfNewPass = new JPasswordField();
        pfConfirmPass = new JPasswordField();

        passStrengthBar = new JProgressBar(0, 100);
        passStrengthBar.setValue(0);
        passStrengthBar.setForeground(LibrisColors.STATUS_ERROR_TEXT);
        passStrengthBar.setPreferredSize(new Dimension(0, 6));

        addFormField(passFormCard, cgbc, 1, "Mật khẩu hiện tại:", pfCurrentPass);
        addFormField(passFormCard, cgbc, 3, "Mật khẩu mới:", pfNewPass);
        addFormField(passFormCard, cgbc, 5, "Xác nhận mật khẩu mới:", pfConfirmPass);

        cgbc.gridy = 7;
        lblMeter = new JLabel("Độ mạnh mật khẩu: Yếu (0/100)");
        lblMeter.setFont(LibrisFonts.BODY_SM);
        lblMeter.setForeground(LibrisColors.STATUS_ERROR_TEXT);
        passFormCard.add(lblMeter, cgbc);

        cgbc.gridy = 8;
        passFormCard.add(passStrengthBar, cgbc);

        // === Thêm DocumentListener để tính độ mạnh mật khẩu real-time ===
        pfNewPass.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateStrength();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateStrength();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateStrength();
            }

            private void updateStrength() {
                int length = pfNewPass.getPassword().length;
                int strength = Math.min(100, length * 10); // 1 ký tự = 10%

                passStrengthBar.setValue(strength);

                String level;
                Color color;
                if (strength > 70) {
                    level = "Mạnh";
                    color = LibrisColors.STATUS_SUCCESS_TEXT;
                } else if (strength > 40) {
                    level = "Trung bình";
                    color = LibrisColors.STATUS_WARNING_TEXT;
                } else {
                    level = "Yếu";
                    color = LibrisColors.STATUS_ERROR_TEXT;
                }

                lblMeter.setText("Độ mạnh mật khẩu: " + level + " (" + strength + "/100)");
                lblMeter.setForeground(color);
                passStrengthBar.setForeground(color);
            }
        });

        cgbc.gridy = 9;
        btnChangePassword = FilterToolbar.createSecondaryButton("🔑 Xác nhận đổi mật khẩu");
        btnChangePassword.addActionListener(e -> {
            if (changePasswordListener != null)
                changePasswordListener.actionPerformed(e);
            else
                handleSelfChangePass();
        });
        passFormCard.add(btnChangePassword, cgbc);

        rightCol.add(passFormCard);
        rightCol.add(Box.createRigidArea(new Dimension(0, 12)));

        splitPanel.add(leftCol);
        splitPanel.add(rightCol);

        JScrollPane scrollPane = new JScrollPane(splitPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(LibrisColors.CANVAS);

        add(bannerCard, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createBannerBadge(String title, String val) {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setBackground(new Color(255, 255, 255, 20));
        p.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(LibrisFonts.LABEL_SM);
        t.setForeground(LibrisColors.SECONDARY_SUPPORTING);
        JLabel v = new JLabel(val, SwingConstants.CENTER);
        v.setFont(LibrisFonts.TITLE_LG);
        v.setForeground(LibrisColors.ON_PRIMARY);
        p.add(t);
        p.add(v);
        return p;
    }

    private void addFormField(JPanel card, GridBagConstraints gbc, int y, String labelText, JComponent field) {
        gbc.gridy = y;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(LibrisFonts.BODY_SM);
        lbl.setForeground(LibrisColors.ON_SURFACE_VARIANT);
        card.add(lbl, gbc);

        gbc.gridy = y + 1;
        field.setFont(LibrisFonts.BODY_MD);
        card.add(field, gbc);
    }

    private void handleSelfUpdateProfile() {
        if (currentUser == null)
            return;
        boolean ok = controller.updateProfileDetails(currentUser.getUserId(), getFullName(), getEmail(), getPhone());
        if (ok) {
            currentUser.setFullName(getFullName());
            showSuccessMessage("Cập nhật thông tin hồ sơ cá nhân thành công!");
        } else {
            showError("Cập nhật hồ sơ thất bại!");
        }
    }

    private void handleSelfChangePass() {
        if (currentUser == null)
            return;
        String cur = getCurrentPassword();
        String n1 = getNewPassword();
        String n2 = getConfirmPassword();

        if (n1.isEmpty() || !n1.equals(n2)) {
            showError("Mật khẩu mới không trùng khớp hoặc đang để trống!");
            return;
        }

        boolean ok = controller.changePassword(currentUser.getUserId(), cur, n1);
        if (ok) {
            showSuccessMessage("Đổi mật khẩu bảo mật thành công!");
            pfCurrentPass.setText("");
            pfNewPass.setText("");
            pfConfirmPass.setText("");
        } else {
            showError("Mật khẩu hiện tại không chính xác!");
        }
    }

    @Override
    public void setUserProfile(User user) {
        this.currentUser = user;
        if (user != null) {
            boolean isAdmin = "Admin".equalsIgnoreCase(user.getRole());
            
            txtUsername.setText(user.getUsername());
            txtFullName.setText(user.getFullName() != null ? user.getFullName() : "");
            txtEmail.setText(user.getEmail() != null ? user.getEmail() : "");
            txtPhone.setText(user.getPhone() != null ? user.getPhone() : "");

            // Cập nhật Banner
            lblName.setText(user.getFullName() != null && !user.getFullName().isEmpty() ? user.getFullName() : user.getUsername());
            lblRoleTitle.setText(isAdmin ? "Quản trị viên (Admin)" : "Độc giả (Customer)");
            
            String initials = user.getUsername().substring(0, Math.min(2, user.getUsername().length())).toUpperCase();
            lblAvatar.setText(initials);

            // Ẩn Lịch sử tác vụ nếu là Customer
            auditCard.setVisible(isAdmin);

            // Cập nhật Badge bên phải
            rightStats.removeAll();
            if (isAdmin) {
                rightStats.add(createBannerBadge("QUYỀN HẠN", "TỐI ĐA"));
                rightStats.add(createBannerBadge("TRẠNG THÁI", "ACTIVE"));
            } else {
                rightStats.add(createBannerBadge("MÃ ĐỘC GIẢ", user.getCustomerId() != null ? "C00" + user.getCustomerId() : "N/A"));
                rightStats.add(createBannerBadge("TRẠNG THÁI", "ĐANG HOẠT ĐỘNG"));
            }
            rightStats.revalidate(); rightStats.repaint();
        }
    }

    @Override
    public String getFullName() {
        return txtFullName.getText().trim();
    }

    @Override
    public String getEmail() {
        return txtEmail.getText().trim();
    }

    @Override
    public String getPhone() {
        return txtPhone.getText().trim();
    }

    @Override
    public String getCurrentPassword() {
        return new String(pfCurrentPass.getPassword());
    }

    @Override
    public String getNewPassword() {
        return new String(pfNewPass.getPassword());
    }

    @Override
    public String getConfirmPassword() {
        return new String(pfConfirmPass.getPassword());
    }

    @Override
    public void showSuccessMessage(String message) {
        ToastPanel.showToast(SwingUtilities.getWindowAncestor(this), message, false);
    }

    @Override
    public void showError(String message) {
        ToastPanel.showToast(SwingUtilities.getWindowAncestor(this), message, true);
    }

    @Override
    public void addUpdateProfileListener(ActionListener listener) {
        this.updateProfileListener = listener;
    }

    @Override
    public void addChangePasswordListener(ActionListener listener) {
        this.changePasswordListener = listener;
    }
}