package com.libris.view.components;

import javax.swing.*;

import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class KpiTabBar extends JPanel {
    private List<JButton> tabButtons = new ArrayList<>();
    private Consumer<Integer> tabSelectionConsumer;
    private int selectedIndex = 0;

    public KpiTabBar(String[] tabLabels, Consumer<Integer> consumer) {
        this.tabSelectionConsumer = consumer;
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        setBackground(LibrisColors.CANVAS);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LibrisColors.HAIRLINE_BORDER));

        for (int i = 0; i < tabLabels.length; i++) {
            final int index = i;
            JButton btn = new JButton(tabLabels[i]);
            btn.setFont(LibrisFonts.TITLE_MD);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setContentAreaFilled(false);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

            btn.addActionListener(e -> {
                setSelectedIndex(index);
                if (tabSelectionConsumer != null) {
                    tabSelectionConsumer.accept(index);
                }
            });

            tabButtons.add(btn);
            add(btn);
        }

        updateTabStyles();
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
        updateTabStyles();
    }

    public void updateTabLabels(String[] labels) {
        for (int i = 0; i < tabButtons.size() && i < labels.length; i++) {
            tabButtons.get(i).setText(labels[i]);
        }
    }

    private void updateTabStyles() {
        for (int i = 0; i < tabButtons.size(); i++) {
            JButton btn = tabButtons.get(i);
            if (i == selectedIndex) {
                btn.setForeground(LibrisColors.SECONDARY);
                btn.setFont(LibrisFonts.TITLE_MD);
                btn.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, LibrisColors.SECONDARY));
            } else {
                btn.setForeground(LibrisColors.ON_SURFACE_VARIANT);
                btn.setFont(LibrisFonts.BODY_MD);
                btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 12, 16));
            }
        }
        revalidate();
        repaint();
    }
}
