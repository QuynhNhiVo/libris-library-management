package com.libris.view.components;

import javax.swing.*;

import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;

import java.awt.*;

public class ModalDialog extends JDialog {
    private JPanel bodyPanel;
    private JButton btnSave;
    private JButton btnCancel;

    public ModalDialog(Window owner, String title) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LibrisColors.SURFACE_CONTAINER_LOW);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(LibrisFonts.TITLE_LG);
        lblTitle.setForeground(LibrisColors.ON_SURFACE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // Body
        bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        bodyPanel.setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footerPanel.setBackground(LibrisColors.SURFACE_CONTAINER_LOW);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, LibrisColors.SURFACE_CONTAINER_HIGH));

        btnCancel = FilterToolbar.createSecondaryButton("Hủy");
        btnCancel.addActionListener(e -> dispose());
        btnSave = FilterToolbar.createPrimaryButton("Lưu thông tin");

        footerPanel.add(btnCancel);
        footerPanel.add(btnSave);

        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(bodyPanel) {{
            setBorder(null);
            getViewport().setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        }}, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        setSize(480, 560);
        setLocationRelativeTo(owner);
    }

    public JPanel getBodyPanel() {
        return bodyPanel;
    }

    public JButton getSaveButton() {
        return btnSave;
    }

    public JButton getCancelButton() {
        return btnCancel;
    }
}
