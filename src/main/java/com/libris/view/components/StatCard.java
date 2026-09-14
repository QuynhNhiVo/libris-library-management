package com.libris.view.components;

import javax.swing.*;

import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;

import java.awt.*;

public class StatCard extends JPanel {
    private JLabel lblTitle;
    private JLabel lblValue;
    private JLabel lblSubtitle;
    private Color topAccentColor;

    public StatCard(String title, String value, String subtitle, Icon icon) {
        this(title, value, subtitle, icon, LibrisColors.PRIMARY);
    }

    public StatCard(String title, String value, String subtitle, Icon icon, Color accentColor) {
        this.topAccentColor = accentColor != null ? accentColor : LibrisColors.PRIMARY;

        setLayout(new BorderLayout(8, 8));
        setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        // Header Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(LibrisFonts.LABEL_SM);
        lblTitle.setForeground(LibrisColors.ON_SURFACE_VARIANT);
        topPanel.add(lblTitle, BorderLayout.WEST);

        if (icon != null) {
            JLabel lblIcon = new JLabel(icon);
            topPanel.add(lblIcon, BorderLayout.EAST);
        }

        // Center Panel (Value + Subtitle)
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        centerPanel.setOpaque(false);

        lblValue = new JLabel(value);
        lblValue.setFont(LibrisFonts.DISPLAY_MD);
        lblValue.setForeground(LibrisColors.PRIMARY);
        centerPanel.add(lblValue);

        lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(LibrisFonts.BODY_SM);
        lblSubtitle.setForeground(LibrisColors.ON_SURFACE_VARIANT);
        centerPanel.add(lblSubtitle);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (topAccentColor != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(topAccentColor);
            g2.fillRect(0, 0, getWidth(), 3);
            g2.dispose();
        }
    }

    public void setValue(String value) {
        lblValue.setText(value);
    }

    public void setSubtitle(String subtitle) {
        lblSubtitle.setText(subtitle);
    }
}

