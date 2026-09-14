package com.libris.view.components;

import javax.swing.*;

import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;

import java.awt.*;

public class ToastPanel extends JWindow {
    public ToastPanel(Window owner, String message, boolean isError) {
        super(owner);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        panel.setBackground(isError ? LibrisColors.ERROR_CONTAINER : LibrisColors.PRIMARY_CONTAINER);
        panel.setBorder(BorderFactory.createLineBorder(isError ? LibrisColors.ERROR : LibrisColors.PRIMARY, 1));

        JLabel lbl = new JLabel(message);
        lbl.setFont(LibrisFonts.BODY_MD);
        lbl.setForeground(isError ? LibrisColors.ON_ERROR_CONTAINER : LibrisColors.ON_PRIMARY);
        panel.add(lbl);

        add(panel);
        pack();

        if (owner != null && owner.isVisible()) {
            Point p = owner.getLocationOnScreen();
            int x = p.x + owner.getWidth() - getWidth() - 20;
            int y = p.y + owner.getHeight() - getHeight() - 40;
            setLocation(x, y);
        } else {
            setLocationRelativeTo(null);
        }
    }

    public static void showToast(Window owner, String message, boolean isError) {
        SwingUtilities.invokeLater(() -> {
            ToastPanel toast = new ToastPanel(owner, message, isError);
            toast.setVisible(true);
            Timer timer = new Timer(3000, e -> toast.dispose());
            timer.setRepeats(false);
            timer.start();
        });
    }
}
