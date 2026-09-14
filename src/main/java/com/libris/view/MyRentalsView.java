package com.libris.view;

import com.libris.controller.RentController;
import com.libris.model.RentalOrder;
import com.libris.model.User;
import com.libris.view.components.*;
import com.libris.view.interfaces.IMyRentalsView;
import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MyRentalsView extends JPanel implements IMyRentalsView {
    private DataTablePanel activeTablePanel;
    private DataTablePanel historyTablePanel;
    
    private JButton btnReturnBook;
    private JPanel detailBody;
    private JLabel lblTotalCount, lblTotalRentalFee, lblTotalDeposit, lblGrandTotal, lblReturnDate;
    
    private User currentUser;
    private List<RentalOrder> rentalList = new ArrayList<>();
    private List<RentalOrder> activeOrdersList = new ArrayList<>();
    private RentalOrder selectedOrder;
    private RentController controller;
    private ActionListener returnListener;

    private StatCard cardHeldBooks;
    private StatCard cardDepositHold;
    private StatCard cardNearestDue;

    public MyRentalsView(User user) {
        this.currentUser = user;
        initComponents();
        this.controller = new RentController(this, currentUser, null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(LibrisColors.CANVAS);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout(16, 0));
        headerPanel.setOpaque(false);
        String username = currentUser != null ? currentUser.getUsername() : "customer1";
        JLabel lblTitle = new JLabel("<html><b>Độc giả: " + username + "</b> - Quản lý Trả sách & Lịch sử</html>");
        lblTitle.setFont(LibrisFonts.TITLE_LG);
        lblTitle.setForeground(LibrisColors.PRIMARY);
        
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnGroup.setOpaque(false);
        JButton btnPrintReceipt = FilterToolbar.createSecondaryButton("Lưu PDF/In Lịch sử");
        btnPrintReceipt.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            sb.append("========================================================\n");
            sb.append("              LỊCH SỬ HOÀN TẤT - LIBRIS                 \n");
            sb.append("========================================================\n");
            sb.append("Độc giả: ").append(currentUser.getUsername()).append("\n\n");
            
            for (RentalOrder o : rentalList) {
                if ("Returned".equalsIgnoreCase(o.getOrderStatus()) || "Rejected".equalsIgnoreCase(o.getOrderStatus())) {
                    sb.append("Mã đơn: #").append(o.getOrderCode()).append(" [").append(o.getOrderStatus().toUpperCase()).append("]\n");
                    sb.append("Ngày tạo: ").append(o.getRentDate() != null ? o.getRentDate() : "").append("\n");
                    sb.append("Sách:\n");
                    if (o.getDetails() != null) {
                        for (com.libris.model.RentalOrderDetail d : o.getDetails()) {
                            sb.append(" - ").append(d.getBook().getTitle()).append("\n");
                        }
                    }
                    sb.append("Tiền cọc: ").append(String.format("%,d VNĐ", o.getTotalDeposit())).append("\n");
                    sb.append("Phí thuê: ").append(String.format("%,d VNĐ", o.getTotalRentalFee())).append("\n");
                    sb.append("--------------------------------------------------------\n");
                }
            }
            com.libris.utils.PDFExporter.exportTextToPDF(this, sb.toString(), "Lịch sử Đơn hàng");
        });
        btnGroup.add(btnPrintReceipt);
        
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnGroup, BorderLayout.EAST);

        // 3 STAT CARDS
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        statsPanel.setOpaque(false);
        cardHeldBooks = new StatCard("TỔNG SÁCH ĐANG GIỮ", "0 cuốn", "Đang mượn", null, LibrisColors.PRIMARY);
        cardDepositHold = new StatCard("TỔNG CỌC ĐANG LƯU", "0 VNĐ", "Tại quỹ trung tâm", null, LibrisColors.STATUS_RENTED);
        cardNearestDue = new StatCard("HẠN TRẢ GẦN NHẤT", "Không có", "Lưu ý", null, LibrisColors.STATUS_WARNING);
        statsPanel.add(cardHeldBooks);
        statsPanel.add(cardDepositHold);
        statsPanel.add(cardNearestDue);

        // MAIN GRID (60% / 40%)
        JPanel mainGrid = new JPanel(new BorderLayout(16, 0));
        mainGrid.setOpaque(false);

        // TRÁI (Bảng Đơn đang mượn & Bảng Lịch sử)
        JPanel leftContainer = new JPanel(new GridLayout(2, 1, 0, 16));
        leftContainer.setOpaque(false);

        JPanel activeOrderCard = new JPanel(new BorderLayout(0, 10));
        activeOrderCard.setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        activeOrderCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1), BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        JLabel lblActiveTitle = new JLabel("CÁC ĐƠN ĐANG MƯỢN / CHỜ DUYỆT (CHỌN ĐỂ TRẢ)");
        lblActiveTitle.setFont(LibrisFonts.LABEL_SM);
        lblActiveTitle.setForeground(LibrisColors.PLACEHOLDER);
        activeOrderCard.add(lblActiveTitle, BorderLayout.NORTH);

        String[] actCols = { "STT", "Mã đơn", "Số sách", "Hẹn trả", "Trạng thái" };
        activeTablePanel = new DataTablePanel(actCols);
        activeTablePanel.setFooterVisible(false);
        activeTablePanel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = activeTablePanel.getTable().getSelectedRow();
                int modelRow = row >= 0 ? activeTablePanel.getTable().convertRowIndexToModel(row) : -1;
                if (modelRow >= 0 && modelRow < activeOrdersList.size()) {
                    selectedOrder = activeOrdersList.get(modelRow);
                    updateSettlementPanel(selectedOrder);
                }
            }
        });
        activeOrderCard.add(activeTablePanel, BorderLayout.CENTER);

        JPanel historyContainer = new JPanel(new BorderLayout(0, 8));
        historyContainer.setOpaque(false);
        JLabel lblHistoryTitle = new JLabel("LỊCH SỬ HOÀN TẤT");
        lblHistoryTitle.setFont(LibrisFonts.LABEL_SM);
        lblHistoryTitle.setForeground(LibrisColors.PLACEHOLDER);
        
        historyTablePanel = new DataTablePanel(new String[]{"STT", "Mã đơn", "Ngày nhận", "Hạn trả", "Trạng thái", "Tiền cọc", "Phí thuê"});
        historyTablePanel.setFooterVisible(false);
        
        historyContainer.add(lblHistoryTitle, BorderLayout.NORTH);
        historyContainer.add(historyTablePanel, BorderLayout.CENTER);

        leftContainer.add(activeOrderCard);
        leftContainer.add(historyContainer);

        // PHẢI (Chi tiết và nút trả sách)
        JPanel rightContainer = new JPanel();
        rightContainer.setLayout(new BoxLayout(rightContainer, BoxLayout.Y_AXIS));
        rightContainer.setOpaque(false);
        rightContainer.setPreferredSize(new Dimension(360, 0));

        JPanel settlementCard = new JPanel(new BorderLayout(0, 12));
        settlementCard.setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        settlementCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1), BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        
        JLabel lblSettlementTitle = new JLabel("CHI TIẾT TRẢ SÁCH & QUYẾT TOÁN");
        lblSettlementTitle.setFont(LibrisFonts.TITLE_MD);
        lblSettlementTitle.setForeground(LibrisColors.PRIMARY);
        
        detailBody = new JPanel();
        detailBody.setLayout(new BoxLayout(detailBody, BoxLayout.Y_AXIS));
        detailBody.setOpaque(false);
        
        JPanel summaryPanel = new JPanel(new GridLayout(5, 1, 0, 4));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, LibrisColors.HAIRLINE_BORDER), BorderFactory.createEmptyBorder(8, 0, 0, 0)));
        lblTotalCount = new JLabel("Số lượng tài liệu: 0 cuốn");
        lblTotalRentalFee = new JLabel("Phí thuê: 0 VNĐ");
        lblTotalDeposit = new JLabel("Tiền cọc sẽ hoàn: 0 VNĐ");
        lblReturnDate = new JLabel("Hạn trả: --");
        lblGrandTotal = new JLabel("TỔNG CẦN TRẢ: 0 VNĐ");
        lblGrandTotal.setFont(LibrisFonts.TITLE_LG);
        lblGrandTotal.setForeground(LibrisColors.PRIMARY);
        
        summaryPanel.add(lblTotalCount); summaryPanel.add(lblTotalRentalFee); summaryPanel.add(lblTotalDeposit); summaryPanel.add(lblReturnDate); summaryPanel.add(lblGrandTotal);

        btnReturnBook = FilterToolbar.createPrimaryButton("Thực hiện trả toàn bộ Đơn này");
        btnReturnBook.setBackground(LibrisColors.PRIMARY);
        btnReturnBook.setPreferredSize(new Dimension(0, 44));
        btnReturnBook.addActionListener(e -> {
            if (returnListener != null) returnListener.actionPerformed(e);
        });

        settlementCard.add(lblSettlementTitle, BorderLayout.NORTH);
        
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(new JScrollPane(detailBody) {{ setBorder(null); getViewport().setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST); }}, BorderLayout.CENTER);
        centerWrapper.add(summaryPanel, BorderLayout.SOUTH);

        settlementCard.add(centerWrapper, BorderLayout.CENTER);
        settlementCard.add(btnReturnBook, BorderLayout.SOUTH);

        rightContainer.add(settlementCard);

        mainGrid.add(leftContainer, BorderLayout.CENTER);
        mainGrid.add(rightContainer, BorderLayout.EAST);

        JPanel topSection = new JPanel(new BorderLayout(0, 16));
        topSection.setOpaque(false);
        topSection.add(headerPanel, BorderLayout.NORTH);
        topSection.add(statsPanel, BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);
        add(mainGrid, BorderLayout.CENTER);
    }

    private JPanel createRow(String label, String val) {
        JPanel r = new JPanel(new BorderLayout());
        r.setOpaque(false); r.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        JLabel l1 = new JLabel("<html><font color='#475569'>" + label + "</font></html>"); l1.setFont(LibrisFonts.BODY_SM);
        JLabel l2 = new JLabel(val); l2.setFont(LibrisFonts.TITLE_MD); l2.setForeground(LibrisColors.PRIMARY);
        r.add(l1, BorderLayout.WEST); r.add(l2, BorderLayout.EAST);
        return r;
    }

    private void updateSettlementPanel(RentalOrder o) {
        detailBody.removeAll();
        if (o != null) {
            // Render danh sách tên sách vào detailBody
            if (o.getDetails() != null) {
                for(com.libris.model.RentalOrderDetail d : o.getDetails()) {
                    detailBody.add(createRow("• " + d.getBook().getTitle(), ""));
                }
            }
            lblTotalCount.setText("Số lượng tài liệu: " + (o.getDetails() != null ? o.getDetails().size() : 0) + " cuốn");
            lblTotalRentalFee.setText(String.format("Phí thuê: %,d VNĐ", o.getTotalRentalFee()));
            lblTotalDeposit.setText(String.format("Tiền cọc sẽ hoàn: %,d VNĐ", o.getTotalDeposit()));
            lblGrandTotal.setText(String.format("TỔNG HÓA ĐƠN: %,d VNĐ", o.getTotalAmount()));
            lblReturnDate.setText("Hạn trả: " + (o.getExpectedReturnDate() != null ? o.getExpectedReturnDate().toString() : ""));
            if ("Pending".equalsIgnoreCase(o.getOrderStatus())) {
                btnReturnBook.setEnabled(true);
                btnReturnBook.setText("Hủy yêu cầu mượn này");
            } else if ("Renting".equalsIgnoreCase(o.getOrderStatus())) {
                btnReturnBook.setEnabled(true);
                btnReturnBook.setText("Thực hiện Trả Đơn Này");
            } else {
                btnReturnBook.setEnabled(false);
                btnReturnBook.setText("Đã hoàn tất");
            }
        } else {
            lblTotalCount.setText("Số lượng tài liệu: 0 cuốn");
            lblTotalRentalFee.setText("Phí thuê: 0 VNĐ");
            lblTotalDeposit.setText("Tiền cọc sẽ hoàn: 0 VNĐ");
            lblGrandTotal.setText("TỔNG HÓA ĐƠN: 0 VNĐ");
            lblReturnDate.setText("Hạn trả: --");
            btnReturnBook.setEnabled(false);
            btnReturnBook.setText("Chọn một đơn để trả");
        }
        detailBody.revalidate(); detailBody.repaint();
    }

    @Override
    public void showMyRentals(List<RentalOrder> rentals) {
        this.rentalList = rentals != null ? rentals : new ArrayList<>();
        activeOrdersList.clear();

        DefaultTableModel histModel = historyTablePanel.getTableModel(); histModel.setRowCount(0);
        DefaultTableModel actModel = activeTablePanel.getTableModel(); actModel.setRowCount(0);

        int heldBooks = 0; int depositHold = 0; String nearestDue = "";
        int sttHist = 1, sttAct = 1;

        for (RentalOrder o : rentalList) {
            if ("Returned".equalsIgnoreCase(o.getOrderStatus()) || "Rejected".equalsIgnoreCase(o.getOrderStatus())) {
                histModel.addRow(new Object[] { sttHist++, "#" + o.getOrderCode(), o.getRentDate() != null ? o.getRentDate().toString().substring(0,10) : "",
                        o.getExpectedReturnDate() != null ? o.getExpectedReturnDate().toString().substring(0,10) : "", o.getOrderStatus(), 
                        String.format("%,d VNĐ", o.getTotalDeposit()), String.format("%,d VNĐ", o.getTotalRentalFee()) });
            } else {
                activeOrdersList.add(o);
                if ("Renting".equalsIgnoreCase(o.getOrderStatus())) {
                    heldBooks += o.getDetails() != null ? o.getDetails().size() : 0;
                    depositHold += o.getTotalDeposit();
                    if (nearestDue.isEmpty() || (o.getExpectedReturnDate() != null && o.getExpectedReturnDate().toString().compareTo(nearestDue) < 0)) {
                        nearestDue = o.getExpectedReturnDate() != null ? o.getExpectedReturnDate().toString().substring(0, 10) : "";
                    }
                }
                String booksStr = o.getDetails() != null ? o.getDetails().size() + " cuốn" : "0 cuốn";
                actModel.addRow(new Object[] { sttAct++, "#" + o.getOrderCode(), booksStr, o.getExpectedReturnDate() != null ? o.getExpectedReturnDate().toString().substring(0,10) : "", o.getOrderStatus() });
            }
        }
        
        cardHeldBooks.setValue(heldBooks + " cuốn");
        cardDepositHold.setValue(String.format("%,d VNĐ", depositHold));
        cardNearestDue.setValue(nearestDue.isEmpty() ? "Không có" : nearestDue);

        if (!activeOrdersList.isEmpty()) {
            activeTablePanel.getTable().setRowSelectionInterval(0, 0); 
        } else {
            selectedOrder = null; updateSettlementPanel(null);
        }
    }

    @Override
    public void showError(String message) { ToastPanel.showToast(SwingUtilities.getWindowAncestor(this), message, true); }
    @Override
    public void showSuccessMessage(String message) { ToastPanel.showToast(SwingUtilities.getWindowAncestor(this), message, false); }
    @Override
    public RentalOrder getSelectedRentalOrder() { return selectedOrder; }
    @Override
    public void addReturnBookListener(ActionListener listener) { this.returnListener = listener; }

    public void refreshData() {
        if (controller != null) controller.loadMyRentals();
    }
}