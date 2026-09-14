package com.libris.view.components;

import javax.swing.*;

import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;

import java.awt.*;

public class StatusBadge extends JLabel {
    private String status;

    public StatusBadge(String status) {
        super(status != null ? status : "");
        this.status = status;
        setFont(LibrisFonts.LABEL_SM);
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(true);
        updateStyle();
    }

    public void setStatus(String status) {
        this.status = status;
        setText(status != null ? status : "");
        updateStyle();
    }

    private void updateStyle() {
        if (status == null) status = "";
        String s = status.toLowerCase().trim();

        Color bg;
        Color fg;
        Color border;

        if (s.contains("available") || s.contains("có sẵn") || s.contains("đã trả") || s.contains("returned")) {
            bg = LibrisColors.STATUS_SUCCESS_BG;
            fg = LibrisColors.STATUS_SUCCESS_TEXT;
            border = LibrisColors.STATUS_SUCCESS_BORDER;
        } else if (s.contains("renting") || s.contains("rented") || s.contains("đang thuê") || s.contains("đang cho mượn")) {
            bg = LibrisColors.STATUS_RENTED_BG;
            fg = LibrisColors.STATUS_RENTED_TEXT;
            border = LibrisColors.STATUS_RENTED_BORDER;
        } else if (s.contains("pending") || s.contains("chờ duyệt") || s.contains("reserved")) {
            bg = LibrisColors.STATUS_WARNING_BG;
            fg = LibrisColors.STATUS_WARNING_TEXT;
            border = LibrisColors.STATUS_WARNING_BORDER;
        } else if (s.contains("overdue") || s.contains("quá hạn") || s.contains("rejected") || s.contains("từ chối")) {
            bg = LibrisColors.STATUS_ERROR_BG;
            fg = LibrisColors.STATUS_ERROR_TEXT;
            border = LibrisColors.STATUS_ERROR_BORDER;
        } else {
            bg = LibrisColors.SURFACE_CONTAINER;
            fg = LibrisColors.ON_SURFACE_VARIANT;
            border = LibrisColors.HAIRLINE_BORDER;
        }

        setBackground(bg);
        setForeground(fg);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 1),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));
    }
}

