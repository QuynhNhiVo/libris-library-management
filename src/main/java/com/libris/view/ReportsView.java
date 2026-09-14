package com.libris.view;

import com.libris.controller.ReportController;
import com.libris.model.RentalOrder;
import com.libris.model.ReportStat;
import com.libris.utils.ChartGenerator;
import com.libris.view.components.*;
import com.libris.view.interfaces.IReportsView;
import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class ReportsView extends JPanel implements IReportsView {
    private JComboBox<String> cbPeriod;
    private JComboBox<String> cbReportType;
    private JButton btnExportExcel;
    private JButton btnExportPDF;
    private StatCard cardRevenue, cardDeposit, cardRentals, cardOnTimeRate;
    private KpiTabBar subTabBar;
    private JPanel chartAreaPanel;
    private DataTablePanel tablePanel;
    private JPanel topBooksBody, topUsersBody;
    private JLabel lblWarn;
    private ReportController controller;
    private ActionListener typeChangeListener, exportExcelListener;
    private File exportFile;

    public ReportsView() {
        initComponents();
        this.controller = new ReportController(this, null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(LibrisColors.CANVAS);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel toolbarPanel = new JPanel(new BorderLayout(12, 0));
        toolbarPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("Báo cáo & Thống kê");
        lblTitle.setFont(LibrisFonts.DISPLAY_MD);
        lblTitle.setForeground(LibrisColors.PRIMARY);

        JPanel filterGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterGroup.setOpaque(false);
        cbPeriod = new JComboBox<>(new String[] { "Năm 2026", "Năm 2025" });
        cbPeriod.setFont(LibrisFonts.BODY_MD);

        String[] reportOptions = { "Doanh thu & Dòng tiền", "Tần suất & Xu hướng sách", "Độc giả tích cực", "Thống kê quá hạn & Vi phạm" };
        cbReportType = new JComboBox<>(reportOptions);
        cbReportType.setFont(LibrisFonts.BODY_MD);

        cbPeriod.addActionListener(e -> refreshData());

        btnExportExcel = FilterToolbar.createPrimaryButton("Xuất Excel");
        btnExportExcel.setBackground(LibrisColors.STATUS_SUCCESS_TEXT);
        btnExportExcel.addActionListener(e -> {
            if (exportExcelListener != null) exportExcelListener.actionPerformed(e);
            else handleExportExcel();
        });

        // NÚT PDF
        btnExportPDF = FilterToolbar.createSecondaryButton("Xuất PDF / In");
        btnExportPDF.addActionListener(e -> {
            String title = cbReportType.getSelectedItem().toString() + " - " + cbPeriod.getSelectedItem().toString();
            com.libris.utils.PDFExporter.exportTableToPDF(this, tablePanel.getTable(), title);
        });

        filterGroup.add(cbPeriod); filterGroup.add(cbReportType); filterGroup.add(btnExportExcel); filterGroup.add(btnExportPDF);
        toolbarPanel.add(lblTitle, BorderLayout.WEST); toolbarPanel.add(filterGroup, BorderLayout.EAST);

        // KPI CARDS
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        statsPanel.setOpaque(false);
        cardRevenue = new StatCard("DOANH THU", "0 VNĐ", "Năm hiện tại", null, LibrisColors.PRIMARY);
        cardDeposit = new StatCard("TIỀN CỌC GIỮ", "0 VNĐ", "Đang lưu giữ", null, LibrisColors.STATUS_RENTED);
        cardRentals = new StatCard("TỔNG SỐ LƯỢT THUÊ", "0", "Đơn hàng", null, LibrisColors.ACCENT_BLUE);
        cardOnTimeRate = new StatCard("TỶ LỆ ĐÚNG HẠN", "0%", "Hoàn trả đúng hạn", null, LibrisColors.STATUS_SUCCESS);
        statsPanel.add(cardRevenue); statsPanel.add(cardDeposit); statsPanel.add(cardRentals); statsPanel.add(cardOnTimeRate);

        // MAIN GRID
        JPanel mainGrid = new JPanel(new BorderLayout(16, 0));
        mainGrid.setOpaque(false);

        // LEFT
        JPanel leftContainer = new JPanel(new BorderLayout(0, 12));
        leftContainer.setOpaque(false);
        subTabBar = new KpiTabBar(reportOptions, idx -> cbReportType.setSelectedIndex(idx));

        // === SCROLL TABS MỚI ===
        JScrollPane scrollTabs = new JScrollPane(
                subTabBar,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        scrollTabs.setBorder(null);
        scrollTabs.setOpaque(false);
        scrollTabs.getViewport().setOpaque(false);

        // Không để scrollbar sát tab
        scrollTabs.setViewportBorder(
                BorderFactory.createEmptyBorder(0, 0, 6, 0)
        );

        // Horizontal scrollbar
        JScrollBar horizontalBar = scrollTabs.getHorizontalScrollBar();

        horizontalBar.setUI(new ModernHorizontalScrollBarUI());
        horizontalBar.setPreferredSize(new Dimension(0, 8));
        horizontalBar.setUnitIncrement(24);
        horizontalBar.setBlockIncrement(120);
        horizontalBar.setOpaque(false);

        // Shift + lăn chuột = scroll ngang
        scrollTabs.addMouseWheelListener(e -> {
            if (e.isShiftDown()) {
                int amount = e.getWheelRotation()
                        * horizontalBar.getUnitIncrement();

                horizontalBar.setValue(
                        horizontalBar.getValue() + amount
                );

                e.consume();
            }
        });
        // =======================

        chartAreaPanel = new JPanel(new BorderLayout());
        chartAreaPanel.setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        chartAreaPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1), BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        tablePanel = new DataTablePanel(new String[]{"Cột 1", "Cột 2"});
        tablePanel.setFooterVisible(false); // Ẩn Footer

        JPanel leftBody = new JPanel(new GridLayout(2, 1, 0, 12));
        leftBody.setOpaque(false);
        leftBody.add(chartAreaPanel); leftBody.add(tablePanel);

        leftContainer.add(scrollTabs, BorderLayout.NORTH);
        leftContainer.add(leftBody, BorderLayout.CENTER);

        // RIGHT SUMMARY
        JPanel rightSummaryPanel = new JPanel();
        rightSummaryPanel.setLayout(new BoxLayout(rightSummaryPanel, BoxLayout.Y_AXIS));
        rightSummaryPanel.setOpaque(false);
        rightSummaryPanel.setPreferredSize(new Dimension(340, 0));

        JPanel cardTopBooks = createCardWrapper("TOP 3 SÁCH MƯỢN NHIỀU NHẤT NĂM");
        topBooksBody = new JPanel(new GridLayout(3, 1, 0, 8)); topBooksBody.setOpaque(false); cardTopBooks.add(topBooksBody, BorderLayout.CENTER);

        JPanel cardTopUsers = createCardWrapper("TOP 3 ĐỘC GIẢ TÍCH CỰC");
        topUsersBody = new JPanel(new GridLayout(3, 1, 0, 8)); topUsersBody.setOpaque(false); cardTopUsers.add(topUsersBody, BorderLayout.CENTER);

        JPanel cardOverdueNotice = createCardWrapper("CẢNH BÁO QUÁ HẠN");
        cardOverdueNotice.setBackground(LibrisColors.STATUS_ERROR_BG);
        cardOverdueNotice.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LibrisColors.STATUS_ERROR_BORDER, 1), BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        lblWarn = new JLabel("Đang tải...");
        lblWarn.setFont(LibrisFonts.TITLE_MD); lblWarn.setForeground(LibrisColors.STATUS_ERROR_TEXT);
        cardOverdueNotice.add(lblWarn, BorderLayout.CENTER);

        rightSummaryPanel.add(cardTopBooks); rightSummaryPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        rightSummaryPanel.add(cardTopUsers); rightSummaryPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        rightSummaryPanel.add(cardOverdueNotice);

        mainGrid.add(leftContainer, BorderLayout.CENTER);
        mainGrid.add(rightSummaryPanel, BorderLayout.EAST);

        JPanel topSection = new JPanel(new BorderLayout(0, 16));
        topSection.setOpaque(false);
        topSection.add(toolbarPanel, BorderLayout.NORTH); topSection.add(statsPanel, BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);
        add(mainGrid, BorderLayout.CENTER);
    }

    private JPanel createCardWrapper(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1), BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JLabel lbl = new JLabel(title); lbl.setFont(LibrisFonts.LABEL_SM); lbl.setForeground(LibrisColors.PRIMARY); p.add(lbl, BorderLayout.NORTH); return p;
    }

    private JPanel createRankItem(String title, String desc) {
        JPanel r = new JPanel(new BorderLayout()); r.setOpaque(false);
        JLabel l1 = new JLabel("<html><b>" + title + "</b><br><font color='#475569' size='2'>" + desc + "</font></html>"); l1.setFont(LibrisFonts.BODY_SM);
        r.add(l1, BorderLayout.CENTER); return r;
    }

    @Override
    public File getExportFile() { return exportFile; }

    @Override
    public void handleExportExcel() {
        JFileChooser chooser = new JFileChooser();
        String yr = cbPeriod.getSelectedItem().toString().replaceAll("\\D+", "");
        chooser.setSelectedFile(new File("BaoCao_Libris_" + yr + ".xlsx"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            exportFile = chooser.getSelectedFile();
            if (exportExcelListener != null) exportExcelListener.actionPerformed(null);
        } else {
            exportFile = null;
        }
    }

    @Override
    public void showMonthlyRevenue(List<ReportStat> stats) {
        tablePanel.updateColumns(new String[] { "Tháng", "Doanh thu (VNĐ)", "Số đơn mượn" });
        DefaultTableModel model = tablePanel.getTableModel();
        model.setRowCount(0);
        if (stats != null) {
            for (ReportStat s : stats) {
                model.addRow(new Object[] { s.getLabel(), String.format("%,.0f VNĐ", s.getDoubleValue()), s.getValue() });
            }
        }
        chartAreaPanel.removeAll();
        chartAreaPanel.add(ChartGenerator.createRevenueComboChart(stats), BorderLayout.CENTER);
        chartAreaPanel.revalidate(); chartAreaPanel.repaint();
    }

    @Override
    public void showTopBooks(List<ReportStat> stats) {
        tablePanel.updateColumns(new String[] { "Tên sách", "Lượt mượn", "Doanh thu (VNĐ)" });
        DefaultTableModel model = tablePanel.getTableModel();
        model.setRowCount(0);
        if (stats != null) {
            for (ReportStat s : stats) {
                model.addRow(new Object[] { s.getLabel(), s.getValue(), String.format("%,.0f VNĐ", s.getDoubleValue()) });
            }
        }
        chartAreaPanel.removeAll();
        chartAreaPanel.add(ChartGenerator.createHorizontalCategoryBarChart(stats, "Lượt mượn"), BorderLayout.CENTER);
        chartAreaPanel.revalidate(); chartAreaPanel.repaint();
    }

    @Override
    public void showCategoryStats(List<ReportStat> stats) { }

    @Override
    public void showTopCustomers(List<ReportStat> stats) {
        tablePanel.updateColumns(new String[] { "Tên khách hàng", "Số đơn mượn", "Tổng chi tiêu (VNĐ)" });
        DefaultTableModel model = tablePanel.getTableModel();
        model.setRowCount(0);
        if (stats != null) {
            for (ReportStat s : stats) {
                model.addRow(new Object[] { s.getLabel(), s.getValue(), String.format("%,.0f VNĐ", s.getDoubleValue()) });
            }
        }
        chartAreaPanel.removeAll();
        chartAreaPanel.add(ChartGenerator.createHorizontalCategoryBarChart(stats, "Số đơn mượn"), BorderLayout.CENTER);
        chartAreaPanel.revalidate(); chartAreaPanel.repaint();
    }

    @Override
    public void showOverdueOrders(List<ReportStat> chartStats, List<RentalOrder> orders) {
        tablePanel.updateColumns(new String[] { "Mã đơn", "Khách hàng", "Hạn trả", "Trạng thái", "Phân loại" });
        DefaultTableModel model = tablePanel.getTableModel();
        model.setRowCount(0);
        if (orders != null) {
            for (RentalOrder o : orders) {
                model.addRow(new Object[] {
                        o.getOrderCode(),
                        o.getCustomerName() != null ? o.getCustomerName() : "ID #" + o.getCustomerId(),
                        o.getExpectedReturnDate() != null ? o.getExpectedReturnDate().toString().substring(0,10) : "",
                        o.getOrderStatus(),
                        o.getCustomerCode()
                });
            }
        }
        chartAreaPanel.removeAll();
        chartAreaPanel.add(ChartGenerator.createCategoryDonutChartFromStats(chartStats), BorderLayout.CENTER);
        chartAreaPanel.revalidate(); chartAreaPanel.repaint();
    }

    @Override
    public void showError(String message) {
        boolean isErr = !message.toLowerCase().contains("thành công");
        ToastPanel.showToast(SwingUtilities.getWindowAncestor(this), message, isErr);
    }
    @Override
    public String getSelectedReportType() { return (String) cbReportType.getSelectedItem(); }

    @Override
    public void addReportTypeChangeListener(ActionListener listener) { this.typeChangeListener = listener; cbReportType.addActionListener(listener); }
   
    @Override
    public void addExportExcelListener(ActionListener listener) { this.exportExcelListener = listener; }
    
    @Override
    public String getPeriodFilter() { return (String) cbPeriod.getSelectedItem(); }

    @Override
    public void updateKPIs(double revenue, double deposit, int rentals, double onTimeRate) {
        cardRevenue.setValue(String.format("%,.0f đ", revenue));
        cardDeposit.setValue(String.format("%,.0f đ", deposit));
        cardRentals.setValue(String.valueOf(rentals));
        cardOnTimeRate.setValue(String.format("%.1f%%", onTimeRate));
    }

    @Override
    public void updateRightSummary(List<ReportStat> topBooks, List<ReportStat> topUsers, int overdueCount) {
        topBooksBody.removeAll();
        if (topBooks != null) {
            for (int i = 0; i < Math.min(3, topBooks.size()); i++) {
                topBooksBody.add(createRankItem(topBooks.get(i).getLabel(), "Đã thuê: " + topBooks.get(i).getValue() + " lượt"));
            }
        }
        topBooksBody.revalidate(); topBooksBody.repaint();
        topUsersBody.removeAll();
        if (topUsers != null) {
            for (int i = 0; i < Math.min(3, topUsers.size()); i++) {
                topUsersBody.add(createRankItem(topUsers.get(i).getLabel(), "Tổng đơn: " + topUsers.get(i).getValue() + " đơn"));
            }
        }
        topUsersBody.revalidate(); topUsersBody.repaint();
        lblWarn.setText(overdueCount > 0 ? "CÓ " + overdueCount + " ĐƠN QUÁ HẠN!" : "An toàn, không có đơn quá hạn.");
        lblWarn.setForeground(overdueCount > 0 ? LibrisColors.STATUS_ERROR_TEXT : LibrisColors.STATUS_SUCCESS_TEXT);
    }

    // ==================== MODERN SCROLLBAR UI ====================
    private static class ModernHorizontalScrollBarUI extends BasicScrollBarUI {

        private static final int ARC = 8;

        @Override
        protected void configureScrollBarColors() {
            trackColor = LibrisColors.CANVAS;
            thumbColor = LibrisColors.HAIRLINE_BORDER;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createInvisibleButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createInvisibleButton();
        }

        private JButton createInvisibleButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int height = 4;
            int y = trackBounds.y + (trackBounds.height - height) / 2;

            g2.setColor(LibrisColors.CANVAS);
            g2.fillRoundRect(trackBounds.x, y, trackBounds.width, height, ARC, ARC);
            g2.dispose();
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int height = 5;
            int y = thumbBounds.y + (thumbBounds.height - height) / 2;

            if (isDragging || isThumbRollover()) {
                g2.setColor(LibrisColors.PRIMARY);
            } else {
                g2.setColor(LibrisColors.HAIRLINE_BORDER);
            }

            g2.fillRoundRect(thumbBounds.x, y, thumbBounds.width, height, ARC, ARC);
            g2.dispose();
        }

        @Override
        protected Dimension getMinimumThumbSize() {
            return new Dimension(40, 5);
        }
    }
    // =============================================================

    public void refreshData() {
        if (controller != null) {
            subTabBar.setSelectedIndex(cbReportType.getSelectedIndex());
            controller.loadReportData();
        }
    }
}