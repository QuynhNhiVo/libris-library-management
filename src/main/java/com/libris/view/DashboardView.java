package com.libris.view;

import com.libris.controller.ReportController;
import com.libris.model.Book;
import com.libris.model.RentalOrder;
import com.libris.model.ReportStat;
import com.libris.utils.ChartGenerator;
import com.libris.view.components.DataTablePanel;
import com.libris.view.components.FilterToolbar;
import com.libris.view.components.StatCard;
import com.libris.view.components.ToastPanel;
import com.libris.view.interfaces.IDashboardView;
import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DashboardView extends JPanel implements IDashboardView {
    private StatCard cardTotalCustomers, cardRentedBooks, cardPendingOrders, cardOverdueBooks;
    private JPanel chartRevenuePanel, chartDonutPanel, overdueAlertPanel;
    private DataTablePanel topBooksTable;
    private JLabel lblStickyText;
    private ReportController controller;
    private Consumer<String> navigationConsumer;

    public DashboardView(Consumer<String> navigationConsumer) {
        this.navigationConsumer = navigationConsumer;
        initComponents();
        this.controller = new ReportController((IDashboardView) this, null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(LibrisColors.CANVAS);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);

        // ROW 1: 4 KPI Cards 
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        kpiGrid.setOpaque(false);
        kpiGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        cardTotalCustomers = new StatCard("TỔNG ĐỘC GIẢ", "0", "Hội viên", null, LibrisColors.ACCENT_BLUE);
        cardRentedBooks = new StatCard("ĐANG CHO THUÊ", "0", "Lưu hành", null, LibrisColors.STATUS_RENTED);
        cardPendingOrders = new StatCard("ĐƠN CHỜ DUYỆT", "0", "Đang xử lý", null, LibrisColors.STATUS_WARNING);
        cardOverdueBooks = new StatCard("ĐƠN QUÁ HẠN", "0", "Cần thu hồi", null, LibrisColors.STATUS_ERROR);
        
        kpiGrid.add(cardTotalCustomers); kpiGrid.add(cardRentedBooks); 
        kpiGrid.add(cardPendingOrders); kpiGrid.add(cardOverdueBooks);
        mainContent.add(kpiGrid);
        mainContent.add(Box.createRigidArea(new Dimension(0, 16)));

        // ROW 2: 2 Charts (65/35)
        JPanel chartsRow = new JPanel(new GridBagLayout());
        chartsRow.setOpaque(false);
        GridBagConstraints cgbc = new GridBagConstraints();
        cgbc.fill = GridBagConstraints.BOTH; cgbc.insets = new Insets(0, 0, 0, 12);
        cgbc.weightx = 0.65; cgbc.weighty = 1.0;

        JPanel revenueContainer = createChartWrapperPanel("DIỄN BIẾN DOANH THU");
        chartRevenuePanel = ChartGenerator.createRevenueComboChart(null);
        revenueContainer.add(chartRevenuePanel, BorderLayout.CENTER);
        chartsRow.add(revenueContainer, cgbc);

        cgbc.insets = new Insets(0, 0, 0, 0); cgbc.weightx = 0.35;
        JPanel donutContainer = createChartWrapperPanel("THỂ LOẠI YÊU THÍCH");
        chartDonutPanel = ChartGenerator.createCategoryDonutChart(null);
        donutContainer.add(chartDonutPanel, BorderLayout.CENTER);
        chartsRow.add(donutContainer, cgbc);
        mainContent.add(chartsRow);
        mainContent.add(Box.createRigidArea(new Dimension(0, 16)));

        // ROW 3: Top Books (Tăng chiều rộng 75%, Lùn đi) & Cảnh báo (25%)
        JPanel operationsRow = new JPanel(new GridBagLayout());
        operationsRow.setOpaque(false);
        GridBagConstraints ogbc = new GridBagConstraints();
        ogbc.fill = GridBagConstraints.BOTH; ogbc.insets = new Insets(0, 0, 0, 12);
        ogbc.weightx = 0.75; // TĂNG CHIỀU RỘNG LÊN 75%
        ogbc.weighty = 1.0;

        JPanel topBooksContainer = createChartWrapperPanel("TOP 5 SÁCH ĐƯỢC QUAN TÂM NHẤT");
        topBooksContainer.setPreferredSize(new Dimension(0, 220)); // THU NHỎ CHIỀU CAO LẠI (Lùn đi)
        topBooksTable = new DataTablePanel(new String[]{ "Mã", "Tên sách", "Tác giả", "Thể loại", "Lượt mượn" });
        topBooksTable.setRowData(new Object[0][0]);
        topBooksTable.setFooterVisible(false); // BỎ FOOTER BẢNG THEO YÊU CẦU
        topBooksContainer.add(topBooksTable, BorderLayout.CENTER);
        operationsRow.add(topBooksContainer, ogbc);

        ogbc.insets = new Insets(0, 0, 0, 0); ogbc.weightx = 0.25; // GIẢM RỘNG PANEL CẢNH BÁO
        overdueAlertPanel = createChartWrapperPanel("CẢNH BÁO");
        
        JPanel alertBody = new JPanel(new BorderLayout());
        alertBody.setOpaque(false);
        
        lblStickyText = new JLabel("Đang tải dữ liệu...");
        lblStickyText.setFont(LibrisFonts.BODY_MD);
        lblStickyText.setForeground(LibrisColors.STATUS_ERROR_TEXT);
        alertBody.add(lblStickyText, BorderLayout.NORTH);
        
        JButton btnProcess = FilterToolbar.createPrimaryButton("Xử lý ngay");
        btnProcess.addActionListener(e -> { if(navigationConsumer!=null) navigationConsumer.accept("OrdersView"); });
        alertBody.add(btnProcess, BorderLayout.SOUTH);
        
        overdueAlertPanel.add(alertBody, BorderLayout.CENTER);
        operationsRow.add(overdueAlertPanel, ogbc);
        mainContent.add(operationsRow);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(LibrisColors.CANVAS);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createChartWrapperPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1), BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        JLabel lbl = new JLabel(title); lbl.setFont(LibrisFonts.TITLE_MD); lbl.setForeground(LibrisColors.PRIMARY); p.add(lbl, BorderLayout.NORTH);
        return p;
    }

    @Override
    public void setStats(Map<String, Object> stats) {
        if (stats == null) return;
        cardTotalCustomers.setValue(String.valueOf(stats.getOrDefault("totalCustomers", 0)));
        cardRentedBooks.setValue(String.valueOf(stats.getOrDefault("rentingOrders", 0)));
        cardPendingOrders.setValue(String.valueOf(stats.getOrDefault("pendingOrders", 0)));
        cardOverdueBooks.setValue(String.valueOf(stats.getOrDefault("overdueBooks", 0)));
        
        int pending = (int) stats.getOrDefault("pendingOrders", 0);
        lblStickyText.setText(pending > 0 ? "<html>⚠️ <b>Chú ý:</b><br><br>Có " + pending + " đơn chờ duyệt.</html>" : "<html>✔️ Mọi thứ<br>đang ổn định.</html>");
    }

    @Override
    public void setTopBooks(List<Book> topBooks) { }

    @Override
    public void setTopBookStats(List<ReportStat> topBooks) {
        DefaultTableModel model = topBooksTable.getTableModel();
        model.setRowCount(0);
        if (topBooks != null) {
            for (ReportStat stat : topBooks) {
                String code = "", author = "", category = "";
                if (stat.getStringValue() != null) {
                    String[] parts = stat.getStringValue().split("\\|\\|");
                    if (parts.length == 3) { code = parts[0]; author = parts[1]; category = parts[2]; }
                }
                model.addRow(new Object[] { code, stat.getLabel(), author, category, stat.getValue() });
            }
        }
    }

    @Override
    public void updateCharts(List<ReportStat> revenueStats, List<ReportStat> categoryStats) {
        JPanel revenueContainer = (JPanel) chartRevenuePanel.getParent();
        if (revenueContainer != null) {
            revenueContainer.remove(chartRevenuePanel);
            chartRevenuePanel = ChartGenerator.createRevenueComboChart(revenueStats);
            revenueContainer.add(chartRevenuePanel, BorderLayout.CENTER);
            revenueContainer.revalidate(); revenueContainer.repaint();
        }
        JPanel donutContainer = (JPanel) chartDonutPanel.getParent();
        if (donutContainer != null) {
            donutContainer.remove(chartDonutPanel);
            chartDonutPanel = ChartGenerator.createCategoryDonutChartFromStats(categoryStats);
            donutContainer.add(chartDonutPanel, BorderLayout.CENTER);
            donutContainer.revalidate(); donutContainer.repaint();
        }
    }

    @Override
    public void setRecentOrders(List<RentalOrder> recentOrders) { }
    @Override
    public void showError(String message) { ToastPanel.showToast(SwingUtilities.getWindowAncestor(this), message, true); }
    public void refreshData() { if (controller != null) controller.loadDashboardData(); }
}