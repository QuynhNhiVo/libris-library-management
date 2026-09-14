package com.libris.view;

import com.libris.config.Constants;
import com.libris.controller.AuthController;
import com.libris.model.User;
import com.libris.utils.IconUtils;
import com.libris.view.interfaces.ILoginView;
import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginView extends JFrame implements ILoginView {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin;
    private JLabel lblMessage;
    // remember-me removed; sessions handled via database
    private JToggleButton btnToggleEye;
    private AuthController controller;


    public LoginView() {
        initComponents();
        this.controller = new AuthController(this, null);
    }

    private void initComponents() {
        setTitle("Đăng nhập Hệ thống — Libris Editorial Archive");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel rootPanel = new JPanel(new GridLayout(1, 2));
        rootPanel.setBackground(LibrisColors.CANVAS);

        // ----------------------------------------------------
        // LEFT COLUMN (40%): Branding & Test Helpers (#0f172a)
        // ----------------------------------------------------
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(LibrisColors.PRIMARY);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 36, 32, 36));

        // Brand & Logo Header
        JPanel brandHeader = new JPanel();
        brandHeader.setLayout(new BoxLayout(brandHeader, BoxLayout.Y_AXIS));
        brandHeader.setOpaque(false);

        JLabel lblLogo = new JLabel("Libris");
        lblLogo.setFont(LibrisFonts.DISPLAY_LG);
        lblLogo.setForeground(LibrisColors.ON_PRIMARY);
        lblLogo.setIcon(IconUtils.loadIconForComponent(Constants.IC_BOOKS, lblLogo));
        lblLogo.setIconTextGap(14);
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTagline = new JLabel("HỆ THỐNG QUẢN LÝ THƯ VIỆN CHUYÊN NGHIỆP");
        lblTagline.setFont(LibrisFonts.LABEL_SM);
        lblTagline.setForeground(LibrisColors.SECONDARY_SUPPORTING);
        lblTagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        brandHeader.add(lblLogo);
        brandHeader.add(Box.createRigidArea(new Dimension(0, 8)));
        brandHeader.add(lblTagline);

        // Center Quote Card
        JPanel quoteCard = new JPanel(new BorderLayout(8, 8));
        quoteCard.setOpaque(false);
        quoteCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JLabel lblQuote = new JLabel("<html><i>“Thư viện không chỉ là nơi lưu trữ sách, mà là đền thờ của trí tuệ và tri thức nhân loại.”</i></html>");
        lblQuote.setFont(LibrisFonts.BODY_MD);
        lblQuote.setForeground(new Color(226, 232, 240));

        JLabel lblAuthor = new JLabel("— Libris Editorial Archives");
        lblAuthor.setFont(LibrisFonts.BODY_SM);
        lblAuthor.setForeground(LibrisColors.SECONDARY_SUPPORTING);
        lblAuthor.setHorizontalAlignment(SwingConstants.RIGHT);

        quoteCard.add(lblQuote, BorderLayout.CENTER);
        quoteCard.add(lblAuthor, BorderLayout.SOUTH);

        // Footer WAL Status
        JLabel lblWalStatus = new JLabel("● SQLite WAL Journal Active • Java 25 & FlatLaf 3.7");
        lblWalStatus.setFont(LibrisFonts.CODE_SM);
        lblWalStatus.setForeground(LibrisColors.STATUS_SUCCESS_BORDER);

        JPanel leftCenterBody = new JPanel();
        leftCenterBody.setLayout(new BoxLayout(leftCenterBody, BoxLayout.Y_AXIS));
        leftCenterBody.setOpaque(false);
        leftCenterBody.add(quoteCard);
        leftCenterBody.add(Box.createRigidArea(new Dimension(0, 20)));
        // Quick-fill test accounts (kept per request)
        JPanel testAccountPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        testAccountPanel.setOpaque(false);

        JButton btnTestAdmin = new JButton("⚡ Admin Quick-Fill (admin / 123)");
        btnTestAdmin.setFont(LibrisFonts.BODY_SM);
        btnTestAdmin.setForeground(LibrisColors.ON_PRIMARY);
        btnTestAdmin.setBackground(new Color(30, 41, 59));
        btnTestAdmin.setFocusPainted(false);
        btnTestAdmin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTestAdmin.addActionListener(e -> {
            tfUsername.setText("admin");
            pfPassword.setText("123");
        });

        JButton btnTestCustomer = new JButton("⚡ Reader Quick-Fill (customer1 / 123)");
        btnTestCustomer.setFont(LibrisFonts.BODY_SM);
        btnTestCustomer.setForeground(LibrisColors.ON_PRIMARY);
        btnTestCustomer.setBackground(new Color(30, 41, 59));
        btnTestCustomer.setFocusPainted(false);
        btnTestCustomer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTestCustomer.addActionListener(e -> {
            tfUsername.setText("customer1");
            pfPassword.setText("123");
        });

        testAccountPanel.add(btnTestAdmin);
        testAccountPanel.add(btnTestCustomer);
        leftCenterBody.add(testAccountPanel);

        leftPanel.add(brandHeader, BorderLayout.NORTH);
        leftPanel.add(leftCenterBody, BorderLayout.CENTER);
        leftPanel.add(lblWalStatus, BorderLayout.SOUTH);

        // ----------------------------------------------------
        // RIGHT COLUMN (60%): Auth Form (#ffffff)
        // ----------------------------------------------------
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(36, 44, 36, 44));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        gbc.gridy = 0;
        rightPanel.add(Box.createRigidArea(new Dimension(0, 6)), gbc);

        // Username Input
        gbc.gridy = 2;
        JLabel lblUser = new JLabel("Tên đăng nhập / Mã thẻ");
        lblUser.setFont(LibrisFonts.TITLE_MD);
        lblUser.setForeground(LibrisColors.PRIMARY);
        rightPanel.add(lblUser, gbc);

        gbc.gridy = 3;
        tfUsername = new JTextField("admin", 20);
        tfUsername.setFont(LibrisFonts.BODY_MD);
        tfUsername.setPreferredSize(new Dimension(340, 40));
        tfUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        rightPanel.add(tfUsername, gbc);

        // Password Input
        gbc.gridy = 4;
        JLabel lblPass = new JLabel("Mật khẩu truy cập");
        lblPass.setFont(LibrisFonts.TITLE_MD);
        lblPass.setForeground(LibrisColors.PRIMARY);
        rightPanel.add(lblPass, gbc);

        gbc.gridy = 5;
        JPanel passWrapper = new JPanel(new BorderLayout());
        passWrapper.setOpaque(false);

        pfPassword = new JPasswordField("123", 20);
        pfPassword.setFont(LibrisFonts.BODY_MD);
        pfPassword.setPreferredSize(new Dimension(290, 40));
        pfPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        btnToggleEye = new JToggleButton("👁");
        btnToggleEye.setFont(LibrisFonts.BODY_SM);
        btnToggleEye.setFocusPainted(false);
        btnToggleEye.setContentAreaFilled(false);
        btnToggleEye.addActionListener(e -> {
            if (btnToggleEye.isSelected()) {
                pfPassword.setEchoChar((char) 0);
            } else {
                pfPassword.setEchoChar('•');
            }
        });

        passWrapper.add(pfPassword, BorderLayout.CENTER);
        passWrapper.add(btnToggleEye, BorderLayout.EAST);
        rightPanel.add(passWrapper, gbc);

        // Remember Me & Forgot Password
        gbc.gridy = 6;
        JPanel optsPanel = new JPanel(new BorderLayout());
        optsPanel.setOpaque(false);

        // "Remember me" removed - session handling via database only

        JLabel lblForgot = new JLabel("Quên mật khẩu?");
        lblForgot.setFont(LibrisFonts.BODY_SM);
        lblForgot.setForeground(LibrisColors.ACCENT_BLUE);
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // remember-me control removed
        optsPanel.add(lblForgot, BorderLayout.EAST);
        rightPanel.add(optsPanel, gbc);

        // Message error label
        gbc.gridy = 7;
        lblMessage = new JLabel(" ");
        lblMessage.setFont(LibrisFonts.BODY_SM);
        lblMessage.setForeground(LibrisColors.STATUS_ERROR);
        rightPanel.add(lblMessage, gbc);

        // Submit Button (CTA #0f172a)
        gbc.gridy = 8;
        btnLogin = new JButton("Đăng nhập vào hệ thống");
        btnLogin.setFont(LibrisFonts.TITLE_LG);
        btnLogin.setBackground(LibrisColors.PRIMARY);
        btnLogin.setForeground(LibrisColors.ON_PRIMARY);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(340, 44));
        rightPanel.add(btnLogin, gbc);

        // no role selection UI; role is resolved from database on login

        rootPanel.add(leftPanel);
        rootPanel.add(rightPanel);
        add(rootPanel);
    }

    // role selection removed; role is derived from database

    @Override
    public String getUsername() {
        return tfUsername.getText().trim();
    }

    @Override
    public String getPassword() {
        return new String(pfPassword.getPassword());
    }

    @Override
    public void setErrorMessage(String message) {
        lblMessage.setText(message != null ? message : " ");
    }

    @Override
    public void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        if (loading) {
            btnLogin.setText("Đang xác thực...");
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            btnLogin.setText("Đăng nhập vào hệ thống");
            setCursor(Cursor.getDefaultCursor());
        }
    }

    @Override
    public void addLoginListener(ActionListener listener) {
        btnLogin.addActionListener(listener);
        tfUsername.addActionListener(listener);
        pfPassword.addActionListener(listener);
    }

    @Override
    public void closeView() {
        System.out.println("[LoginView] closeView() called");
        dispose();
    }

    @Override
    public void openMainFrame(User user) {
        System.out.println("[LoginView] openMainFrame() user=" + (user != null ? user.getUsername() : "null"));
        new MainFrame(user).setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}