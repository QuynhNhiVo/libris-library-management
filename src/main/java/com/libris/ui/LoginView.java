package com.libris.ui;

import com.libris.config.Constants;
import com.libris.controller.AuthController;
import com.libris.model.User;
import com.libris.utils.IconUtils;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin;
    private JLabel lblMessage;
    private AuthController authController;

    public LoginView() {
        authController = new AuthController();
        initComponents();

    }

    private void initComponents() {
        setTitle("Libris - Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel chính với BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Panel đăng nhập 
        JPanel loginPanel = createLoginPanel();

        // Panel phải
        JPanel artPanel = createArtPanel();

        // Thêm vào mainPanel
        mainPanel.add(loginPanel, BorderLayout.WEST);
        mainPanel.add(artPanel, BorderLayout.CENTER);

        add(mainPanel);

        // Enter
        tfUsername.addActionListener(e -> handleLogin());
        pfPassword.addActionListener(e -> handleLogin());
        btnLogin.addActionListener(e -> handleLogin());
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(400, 550));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Logo
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        JLabel lblLogo = new JLabel("Libris");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblLogo.setForeground(new Color(37, 99, 235));
        lblLogo.setIcon(IconUtils.loadIconForComponent(Constants.IC_BOOKS, lblLogo));
        lblLogo.setIconTextGap(10);
        panel.add(lblLogo, gbc);

        // Subtitle
        gbc.gridy = 1;
        JLabel lblSub = new JLabel("Hệ thống quản lý thuê sách thư viện");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        panel.add(lblSub, gbc);

        gbc.gridy = 2;
        panel.add(Box.createRigidArea(new Dimension(0, 20)), gbc);

        // Username
        gbc.gridy = 3;
        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lblUser, gbc);

        gbc.gridy = 4;
        tfUsername = new JTextField(20);
        tfUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tfUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        panel.add(tfUsername, gbc);

        // Password
        gbc.gridy = 5;
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lblPass, gbc);

        gbc.gridy = 6;
        pfPassword = new JPasswordField(20);
        pfPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pfPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        panel.add(pfPassword, gbc);

        // Message
        gbc.gridy = 7;
        lblMessage = new JLabel(" ");
        lblMessage.setForeground(Color.RED);
        lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(lblMessage, gbc);

        // Login Button
        gbc.gridy = 8;
        gbc.insets = new Insets(15, 8, 8, 8);
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setBackground(new Color(37, 99, 235));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(300, 45));
        panel.add(btnLogin, gbc);

        // Hint
        gbc.gridy = 9;
        gbc.insets = new Insets(15, 8, 8, 8);
        JLabel lblHint = new JLabel("Demo: admin / 123  |  customer1 / 123");
        lblHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHint.setForeground(new Color(150, 150, 150));
        panel.add(lblHint, gbc);

        return panel;
    }

    private JPanel createArtPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(37, 99, 235),
                        getWidth(), getHeight(), new Color(30, 58, 138));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(450, 550));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 30, 15, 30);
        gbc.gridx = 0;

        JLabel lblTitle = new JLabel("Quản lý thư viện thông minh");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        panel.add(lblTitle, gbc);

        gbc.gridy = 1;
        JLabel lblDesc = new JLabel("<html><center>Quản lý sách, khách hàng và đơn thuê<br>" +
                "trong một nền tảng duy nhất —<br>" +
                "nhanh, gọn và trực quan.</center></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblDesc.setForeground(new Color(220, 230, 255));
        panel.add(lblDesc, gbc);

        gbc.gridy = 2;
        String[] features = { "Quản lý sách", "Quản lý khách hàng",
                "Quản lý đơn thuê", "Báo cáo thống kê" };
        JPanel featurePanel = new JPanel(new GridLayout(4, 1, 5, 10));
        featurePanel.setOpaque(false);
        for (String f : features) {
            JLabel lbl = new JLabel(f);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            lbl.setForeground(Color.WHITE);
            featurePanel.add(lbl);
        }
        panel.add(featurePanel, gbc);

        return panel;
    }

    private void handleLogin() {
        String username = tfUsername.getText().trim();
        String password = new String(pfPassword.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            lblMessage.setText("Vui lòng nhập đủ thông tin!");
            return;
        }

        btnLogin.setEnabled(false);
        String originalText = btnLogin.getText();
        btnLogin.setText("Đang kết nối...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return authController.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        new MainFrame(user).setVisible(true);
                        dispose();
                    } else {
                        lblMessage.setText("Sai tên đăng nhập hoặc mật khẩu!");
                        btnLogin.setEnabled(true);
                        btnLogin.setText(originalText);
                        setCursor(Cursor.getDefaultCursor());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    lblMessage.setText("Lỗi kết nối cơ sở dữ liệu!");
                    btnLogin.setEnabled(true);
                    btnLogin.setText(originalText);
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginView().setVisible(true);
        });
    }
}