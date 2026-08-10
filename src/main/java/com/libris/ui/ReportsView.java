package com.libris.ui;

import com.libris.controller.ReportController;
import com.libris.controller.BookController;
import com.libris.model.Book;
import com.libris.model.ReportStat;
import com.libris.utils.ChartGenerator;
import com.libris.utils.ExcelExporter;
import com.libris.utils.IconUtils;
import com.libris.config.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportsView extends JPanel {
    private ReportController reportController;
    private BookController bookController;
    
    private JPanel statsPanel;
    private JTable topBooksTable;
    private DefaultTableModel topBooksModel;
    private JPanel chartContainer;
    private JComboBox<String> cbYear;

    public ReportsView() {
        reportController = new ReportController();
        bookController = new BookController();
        initComponents();
        loadData();
    }

    public void refreshData() {
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        setBackground(new Color(245, 247, 250));

        // 1. HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Báo Cáo & Thống Kê");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(30, 41, 59));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // Nút Xuất báo cáo
        JButton btnExportHeader = new JButton("Xuất Báo Cáo") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();

                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
            }
        };
        Color hoverColor = new Color(51, 51, 51);
        Color normalColor = new Color(153, 153, 153);
        btnExportHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExportHeader.setBackground(normalColor);
        btnExportHeader.setForeground(Color.WHITE);
        btnExportHeader.setBorderPainted(false);
        btnExportHeader.setFocusPainted(false);
        btnExportHeader.setContentAreaFilled(false);
        btnExportHeader.setPreferredSize(new Dimension(140, 38));

        btnExportHeader.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnExportHeader.setBackground(hoverColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnExportHeader.setBackground(normalColor);
            }
        });
        btnExportHeader.addActionListener(e -> handleExport("exportRevenue"));
        
        headerPanel.add(btnExportHeader, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // NỘI DUNG CHÍNH
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);

        // 2. STATS CARDS
        statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        mainContent.add(statsPanel);
        mainContent.add(Box.createVerticalStrut(20));

        // 3. BẢNG THỐNG KÊ SỐ LƯỢT THUÊ SÁCH
        JPanel pnlTopBooks = createCardWrapper("Thống Kê Số Lượt Thuê Theo Sách");
        pnlTopBooks.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        topBooksModel = new DefaultTableModel(new String[]{"Mã sách", "Tên sách", "Tác giả", "Số lượt thuê"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        topBooksTable = new JTable(topBooksModel);
        topBooksTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        topBooksTable.setRowHeight(38);
        topBooksTable.setShowVerticalLines(false);
        topBooksTable.setGridColor(new Color(238, 238, 238));
        topBooksTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        topBooksTable.getTableHeader().setBackground(new Color(248, 250, 252));
        topBooksTable.getTableHeader().setPreferredSize(new Dimension(0, 40));

        JScrollPane scrollBooks = new JScrollPane(topBooksTable);
        scrollBooks.setBorder(BorderFactory.createEmptyBorder());
        scrollBooks.getViewport().setBackground(Color.WHITE);
        pnlTopBooks.add(scrollBooks, BorderLayout.CENTER);

        mainContent.add(pnlTopBooks);
        mainContent.add(Box.createVerticalStrut(20));

        // 4. BIỂU ĐỒ DOANH THU THÁNG
        JPanel pnlChartSection = createCardWrapper("Doanh Thu Theo Tháng");
        pnlChartSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));

        JPanel yearPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        yearPanel.setOpaque(false);
        yearPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel lblYear = new JLabel("Chọn năm:");
        lblYear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cbYear = new JComboBox<>();
        for (int i = 2020; i <= 2026; i++) {
            cbYear.addItem(String.valueOf(i));
        }
        cbYear.setSelectedItem("2026");
        cbYear.setPreferredSize(new Dimension(100, 32));
        cbYear.addActionListener(e -> updateCharts());
        
        yearPanel.add(lblYear);
        yearPanel.add(cbYear);
        pnlChartSection.add(yearPanel, BorderLayout.NORTH);

        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setOpaque(false);
        pnlChartSection.add(chartContainer, BorderLayout.CENTER);

        mainContent.add(pnlChartSection);

        JScrollPane mainScroll = new JScrollPane(mainContent);
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        mainScroll.getViewport().setBackground(new Color(245, 247, 250));
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);

        add(mainScroll, BorderLayout.CENTER);
    }

    private JPanel createCardWrapper(String title) {
        JPanel panel = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(226, 232, 240));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(30, 41, 59));
        panel.add(lblTitle, BorderLayout.NORTH);

        return panel;
    }

    private void loadData() {
        loadDashboardStats();
        loadTopBooksTable();
        updateCharts();
    }

    private void loadDashboardStats() {
        try {
            Map<String, Object> stats = reportController.getDashboardStats();
            statsPanel.removeAll();

            String[][] data = {
                {Constants.IC_BOOKS, "Tổng số sách", String.valueOf(stats.get("totalBooks"))},
                {Constants.IC_USERS, "Tổng khách hàng", String.valueOf(stats.get("totalCustomers"))},
                {Constants.IC_RENT, "Đang cho thuê", String.valueOf(stats.get("rentingOrders"))},
                {Constants.IC_RORATE, "Đã trả", String.valueOf(stats.get("returnedOrders"))}
            };

            for (String[] item : data) {
                statsPanel.add(createStatCard(item[0], item[1], item[2]));
            }

            statsPanel.revalidate();
            statsPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createStatCard(String iconPath, String label, String value) {
        JPanel card = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.setColor(new Color(226, 232, 240));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblIcon = new JLabel();
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        lblIcon.setIcon(IconUtils.loadIconForComponent(iconPath, lblIcon));
        lblIcon.setForeground(new Color(37, 99, 235));
        card.add(lblIcon, BorderLayout.WEST);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(new Color(15, 23, 42));
        textPanel.add(lblValue);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLabel.setForeground(new Color(100, 116, 139));
        textPanel.add(lblLabel);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private void loadTopBooksTable() {
        topBooksModel.setRowCount(0);
        try {
            List<ReportStat> topBooks = reportController.getTopBooks(10);
            List<Book> allBooks = bookController.getAllBooks();

            if (topBooks != null && allBooks != null) {
                Map<String, Book> bookMap = allBooks.stream()
                        .collect(Collectors.toMap(Book::getTitle, b -> b, (b1, b2) -> b1));

                for (ReportStat stat : topBooks) {
                    String title = stat.getLabel();
                    int rentCount = stat.getValue();
                    
                    Book bookObj = bookMap.get(title);
                    String bookCode = bookObj != null ? bookObj.getBookCode() : "N/A";
                    String author = bookObj != null ? bookObj.getAuthor() : "Đang cập nhật";

                    topBooksModel.addRow(new Object[]{ bookCode, title, author, rentCount });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCharts() {
        chartContainer.removeAll();
        try {
            int year = Integer.parseInt(cbYear.getSelectedItem().toString());
            List<ReportStat> revenueData = reportController.getMonthlyRevenue(year);
            
            JPanel revChart = ChartGenerator.createRevenueChart(revenueData);
            chartContainer.add(revChart, BorderLayout.CENTER);

            chartContainer.revalidate();
            chartContainer.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleExport(String type) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("BaoCaoDoanhThu_" + cbYear.getSelectedItem() + ".xlsx"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".xlsx")) {
                file = new File(file.getAbsolutePath() + ".xlsx");
            }
            
            boolean success = false;
            try {
                int year = Integer.parseInt(cbYear.getSelectedItem().toString());
                success = ExcelExporter.exportRevenueReport(
                    reportController.getMonthlyRevenue(year),
                    file
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Xuất file thành công!\n" + file.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Xuất file thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}