package com.libris.view;

import com.libris.controller.RentalOrderController;
import com.libris.model.RentalOrder;
import com.libris.model.RentalOrderDetail;
import com.libris.view.components.*;
import com.libris.view.interfaces.IOrdersView;
import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class OrdersView extends JPanel implements IOrdersView {
    private FilterToolbar filterToolbar;
    private KpiTabBar statusPillTabs;
    private DataTablePanel tablePanel;
    private InspectorPanel inspectorPanel;

    private StatCard cardPending;
    private StatCard cardRenting;
    private StatCard cardReturned;
    private StatCard cardRejected;

    private JComboBox<String> cbStatusFilter;
    private List<RentalOrder> orderList = new ArrayList<>();
    private RentalOrder selectedOrder;
    private RentalOrderController controller;

    private ActionListener filterListener;
    private ActionListener searchListener;
    private ActionListener approveListener;
    private ActionListener rejectListener;
    private ActionListener returnListener;
    private Runnable selectOrderCallback;

    public OrdersView() {
        initComponents();
        this.controller = new RentalOrderController(this, null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(LibrisColors.CANVAS);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Top Process Status Progress Panel (4 KPI cards)
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        statsPanel.setOpaque(false);

        cardPending = new StatCard("CHỜ DUYỆT (PENDING)", "12", "Đơn trực tuyến chờ xử lý", null,
                LibrisColors.STATUS_WARNING);
        cardRenting = new StatCard("ĐANG MƯỢN (RENTING)", "45", "Lưu hành thực tế", null, LibrisColors.STATUS_RENTED);
        cardReturned = new StatCard("ĐÃ TRẢ SÁCH (RETURNED)", "128", "Đã quyết toán hoàn cọc", null,
                LibrisColors.STATUS_SUCCESS);
        cardRejected = new StatCard("BỊ TỪ CHỐI (REJECTED)", "6", "Vi phạm hoặc không đủ điều kiện", null,
                LibrisColors.STATUS_ERROR);

        statsPanel.add(cardPending);
        statsPanel.add(cardRenting);
        statsPanel.add(cardReturned);
        statsPanel.add(cardRejected);

        // Filter Toolbar & Status Pill Tabs
        filterToolbar = new FilterToolbar("Tìm theo mã đơn #0104, tên độc giả, SĐT...");
        cbStatusFilter = new JComboBox<>(new String[] { "Tất cả", "Pending", "Renting", "Returned", "Rejected" });
        filterToolbar.addFilterComboBox(cbStatusFilter);

        String[] pillTabs = { "Tất cả (191)", "Chờ duyệt (12)", "Đang mượn (45)", "Đã trả (128)", "Từ chối (6)" };
        statusPillTabs = new KpiTabBar(pillTabs, idx -> {
            String[] filterValues = { "Tất cả", "Pending", "Renting", "Returned", "Rejected" };
            cbStatusFilter.setSelectedItem(filterValues[idx]);
            if (filterListener != null) {
                filterListener.actionPerformed(null);
            }
        });

        JPanel toolbarContainer = new JPanel(new BorderLayout(0, 8));
        toolbarContainer.setOpaque(false);
        toolbarContainer.add(filterToolbar, BorderLayout.NORTH);
        toolbarContainer.add(statusPillTabs, BorderLayout.SOUTH);

        // Main Layout Grid (Table 65% / Inspector 35%)
        JPanel mainGrid = new JPanel(new BorderLayout(16, 0));
        mainGrid.setOpaque(false);

        String[] cols = { "Mã đơn", "Khách hàng", "Ngày mượn", "Hạn trả dự kiến", "Số lượng", "Tiền cọc",
                "Phí thuê", "Trạng thái" };
        tablePanel = new DataTablePanel(cols);

        tablePanel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablePanel.getTable().getSelectedRow();
                int modelRow = row >= 0 ? tablePanel.getTable().convertRowIndexToModel(row) : -1;
                if (modelRow >= 0 && modelRow < orderList.size()) {
                    selectedOrder = orderList.get(modelRow);
                    if (selectOrderCallback != null)
                        selectOrderCallback.run();
                    else
                        renderOrderInspector(selectedOrder, null);
                }
            }
        });

        inspectorPanel = new InspectorPanel("Biên bản xử lý & Giám định đơn mượn");

        mainGrid.add(tablePanel, BorderLayout.CENTER);
        mainGrid.add(inspectorPanel, BorderLayout.EAST);

        JPanel topContainer = new JPanel(new BorderLayout(0, 12));
        topContainer.setOpaque(false);
        topContainer.add(statsPanel, BorderLayout.NORTH);
        topContainer.add(toolbarContainer, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);
        add(mainGrid, BorderLayout.CENTER);
    }

    private void renderOrderInspector(RentalOrder order, List<RentalOrderDetail> details) {
        if (order == null) {
            inspectorPanel.clearContent();
            return;
        }

        String st = order.getOrderStatus() != null ? order.getOrderStatus() : "Pending";
        inspectorPanel.setInspectorTitle("Biên bản #" + order.getOrderCode() + " • " + st);
        inspectorPanel.setInspectorSubtitle(
                "Khách hàng: " + (order.getCustomerName() != null ? order.getCustomerName() : ""));

        JPanel p = inspectorPanel.getContentPanel();
        p.removeAll();

        p.add(createDetailRow("Trạng thái quy trình:", order.getOrderStatus()));
        p.add(createDetailRow("Ngày đăng ký mượn:", order.getRentDate() != null ? order.getRentDate().toString() : ""));
        p.add(createDetailRow("Hạn trả dự kiến:",
                order.getExpectedReturnDate() != null ? order.getExpectedReturnDate().toString() : ""));

        p.add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel lblSec = new JLabel("DANH SÁCH SÁCH YÊU CẦU TRONG ĐƠN");
        lblSec.setFont(LibrisFonts.LABEL_SM);
        lblSec.setForeground(LibrisColors.PLACEHOLDER);
        p.add(lblSec);

        if (details != null && !details.isEmpty()) {
            for (RentalOrderDetail d : details) {
                p.add(createBookStockRow(
                        d.getBook() != null ? d.getBook().getTitle() : ("Ấn phẩm ID #" + d.getBookId()),
                        "🟢 Tồn kho: Khả dụng (4 cuốn)"));
            }
        } else {
            p.add(createBookStockRow("Không có chi tiết sách", ""));
        }

        p.add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel lblFin = new JLabel("BẢNG KÊ TÀI CHÍNH CHI TIẾT");
        lblFin.setFont(LibrisFonts.LABEL_SM);
        lblFin.setForeground(LibrisColors.PLACEHOLDER);
        p.add(lblFin);

        p.add(createDetailRow("Tiền cọc thế chân (Hoàn trả 100%):", String.format("%,d VNĐ", order.getTotalDeposit())));
        p.add(createDetailRow("Phí thuê 14 ngày:", String.format("%,d VNĐ", order.getTotalRentalFee())));

        int totalSum = order.getTotalDeposit() + order.getTotalRentalFee() + order.getLateFee();
        p.add(createDetailRow("Tổng thực thu trước:", String.format("%,d VNĐ", totalSum)));

        JPanel actions = inspectorPanel.getActionPanel();
        actions.removeAll();

        if ("Pending".equalsIgnoreCase(st.trim())) {
            JButton btnApprove = FilterToolbar.createPrimaryButton("✓ Phê duyệt");
            btnApprove.setBackground(LibrisColors.STATUS_SUCCESS_TEXT);
            btnApprove.addActionListener(e -> {
                if (approveListener != null)
                    approveListener.actionPerformed(e);
            });

            JButton btnReject = FilterToolbar.createSecondaryButton("✕ Từ chối");
            btnReject.setForeground(LibrisColors.STATUS_ERROR_TEXT);
            btnReject.addActionListener(e -> {
                if (rejectListener != null)
                    rejectListener.actionPerformed(e);
            });

            actions.add(btnApprove);
            actions.add(btnReject);
            actions.revalidate(); 
            actions.repaint();
        } else if ("Renting".equalsIgnoreCase(st.trim())) {
            JPanel penaltyBox = new JPanel(new BorderLayout());
            penaltyBox.setBackground(LibrisColors.STATUS_RENTED_BG);
            penaltyBox.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(LibrisColors.STATUS_RENTED_BORDER, 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            JLabel lblCalc = new JLabel("⏱ Tính phạt trễ hạn: 0đ (Đang đúng hạn trả)");
            lblCalc.setFont(LibrisFonts.BODY_SM);
            lblCalc.setForeground(LibrisColors.STATUS_RENTED_TEXT);
            penaltyBox.add(lblCalc, BorderLayout.CENTER);
            p.add(penaltyBox);

            JButton btnReturn = FilterToolbar.createPrimaryButton("✓ Nhận trả sách & Hoàn cọc");
            btnReturn.setBackground(LibrisColors.PRIMARY);
            btnReturn.addActionListener(e -> {
                if (returnListener != null)
                    returnListener.actionPerformed(e);
            });
            actions.add(btnReturn);
        } else {
            JLabel lblArchived = new JLabel("📁 Đơn đã hoàn tất lưu trữ trong hệ thống.");
            lblArchived.setFont(LibrisFonts.BODY_SM);
            lblArchived.setForeground(LibrisColors.ON_SURFACE_VARIANT);
            actions.add(lblArchived);
        }

        inspectorPanel.revalidate();
        inspectorPanel.repaint();
    }

    private JPanel createBookStockRow(String title, String stockStatus) {
        JPanel r = new JPanel(new BorderLayout());
        r.setBackground(LibrisColors.CANVAS);
        r.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        JLabel l1 = new JLabel(
                "<html><b>" + title + "</b><br><font color='#047857' size='2'>" + stockStatus + "</font></html>");
        l1.setFont(LibrisFonts.BODY_SM);
        r.add(l1, BorderLayout.CENTER);
        return r;
    }

    private JPanel createDetailRow(String label, String val) {
        JPanel r = new JPanel(new BorderLayout());
        r.setOpaque(false);
        r.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        JLabel l1 = new JLabel(label);
        l1.setFont(LibrisFonts.BODY_SM);
        l1.setForeground(LibrisColors.ON_SURFACE_VARIANT);

        JLabel l2 = new JLabel(val != null ? val : "");
        l2.setFont(LibrisFonts.TITLE_MD);
        l2.setForeground(LibrisColors.PRIMARY);

        r.add(l1, BorderLayout.WEST);
        r.add(l2, BorderLayout.EAST);
        return r;
    }

    @Override
    public void updateKPIs(int pending, int renting, int returned, int rejected, int total) {
        cardPending.setValue(String.valueOf(pending));
        cardRenting.setValue(String.valueOf(renting));
        cardReturned.setValue(String.valueOf(returned));
        cardRejected.setValue(String.valueOf(rejected));
        
        statusPillTabs.updateTabLabels(new String[] {
            "Tất cả (" + total + ")", 
            "Chờ duyệt (" + pending + ")", 
            "Đang mượn (" + renting + ")", 
            "Đã trả (" + returned + ")", 
            "Từ chối (" + rejected + ")"
        });
    }

    @Override
    public void showOrders(List<RentalOrder> orders) {
       
        this.orderList = orders != null ? orders : new ArrayList<>();
        DefaultTableModel model = tablePanel.getTableModel();
        model.setRowCount(0);
        for (RentalOrder o : orderList) {
            model.addRow(new Object[] { 
                "#" + o.getOrderCode(), 
                o.getCustomerName() != null ? o.getCustomerName() : "",
                o.getRentDate() != null ? o.getRentDate().toString() : "",
                o.getExpectedReturnDate() != null ? o.getExpectedReturnDate().toString() : "", 
                o.getDetailCount(),
                String.format("%,d VNĐ", o.getTotalDeposit()), 
                String.format("%,d VNĐ", o.getTotalRentalFee()),
                o.getOrderStatus() 
            });
        }
        tablePanel.updateFooterCount();
    }

    @Override
    public void showOrderDetails(RentalOrder order, List<RentalOrderDetail> details) {
        renderOrderInspector(order, details);
    }

    @Override
    public void showError(String message) {
        ToastPanel.showToast(SwingUtilities.getWindowAncestor(this), message, true);
    }

    @Override
    public void showSuccessMessage(String message) {
        ToastPanel.showToast(SwingUtilities.getWindowAncestor(this), message, false);
    }

    @Override
    public RentalOrder getSelectedOrder() {
        return selectedOrder;
    }

    @Override
    public String getStatusFilter() {
        return (String) cbStatusFilter.getSelectedItem();
    }

    @Override
    public String getSearchKeyword() {
        return filterToolbar.getSearchField().getText().trim();
    }

    @Override
    public String getRejectReason() {
        return JOptionPane.showInputDialog(this, "Nhập lý do từ chối đơn mượn sách này:", "Biên bản từ chối đơn",
                JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void addFilterListener(ActionListener listener) {
        this.filterListener = listener;
        cbStatusFilter.addActionListener(listener);
    }

    @Override
    public void addSearchListener(ActionListener listener) {
        this.searchListener = listener;
        filterToolbar.getSearchField().addActionListener(listener);
    }

    @Override
    public void addApproveOrderListener(ActionListener listener) {
        this.approveListener = listener;
    }

    @Override
    public void addRejectOrderListener(ActionListener listener) {
        this.rejectListener = listener;
    }

    @Override
    public void addReturnOrderListener(ActionListener listener) {
        this.returnListener = listener;
    }

    @Override
    public void addSelectOrderListener(Runnable callback) {
        this.selectOrderCallback = callback;
    }

    public void refreshData() {
        if (controller != null) {
            controller.loadOrders();
        }
    }
}
