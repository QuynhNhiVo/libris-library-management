package com.libris.view.components;

import javax.swing.*;

import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;
import com.libris.view.theme.LibrisMetrics;

import java.awt.*;

public class InspectorPanel extends JPanel {
    private JLabel lblTitle;
    private JLabel lblSubtitle;
    private JPanel contentPanel;
    private JPanel actionPanel;

    public InspectorPanel(String title) {
        setLayout(new BorderLayout());
        setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, LibrisColors.HAIRLINE_BORDER),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        setPreferredSize(new Dimension(LibrisMetrics.INSPECTOR_WIDTH, 0));

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setOpaque(false);
        lblTitle = new JLabel(title);
        lblTitle.setFont(LibrisFonts.TITLE_LG);
        lblTitle.setForeground(LibrisColors.PRIMARY);

        lblSubtitle = new JLabel("Chi tiết mục được chọn");
        lblSubtitle.setFont(LibrisFonts.BODY_SM);
        lblSubtitle.setForeground(LibrisColors.ON_SURFACE_VARIANT);

        headerPanel.add(lblTitle);
        headerPanel.add(lblSubtitle);

        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, LibrisColors.HAIRLINE_BORDER),
                BorderFactory.createEmptyBorder(0, 0, 12, 0)
        ));

        // Content
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        // Actions
        actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    public void setInspectorTitle(String title) {
        lblTitle.setText(title);
    }

    public void setInspectorSubtitle(String subtitle) {
        lblSubtitle.setText(subtitle);
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public JPanel getActionPanel() {
        return actionPanel;
    }

    public void clearContent() {
        contentPanel.removeAll();
        actionPanel.removeAll();
        revalidate();
        repaint();
    }
}

