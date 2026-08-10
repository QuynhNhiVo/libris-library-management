package com.libris.ui;

import com.libris.controller.RentController;
import com.libris.model.RentalOrder;
import com.libris.model.RentalOrderDetail;
import com.libris.model.User;
import com.libris.utils.IconUtils;
import com.libris.config.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MyRentalsView extends JPanel {
    private RentController rentController;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField tfSearch;
    private JComboBox<String> cbStatus;
    
    private User currentUser;
    private int customerId;
    private List<RentalOrder> currentFilteredOrders = new ArrayList<>();
    private List<RentalOrder> selectedOrdersForReturn = new ArrayList<>();

    private JLabel lblBillOrders, lblBillDates, lblBillBooks, lblBillRental, lblBillDeposit, lblBillTotal;
    private JButton btnReturnAction;
    private JPanel billDetailCard;

    public MyRentalsView(com.libris.model.User user) {
        rentController = new RentController();
        this.currentUser = user;
        this.customerId = resolveCustomerId(user); 
        
        initComponents();
        loadData();
    }

    private int resolveCustomerId(com.libris.model.User user) {
        if (user == null) return 1;
        if (user.getUsername() != null) {
            if (user.getUsername().equalsIgnoreCase("customer1")) return 1;
            if (user.getUsername().equalsIgnoreCase("customer2")) return 2;
        }
        return user.getUserId(); 
    }

    public void refreshData() {
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        setBackground(new Color(245, 247, 250));

        // 1. HEADER TIÊU ĐỀ
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Sách Đang Thuê");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(30, 41, 59));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // 2. KHUNG NỘI DUNG CHÍNH
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setOpaque(false);

        // --- KHỐI 1: CARD BẢNG VÀ BỘ LỌC ---
        JPanel cardTablePanel = new RoundedPanel(16, Color.WHITE);
        cardTablePanel.setLayout(new BorderLayout(0, 15));
        cardTablePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cardTablePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterBar.setOpaque(false);

        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(1, 2, w - 2, h - 1, 24, 24);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, w - 2, h - 2, 24, 24);
                g2.setColor(new Color(226, 232, 240));
                g2.drawRoundRect(0, 0, w - 2, h - 2, 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        searchBox.setOpaque(false);
        searchBox.setPreferredSize(new Dimension(320, 40));

        JLabel lblSearchIcon = new JLabel();
        lblSearchIcon.setIcon(IconUtils.loadIconForComponent(Constants.IC_SEARCH, lblSearchIcon));
        lblSearchIcon.setPreferredSize(new Dimension(20, 38));
        lblSearchIcon.setHorizontalAlignment(SwingConstants.CENTER);

        tfSearch = new JTextField(19) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(160, 174, 192));
                    g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                    FontMetrics fm = g2.getFontMetrics();
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString("Tìm kiếm theo mã đơn, tên sách...", 0, y);
                    g2.dispose();
                }
            }
        };
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tfSearch.setBorder(BorderFactory.createEmptyBorder());
        tfSearch.setOpaque(false);
        tfSearch.setPreferredSize(new Dimension(235, 38));

        searchBox.add(lblSearchIcon);
        searchBox.add(tfSearch);

        cbStatus = new JComboBox<>(new String[] { "Tất cả trạng thái", "Pending", "Renting", "Returned", "Rejected" });
        cbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbStatus.setPreferredSize(new Dimension(160, 40));

        filterBar.add(searchBox);
        filterBar.add(cbStatus);
        cardTablePanel.add(filterBar, BorderLayout.NORTH);

        String[] columns = { "Mã đơn", "Sách thuê", "Ngày thuê", "Hạn trả", "Trạng thái", "Tổng tiền", "Chọn" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return column == 6; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 6 ? Boolean.class : String.class;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(42);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setPreferredSize(new Dimension(0, 42));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(6).setMaxWidth(60);
        table.getColumnModel().getColumn(6).setMinWidth(50);
        table.getColumnModel().getColumn(6).setCellRenderer(table.getDefaultRenderer(Boolean.class));

        tableModel.addTableModelListener(e -> {
            if (e.getColumn() == 6) {
                updateSelectedOrdersFromTable();
            }
        });

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        cardTablePanel.add(scrollPane, BorderLayout.CENTER);

        mainContentPanel.add(cardTablePanel);
        mainContentPanel.add(Box.createVerticalStrut(20));

        // --- KHỐI 2: TRẢ SÁCH ---
        JPanel cardReturnPanel = new RoundedPanel(16, Color.WHITE);
        cardReturnPanel.setLayout(new BorderLayout(0, 15));
        cardReturnPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cardReturnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

        JLabel lblReturnHeader = new JLabel("Trả Sách");
        lblReturnHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblReturnHeader.setForeground(new Color(30, 41, 59));
        cardReturnPanel.add(lblReturnHeader, BorderLayout.NORTH);

        billDetailCard = new JPanel(new GridBagLayout());
        billDetailCard.setBackground(new Color(248, 250, 252));
        billDetailCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        billDetailCard.add(createBillLabel("Số lượng đơn chọn:", Font.PLAIN), gbc);
        gbc.gridx = 1;
        lblBillOrders = createBillLabel("0 đơn", Font.BOLD);
        billDetailCard.add(lblBillOrders, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        billDetailCard.add(createBillLabel("Mã đơn đã chọn:", Font.PLAIN), gbc);
        gbc.gridx = 1;
        lblBillDates = createBillLabel("Chưa chọn đơn nào", Font.PLAIN);
        billDetailCard.add(lblBillDates, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        billDetailCard.add(createBillLabel("Danh sách sách trả:", Font.PLAIN), gbc);
        gbc.gridx = 1;
        lblBillBooks = createBillLabel("---", Font.PLAIN);
        billDetailCard.add(lblBillBooks, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        billDetailCard.add(createBillLabel("Tổng tiền thuê:", Font.PLAIN), gbc);
        gbc.gridx = 1;
        lblBillRental = createBillLabel("0 đ", Font.PLAIN);
        billDetailCard.add(lblBillRental, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        billDetailCard.add(createBillLabel("Tổng tiền cọc / Phí phạt:", Font.PLAIN), gbc);
        gbc.gridx = 1;
        lblBillDeposit = createBillLabel("0 đ", Font.PLAIN);
        billDetailCard.add(lblBillDeposit, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        billDetailCard.add(createBillLabel("Tổng thanh toán / Hoàn trả:", Font.BOLD), gbc);
        gbc.gridx = 1;
        lblBillTotal = createBillLabel("0 đ", Font.BOLD);
        lblBillTotal.setForeground(new Color(37, 99, 235));
        billDetailCard.add(lblBillTotal, gbc);

        cardReturnPanel.add(billDetailCard, BorderLayout.CENTER);

        // Nút Xác nhận trả sách 
        JPanel returnBottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        returnBottomPanel.setOpaque(false);

        btnReturnAction = new JButton("Xác Nhận Trả Sách (0)") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btnReturnAction.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReturnAction.setForeground(Color.WHITE);
        btnReturnAction.setBackground(new Color(22, 163, 74)); // Xanh lá chủ đạo
        btnReturnAction.setContentAreaFilled(false);
        btnReturnAction.setBorderPainted(false);
        btnReturnAction.setFocusPainted(false);
        btnReturnAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReturnAction.setPreferredSize(new Dimension(210, 38));
        btnReturnAction.setEnabled(false);
        btnReturnAction.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if(btnReturnAction.isEnabled()) btnReturnAction.setBackground(new Color(21, 128, 61)); }
            @Override public void mouseExited(MouseEvent e) { if(btnReturnAction.isEnabled()) btnReturnAction.setBackground(new Color(22, 163, 74)); }
        });
        btnReturnAction.addActionListener(e -> executeReturnMultipleBooks());

        returnBottomPanel.add(btnReturnAction);
        cardReturnPanel.add(returnBottomPanel, BorderLayout.SOUTH);

        mainContentPanel.add(cardReturnPanel);

        // JScrollPane cuộn toàn bộ màn hình
        JScrollPane mainScroll = new JScrollPane(mainContentPanel);
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        mainScroll.getViewport().setBackground(new Color(245, 247, 250));
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);

        add(mainScroll, BorderLayout.CENTER);
        setupEvents();
    }

    private JLabel createBillLabel(String text, int style) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", style, 13));
        lbl.setForeground(new Color(71, 85, 105));
        return lbl;
    }

    private void setupEvents() {
        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
        });
        cbStatus.addActionListener(e -> filter());
    }

    private void loadData() {
        try {
            List<RentalOrder> orders = rentController.getCustomerRentals(customerId);
            if (orders != null) {
                currentFilteredOrders = new ArrayList<>(orders);
            } else {
                currentFilteredOrders.clear();
            }
            renderTable();
            updateBillSummary();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filter() {
        String searchText = tfSearch.getText().toLowerCase().trim();
        String statusFilter = cbStatus.getSelectedItem() != null ? cbStatus.getSelectedItem().toString() : "Tất cả trạng thái";

        try {
            List<RentalOrder> orders = rentController.getCustomerRentals(customerId);
            currentFilteredOrders.clear();
            if (orders != null) {
                for (RentalOrder order : orders) {
                    StringBuilder booksStr = new StringBuilder();
                    if (order.getDetails() != null) {
                        for (RentalOrderDetail d : order.getDetails()) {
                            if (d.getBook() != null) booksStr.append(d.getBook().getTitle()).append(" ");
                        }
                    }

                    boolean matchSearch = searchText.isEmpty()
                            || (order.getOrderCode() != null && order.getOrderCode().toLowerCase().contains(searchText))
                            || booksStr.toString().toLowerCase().contains(searchText);

                    boolean matchStatus = statusFilter.equals("Tất cả trạng thái")
                            || (order.getOrderStatus() != null && order.getOrderStatus().equals(statusFilter));

                    if (matchSearch && matchStatus) {
                        currentFilteredOrders.add(order);
                    }
                }
            }
            renderTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderTable() {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (RentalOrder order : currentFilteredOrders) {
            boolean isChecked = selectedOrdersForReturn.stream().anyMatch(o -> o.getOrderId() == order.getOrderId());
            
            StringBuilder books = new StringBuilder();
            if (order.getDetails() != null) {
                for (RentalOrderDetail detail : order.getDetails()) {
                    if (books.length() > 0) books.append(", ");
                    books.append(detail.getBook() != null ? detail.getBook().getTitle() : "Unknown");
                }
            }

            tableModel.addRow(new Object[] {
                    order.getOrderCode(),
                    books.toString(),
                    order.getRentDate() != null ? order.getRentDate().format(formatter) : "",
                    order.getExpectedReturnDate() != null ? order.getExpectedReturnDate().format(formatter) : "",
                    order.getOrderStatus(),
                    String.format("%,d đ", order.getTotalAmount()),
                    isChecked
            });
        }
    }

    private void updateSelectedOrdersFromTable() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean checked = (Boolean) tableModel.getValueAt(i, 6);
            String orderCode = tableModel.getValueAt(i, 0).toString();

            RentalOrder targetOrder = currentFilteredOrders.stream()
                    .filter(o -> o.getOrderCode().equals(orderCode))
                    .findFirst().orElse(null);

            if (targetOrder != null) {
                boolean exists = selectedOrdersForReturn.stream().anyMatch(o -> o.getOrderId() == targetOrder.getOrderId());
                if (checked && !exists) {
                    selectedOrdersForReturn.add(targetOrder);
                } else if (!checked && exists) {
                    selectedOrdersForReturn.removeIf(o -> o.getOrderId() == targetOrder.getOrderId());
                }
            }
        }
        updateBillSummary();
    }


    private void updateBillSummary() {
        int count = selectedOrdersForReturn.size();
        if (count == 0) {
            lblBillOrders.setText("0 đơn");
            lblBillDates.setText("Chưa chọn đơn nào");
            lblBillBooks.setText("---");
            lblBillRental.setText("0 đ");
            lblBillDeposit.setText("0 đ");
            lblBillTotal.setText("0 đ");
            btnReturnAction.setText("Xác Nhận Trả Sách (0)");
            btnReturnAction.setEnabled(false);
            btnReturnAction.setBackground(new Color(148, 163, 184));
            return;
        }

        StringBuilder codes = new StringBuilder();
        StringBuilder allBooks = new StringBuilder();
        int totalRental = 0;
        int totalDepositToRefund = 0;
        int totalLateFee = 0;

        java.time.LocalDate today = java.time.LocalDate.now();

        for (RentalOrder o : selectedOrdersForReturn) {
            if (codes.length() > 0) codes.append(", ");
            codes.append(o.getOrderCode());

            if (o.getDetails() != null) {
                for (RentalOrderDetail d : o.getDetails()) {
                    if (allBooks.length() > 0) allBooks.append("; ");
                    allBooks.append(d.getBook() != null ? d.getBook().getTitle() : "");
                }
            }

            totalRental += o.getTotalRentalFee();
            
            totalDepositToRefund += o.getTotalDeposit();

            // Tính toán tự động phí trễ hạn 
            if (o.getExpectedReturnDate() != null) {
                java.time.LocalDate expDate = o.getExpectedReturnDate().toLocalDate();
                if (today.isAfter(expDate)) {
                    long daysLate = java.time.temporal.ChronoUnit.DAYS.between(expDate, today);
                    int finePerDay = 5000; 
                    int calculatedLateFee = (int) (daysLate * finePerDay);
                    totalLateFee += Math.max(o.getLateFee(), calculatedLateFee);
                } else {
                    totalLateFee += o.getLateFee();
                }
            }
        }

        // Tổng tiền khách nhận lại = Tiền cọc hoàn trả - Phí phạt trễ hạn (Nếu phạt quá tiền cọc thì tiền cọc về 0)
        int netRefund = Math.max(0, totalDepositToRefund - totalLateFee);

        lblBillOrders.setText(count + " đơn");
        lblBillDates.setText(codes.toString());
        lblBillBooks.setText(allBooks.toString());
        lblBillRental.setText(String.format("%,d đ", totalRental));
        lblBillDeposit.setText(String.format("Cọc: %,d đ | Phạt trễ: %,d đ", totalDepositToRefund, totalLateFee));
        lblBillTotal.setText(String.format("Hoàn tiền cọc: %,d đ", netRefund));

        btnReturnAction.setText("Xác Nhận Trả Sách (" + count + ")");
        btnReturnAction.setEnabled(true);
        btnReturnAction.setBackground(new Color(22, 163, 74));
    }

    private void executeReturnMultipleBooks() {
        if (selectedOrdersForReturn.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Xác nhận hoàn tất trả sách cho " + selectedOrdersForReturn.size() + " đơn hàng đã chọn?",
            "Xác nhận trả sách hàng loạt",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int successCount = 0;
                for (RentalOrder order : selectedOrdersForReturn) {
                    if (!"Returned".equalsIgnoreCase(order.getOrderStatus())) {
                        if (rentController.returnBooks(order.getOrderId())) {
                            successCount++;
                        }
                    }
                }

                if (successCount > 0) {
                    JOptionPane.showMessageDialog(this, "Trả sách thành công cho " + successCount + " đơn hàng!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    selectedOrdersForReturn.clear();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Không có đơn hàng nào được xử lý hoặc các đơn đã được trả trước đó!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            String status = value != null ? value.toString() : "";

            if (status.equalsIgnoreCase("Pending")) {
                lbl.setForeground(new Color(217, 119, 6));
                lbl.setText("● Chờ duyệt");
            } else if (status.equalsIgnoreCase("Renting")) {
                lbl.setForeground(new Color(37, 99, 235));
                lbl.setText("● Đang thuê");
            } else if (status.equalsIgnoreCase("Returned")) {
                lbl.setForeground(new Color(22, 163, 74));
                lbl.setText("● Đã trả");
            } else if (status.equalsIgnoreCase("Rejected")) {
                lbl.setForeground(new Color(220, 38, 38));
                lbl.setText("● Từ chối");
            } else {
                lbl.setForeground(new Color(100, 116, 139));
                lbl.setText("● " + status);
            }
            lbl.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return lbl;
        }
    }

    private static class RoundedPanel extends JPanel {
        private int arc;
        private Color bgColor;

        public RoundedPanel(int arc, Color bgColor) {
            this.arc = arc;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.setColor(new Color(226, 232, 240));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}