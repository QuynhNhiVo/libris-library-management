package com.libris.view.components;

import com.libris.model.User;
import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;

import javax.swing.*;
import java.awt.*;

public class TopBar extends JPanel {
    private JLabel lblBreadcrumb;
    private JLabel lblUserAvatar;
    private JLabel lblUserName;
    private User currentUser;

    public TopBar(User currentUser) {
        this.currentUser = currentUser;

        setLayout(new BorderLayout(16, 0));
        setPreferredSize(new Dimension(0, 80));
        setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LibrisColors.HAIRLINE_BORDER));
        
        // Left: Breadcrumb
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 28));
        leftPanel.setOpaque(false);

        lblBreadcrumb = new JLabel("Libris / Tổng quan");
        lblBreadcrumb.setFont(LibrisFonts.TITLE_LG);
        lblBreadcrumb.setForeground(LibrisColors.PRIMARY);
        leftPanel.add(lblBreadcrumb);

        // Right: User Avatar Info
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 20));
        rightPanel.setOpaque(false);

        boolean isAdmin = currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole());

        // Avatar
        lblUserAvatar = new JLabel(getInitials(currentUser), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Vẽ nền tròn
                g2.setColor(getBackground());
                g2.fillOval(0, 0, getWidth(), getHeight());

                // Vẽ chữ
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                String text = getText();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, x, y);

                g2.dispose();
            }

            @Override
            public boolean isOpaque() {
                return false; 
            }
        };

        lblUserAvatar.setFont(LibrisFonts.LABEL_MD);
        lblUserAvatar.setBackground(LibrisColors.PRIMARY);
        lblUserAvatar.setForeground(LibrisColors.ON_PRIMARY);
        lblUserAvatar.setPreferredSize(new Dimension(44, 44)); 

        String displayName = getDisplayName(currentUser);
        String displayRole = isAdmin ? "Admin" : "Độc giả";

        lblUserName = new JLabel("<html><b>" + displayName + "</b><br><font color='#475569' size='2'>" + displayRole + "</font></html>");
        lblUserName.setFont(LibrisFonts.BODY_SM);

        rightPanel.add(lblUserAvatar);
        rightPanel.add(lblUserName);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    private String getDisplayName(User user) {
        if (user == null) return "";
        if (user.getFullName() != null && !user.getFullName().trim().isEmpty()) {
            return user.getFullName();
        }
        return user.getUsername() != null ? user.getUsername() : "";
    }

    private String getInitials(User user) {
        String name = getDisplayName(user);
        if (name.isBlank()) return "";
        String[] parts = name.split(" ");
        if (parts.length >= 2) {
            return (parts[parts.length - 2].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    public void setBreadcrumb(String text) {
        lblBreadcrumb.setText("Libris / " + text);
    }

    public void updateUser(User user) {
        this.currentUser = user;
        boolean isAdmin = user != null && "Admin".equalsIgnoreCase(user.getRole());
        lblUserAvatar.setText(getInitials(user));
        lblUserName.setText("<html><b>" + getDisplayName(user) + "</b><br><font color='#475569' size='2'>" +
                (isAdmin ? "Admin" : "Độc giả") + "</font></html>");
    }
}
