package com.libris.view.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;
import com.libris.view.theme.LibrisMetrics;

import java.awt.*;
import java.text.Collator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Locale;

public class DataTablePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JLabel lblFooterInfo;
    private JButton btnPrevPage;
    private JButton btnNextPage;
    private JToggleButton btnDenseToggle;

    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isDense = false;

    public DataTablePanel(String[] columnNames) {
        setLayout(new BorderLayout());
        setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? LibrisColors.SURFACE_CONTAINER_LOWEST : LibrisColors.CANVAS);
                } else {
                    c.setBackground(LibrisColors.SURFACE_CONTAINER_LOW);
                    c.setForeground(LibrisColors.PRIMARY);
                }
                return c;
            }
        };

        table.setFont(LibrisFonts.BODY_MD);
        table.setRowHeight(LibrisMetrics.TABLE_ROW_HEIGHT_DENSE);
        table.setGridColor(LibrisColors.HAIRLINE_BORDER);
        table.setShowVerticalLines(false);
        rowSorter = new TableRowSorter<>(tableModel);
        Comparator<Object> naturalComparator = createNaturalComparator();
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            rowSorter.setComparator(i, naturalComparator);
        }
        table.setRowSorter(rowSorter);

        // Header Styling
        table.getTableHeader().setFont(LibrisFonts.LABEL_SM);
        table.getTableHeader().setBackground(LibrisColors.CANVAS);
        table.getTableHeader().setForeground(LibrisColors.ON_SURFACE_VARIANT);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LibrisColors.HAIRLINE_BORDER));

        // Default cell renderer with padding
        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
        defaultRenderer.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        for (int i = 0; i < columnNames.length; i++) {
            String col = columnNames[i].toLowerCase();
            if (col.contains("trạng thái") || col.contains("status")) {
                table.getColumnModel().getColumn(i).setCellRenderer(new StatusBadgeRenderer());
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(defaultRenderer);
            }
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1));
        scrollPane.getViewport().setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);

        // Footer Pagination Panel
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, LibrisColors.HAIRLINE_BORDER),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));

        lblFooterInfo = new JLabel("Hiển thị 1 - 10 / Tổng số 33 mục");
        lblFooterInfo.setFont(LibrisFonts.BODY_SM);
        lblFooterInfo.setForeground(LibrisColors.ON_SURFACE_VARIANT);

        JPanel rightFooterControl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightFooterControl.setOpaque(false);

        btnPrevPage = new JButton("‹ Trước");
        btnPrevPage.setFont(LibrisFonts.BODY_SM);
        btnPrevPage.setFocusPainted(false);

        btnNextPage = new JButton("Sau ›");
        btnNextPage.setFont(LibrisFonts.BODY_SM);
        btnNextPage.setFocusPainted(false);

        rightFooterControl.add(btnPrevPage);
        rightFooterControl.add(btnNextPage);

        footerPanel.add(lblFooterInfo, BorderLayout.WEST);
        footerPanel.add(rightFooterControl, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public void setFooterVisible(boolean visible) {
        BorderLayout layout = (BorderLayout) this.getLayout();
        Component footer = layout.getLayoutComponent(BorderLayout.SOUTH);
        if (footer != null) {
            footer.setVisible(visible);
        }
    }

    public JTable getTable() {
        return table;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public TableRowSorter<DefaultTableModel> getRowSorter() {
        return rowSorter;
    }

    public void setRowData(Object[][] data) {
        tableModel.setRowCount(0);
        if (data != null) {
            for (Object[] row : data) {
                tableModel.addRow(row);
            }
            lblFooterInfo.setText("Hiển thị 1 - " + data.length + " / Tổng số " + data.length + " mục");
        } else {
            lblFooterInfo.setText("Không có dữ liệu");
        }
    }

    private Comparator<Object> createNaturalComparator() {
        Collator collator = Collator.getInstance(new Locale("vi", "VN"));
        collator.setStrength(Collator.PRIMARY);
        return (left, right) -> {
            if (left == right) return 0;
            if (left == null) return -1;
            if (right == null) return 1;

            Double leftNumber = parseNumber(left.toString());
            Double rightNumber = parseNumber(right.toString());
            if (leftNumber != null && rightNumber != null) {
                return leftNumber.compareTo(rightNumber);
            }

            LocalDateTime leftDate = parseDateTime(left.toString());
            LocalDateTime rightDate = parseDateTime(right.toString());
            if (leftDate != null && rightDate != null) {
                return leftDate.compareTo(rightDate);
            }

            return collator.compare(left.toString(), right.toString());
        };
    }

    private Double parseNumber(String text) {
        if (text == null) return null;
        String cleaned = text.replace("#", "")
                .replaceAll("[^0-9,.-]", "")
                .replace(",", "");
        if (cleaned.isBlank() || cleaned.equals("-")) return null;
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDateTime parseDateTime(String text) {
        if (text == null || text.isBlank()) return null;
        String normalized = text.trim();
        try {
            if (normalized.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return LocalDate.parse(normalized).atStartOfDay();
            }
            if (normalized.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}.*")) {
                return LocalDateTime.parse(normalized);
            }
            if (normalized.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                return LocalDateTime.parse(normalized.replace(" ", "T"));
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static class StatusBadgeRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String status = value != null ? value.toString() : "";
            StatusBadge badge = new StatusBadge(status);
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
            if (isSelected) {
                panel.setBackground(LibrisColors.SURFACE_CONTAINER_LOW);
            } else {
                panel.setBackground(row % 2 == 0 ? LibrisColors.SURFACE_CONTAINER_LOWEST : LibrisColors.CANVAS);
            }
            panel.add(badge);
            return panel;
        }
    }

    public void updateColumns(String[] newColumns) {
        tableModel.setColumnIdentifiers(newColumns);
        rowSorter = new TableRowSorter<>(tableModel);
        Comparator<Object> naturalComparator = createNaturalComparator();
        for (int i = 0; i < newColumns.length; i++) {
            rowSorter.setComparator(i, naturalComparator);
        }
        table.setRowSorter(rowSorter);
        
        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
        defaultRenderer.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        for (int i = 0; i < newColumns.length; i++) {
            String col = newColumns[i].toLowerCase();
            // Thêm "phân loại" để render ra Badge màu
            if (col.contains("trạng thái") || col.contains("status") || col.contains("phân loại")) {
                table.getColumnModel().getColumn(i).setCellRenderer(new StatusBadgeRenderer());
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(defaultRenderer);
            }
        }
    }
    
    public void updateFooterCount() {
        int count = tableModel.getRowCount();
        lblFooterInfo.setText("Hiển thị 1 - " + count + " / Tổng số " + count + " mục");
    }
}
