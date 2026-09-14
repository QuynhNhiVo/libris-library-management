package com.libris.view.components;

import javax.swing.*;

import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;

import java.awt.*;

public class FilterToolbar extends JPanel {
    private JTextField tfSearch;
    private JPanel filterContainer;
    private JPanel actionContainer;

    public FilterToolbar(String searchPlaceholder) {
        setLayout(new BorderLayout(12, 0));
        setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, LibrisColors.HAIRLINE_BORDER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        // Left: Search Field & Filters
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);

        tfSearch = new JTextField(18);
        tfSearch.setFont(LibrisFonts.BODY_MD);
        tfSearch.setToolTipText(searchPlaceholder);
        tfSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        tfSearch.setVisible(true);
        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                triggerSearch();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                triggerSearch();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                triggerSearch();
            }

            private void triggerSearch() {
                for (java.awt.event.ActionListener al : tfSearch.getActionListeners()) {
                    al.actionPerformed(new java.awt.event.ActionEvent(
                            tfSearch,
                            java.awt.event.ActionEvent.ACTION_PERFORMED,
                            null));
                }
            }
        });
        leftPanel.add(tfSearch);

        filterContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterContainer.setOpaque(false);
        leftPanel.add(filterContainer);

        // Right: Action Buttons
        actionContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionContainer.setOpaque(false);

        add(leftPanel, BorderLayout.WEST);
        add(actionContainer, BorderLayout.EAST);
    }

    public JTextField getSearchField() {
        return tfSearch;
    }

    public void addFilterComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(LibrisFonts.BODY_MD);
        comboBox.setPreferredSize(new Dimension(160, 36));
        filterContainer.add(comboBox);
    }

    public void addActionButton(JButton button) {
        button.setFont(LibrisFonts.BODY_MD);
        actionContainer.add(button);
    }

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(LibrisFonts.TITLE_MD);
        btn.setBackground(LibrisColors.PRIMARY);
        btn.setForeground(LibrisColors.ON_PRIMARY);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(LibrisFonts.TITLE_MD);
        btn.setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        btn.setForeground(LibrisColors.ON_SURFACE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        return btn;
    }
}
