package com.libris.ui;

import com.libris.controller.RentalOrderController;
import com.libris.controller.BookController;
import com.libris.controller.CustomerController;
import com.libris.model.RentalOrder;
import com.libris.model.RentalOrderDetail;
import com.libris.model.Book;
import com.libris.model.Customer;
import com.libris.utils.IconUtils;
import com.libris.config.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrdersView extends JPanel {
    private RentalOrderController orderController;
    private BookController bookController;
    private CustomerController customerController;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField tfSearch;
    private JComboBox<String> cbStatus;

    private int currentPage = 1;
    private final int pageSize = 10;
    private int totalPages = 1;
    private JTextField tfPageInput;
    private JLabel lblTotalPages;
    private JButton btnPrev;
    private JButton btnNext;

    private int hoveredRow = -1;
    private int hoveredButton = -1;

    private List<RentalOrder> currentFilteredOrders = new ArrayList<>();
    private List<RentalOrder> allOrdersCache = new ArrayList<>();
    private boolean isDataLoaded = false;

    public OrdersView() {
        orderController = new RentalOrderController();
        bookController = new BookController();
        customerController = new CustomerController();
        initComponents();
        loadDataAsync();
    }

    public void refreshData() {
        isDataLoaded = false;
        loadDataAsync();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        setBackground(new Color(245, 247, 250));

        // 1. HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Quản lý Đơn Thuê");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(30, 41, 59));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnAdd = new JButton("+ Tạo Đơn Thuê") {
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

        Color hoverColor = new Color(0, 42, 113);
        Color normalColor = new Color(39, 64, 139);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setBackground(normalColor);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setBorderPainted(false);
        btnAdd.setFocusPainted(false);
        btnAdd.setContentAreaFilled(false);
        btnAdd.setPreferredSize(new Dimension(140, 40));

        btnAdd.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnAdd.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnAdd.setBackground(normalColor);
            }
        });
        btnAdd.addActionListener(e -> showCreateOrderDialog());
        headerPanel.add(btnAdd, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // 2. CARD 
        JPanel cardPanel = new RoundedPanel(16, Color.WHITE);
        cardPanel.setLayout(new BorderLayout(0, 15));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterBar.setOpaque(false);

        JPanel searchBox = new JPanel(new FlowLayout(
                FlowLayout.LEFT,
                8,
                0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(
                        1,
                        2,
                        w - 2,
                        h - 1,
                        24,
                        24);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(
                        0,
                        0,
                        w - 2,
                        h - 2,
                        24,
                        24);

                g2.setColor(new Color(226, 232, 240));
                g2.drawRoundRect(
                        0,
                        0,
                        w - 2,
                        h - 2,
                        24,
                        24);

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
        lblSearchIcon.setVerticalAlignment(SwingConstants.CENTER);

        tfSearch = new JTextField(19) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (getText().isEmpty() && !isFocusOwner()) {

                    Graphics2D g2 = (Graphics2D) g.create();

                    g2.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setColor(new Color(160, 174, 192));
                    g2.setFont(new Font(
                            "Segoe UI",
                            Font.ITALIC,
                            13));

                    String placeholder = "Tìm kiếm theo tên, mã sách, tác giả...";

                    FontMetrics fm = g2.getFontMetrics();

                    int x = 0;

                    int y = (getHeight()
                            - fm.getHeight()) / 2
                            + fm.getAscent();

                    g2.drawString(placeholder, x, y);

                    g2.dispose();
                }
            }
        };
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tfSearch.setBorder(BorderFactory.createEmptyBorder());
        tfSearch.setOpaque(false);

        tfSearch.setPreferredSize(
                new Dimension(235, 38));

        searchBox.add(lblSearchIcon);
        searchBox.add(tfSearch);

        cbStatus = new JComboBox<>(new String[] { "Tất cả trạng thái", "Pending", "Renting", "Returned", "Rejected" });
        cbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbStatus.setPreferredSize(new Dimension(160, 40));

        JButton btnRefresh = new JButton();
        btnRefresh.setIcon(IconUtils.loadIconForComponent(Constants.IC_REFRESH, btnRefresh));
        btnRefresh.setPreferredSize(new Dimension(40, 40));
        btnRefresh.setBackground(new Color(241, 245, 249));
        btnRefresh.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setToolTipText("Làm mới dữ liệu");

        btnRefresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnRefresh.setBackground(new Color(226, 232, 240));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnRefresh.setBackground(new Color(241, 245, 249));
            }
        });
        btnRefresh.addActionListener(e -> {
            tfSearch.setText("");
            cbStatus.setSelectedIndex(0);
            loadDataAsync();
        });

        filterBar.add(searchBox);
        filterBar.add(cbStatus);
        filterBar.add(btnRefresh);

        cardPanel.add(filterBar, BorderLayout.NORTH);

        String[] columns = { "Mã Đơn", "Mã KH", "Khách Hàng", "Ngày Thuê", "Dự Kiến Trả", "Trạng Thái", "Thao Tác" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(45);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));

        table.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionCellRenderer());

        table.getColumnModel().getColumn(6).setMaxWidth(140);
        table.getColumnModel().getColumn(6).setMinWidth(140);

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 6 && row != -1) {
                    if (hoveredRow != row) {
                        hoveredRow = row;
                        table.repaint();
                    }
                } else if (hoveredRow != -1) {
                    hoveredRow = -1;
                    table.repaint();
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (hoveredRow != -1) {
                    hoveredRow = -1;
                    table.repaint();
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 6 && row != -1) {
                    int modelRow = table.convertRowIndexToModel(row);
                    String status = tableModel.getValueAt(modelRow, 5).toString();
                    Rectangle cellRect = table.getCellRect(row, col, false);
                    int relativeX = e.getX() - cellRect.x;

                    if ("Pending".equalsIgnoreCase(status)) {
                        if (relativeX < cellRect.width / 3) viewOrderDetail(modelRow);
                        else if (relativeX < (cellRect.width * 2) / 3) updateStatusDirect(modelRow, "Renting");
                        else updateStatusDirect(modelRow, "Rejected");
                    } else if ("Renting".equalsIgnoreCase(status)) {
                        if (relativeX < cellRect.width / 2) viewOrderDetail(modelRow);
                        else updateStatusDirect(modelRow, "Returned");
                    } else {
                        viewOrderDetail(modelRow);
                    }
                }
            }
        });

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        paginationPanel.setOpaque(false);

        btnPrev = createStyledPaginationButton("◄ Trang trước");
        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                renderTablePage();
            }
        });

        JPanel pageInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        pageInputPanel.setOpaque(false);

        JLabel lblPageLabel = new JLabel("Trang");
        lblPageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPageLabel.setForeground(new Color(71, 85, 105));

        tfPageInput = new JTextField("1", 3);
        tfPageInput.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tfPageInput.setHorizontalAlignment(JTextField.CENTER);
        tfPageInput.setPreferredSize(new Dimension(45, 30));
        tfPageInput.addActionListener(e -> jumpToPage());

        lblTotalPages = new JLabel("/ 1");
        lblTotalPages.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTotalPages.setForeground(new Color(71, 85, 105));

        pageInputPanel.add(lblPageLabel);
        pageInputPanel.add(tfPageInput);
        pageInputPanel.add(lblTotalPages);

        btnNext = createStyledPaginationButton("Trang sau ►");
        btnNext.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                renderTablePage();
            }
        });

        paginationPanel.add(btnPrev);
        paginationPanel.add(pageInputPanel);
        paginationPanel.add(btnNext);

        cardPanel.add(paginationPanel, BorderLayout.SOUTH);

        add(cardPanel, BorderLayout.CENTER);
        setupEvents();
    }

    private JButton createStyledPaginationButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(71, 85, 105));
        btn.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 32));
        return btn;
    }

    private void jumpToPage() {
        try {
            int targetPage = Integer.parseInt(tfPageInput.getText().trim());
            if (targetPage >= 1 && targetPage <= totalPages) {
                currentPage = targetPage;
                renderTablePage();
            } else {
                JOptionPane.showMessageDialog(this, "Số trang không hợp lệ! Vui lòng nhập từ 1 đến " + totalPages,
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                tfPageInput.setText(String.valueOf(currentPage));
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng chỉ nhập số nguyên!", "Lỗi định dạng",
                    JOptionPane.ERROR_MESSAGE);
            tfPageInput.setText(String.valueOf(currentPage));
        }
    }

    private void setupEvents() {
        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }
        });
        cbStatus.addActionListener(e -> filter());
    }

    private void loadDataAsync() {
        if (isDataLoaded && !allOrdersCache.isEmpty()) {
            currentFilteredOrders = new ArrayList<>(allOrdersCache);
            currentPage = 1;
            updatePagination();
            return;
        }

        SwingWorker<List<RentalOrder>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<RentalOrder> doInBackground() throws Exception {
                return orderController.getAllOrders();
            }

            @Override
            protected void done() {
                try {
                    List<RentalOrder> orders = get();
                    allOrdersCache = orders != null ? new ArrayList<>(orders) : new ArrayList<>();
                    currentFilteredOrders = new ArrayList<>(allOrdersCache);
                    isDataLoaded = true;
                    currentPage = 1;
                    updatePagination();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(OrdersView.this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void filter() {
        String searchText = tfSearch.getText().toLowerCase().trim();
        String status = cbStatus.getSelectedItem() != null ? cbStatus.getSelectedItem().toString()
                : "Tất cả trạng thái";

        try {
            List<RentalOrder> allOrders = orderController.getAllOrders();
            currentFilteredOrders.clear();
            if (allOrders != null) {
                for (RentalOrder order : allOrders) {
                    boolean matchSearch = searchText.isEmpty()
                            || (order.getOrderCode() != null && order.getOrderCode().toLowerCase().contains(searchText))
                            || (order.getCustomerName() != null
                                    && order.getCustomerName().toLowerCase().contains(searchText))
                            || (order.getCustomerCode() != null
                                    && order.getCustomerCode().toLowerCase().contains(searchText));

                    boolean matchStatus = status.equals("Tất cả trạng thái")
                            || (order.getOrderStatus() != null && order.getOrderStatus().equals(status));

                    if (matchSearch && matchStatus) {
                        currentFilteredOrders.add(order);
                    }
                }
            }
            currentPage = 1;
            updatePagination();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePagination() {
        int totalRecords = currentFilteredOrders.size();
        totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / pageSize));
        if (currentPage > totalPages)
            currentPage = totalPages;
        renderTablePage();
    }

    private void renderTablePage() {
        tableModel.setRowCount(0);
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, currentFilteredOrders.size());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = startIndex; i < endIndex; i++) {
            RentalOrder order = currentFilteredOrders.get(i);
            tableModel.addRow(new Object[] {
                    order.getOrderCode(),
                    order.getCustomerCode() != null ? order.getCustomerCode() : "N/A",
                    order.getCustomerName(),
                    order.getRentDate() != null ? order.getRentDate().format(formatter) : "",
                    order.getExpectedReturnDate() != null ? order.getExpectedReturnDate().format(formatter) : "",
                    order.getOrderStatus(),
                    order
            });
        }

        if (tfPageInput != null) {
            tfPageInput.setText(String.valueOf(currentPage));
            lblTotalPages.setText("/ " + totalPages + " (Tổng: " + currentFilteredOrders.size() + " đơn)");
            btnPrev.setEnabled(currentPage > 1);
            btnNext.setEnabled(currentPage < totalPages);
        }
    }

    public void viewOrderDetail(int modelRow) {
        String orderCode = tableModel.getValueAt(modelRow, 0).toString();
        try {
            List<RentalOrder> orders = orderController.getAllOrders();
            RentalOrder order = orders.stream().filter(o -> o.getOrderCode().equals(orderCode)).findFirst()
                    .orElse(null);
            if (order != null) {
                order = orderController.getOrderById(order.getOrderId());
                if (order != null)
                    showOrderDetailDialog(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateStatusDirect(int modelRow, String newStatus) {
        String orderCode = tableModel.getValueAt(modelRow, 0).toString();
        try {
            List<RentalOrder> orders = orderController.getAllOrders();
            RentalOrder order = orders.stream().filter(o -> o.getOrderCode().equals(orderCode)).findFirst()
                    .orElse(null);
            if (order != null) {
                LocalDateTime returnDate = "Returned".equals(newStatus) ? LocalDateTime.now() : null;
                if (orderController.updateOrderStatus(order.getOrderId(), newStatus, returnDate)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thành công!");
                    loadDataAsync();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showOrderDetailDialog(RentalOrder order) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Chi tiết đơn thuê: " + order.getOrderCode(), true);
        dialog.setSize(600, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 8));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin chung"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        infoPanel.add(new JLabel("Mã đơn:"));
        infoPanel.add(new JLabel(order.getOrderCode()));
        infoPanel.add(new JLabel("Khách hàng:"));
        infoPanel.add(new JLabel(order.getCustomerName()));
        infoPanel.add(new JLabel("Ngày thuê:"));
        infoPanel.add(new JLabel(order.getRentDate().format(formatter)));
        infoPanel.add(new JLabel("Dự kiến trả:"));
        infoPanel.add(new JLabel(order.getExpectedReturnDate().format(formatter)));
        infoPanel.add(new JLabel("Tổng tiền:"));
        infoPanel.add(new JLabel(order.getTotalAmount() + " đ"));
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Danh sách sách trong đơn
        DefaultTableModel bookModel = new DefaultTableModel(new String[] { "Mã sách", "Tên sách", "Tác giả" }, 0);
        if (order.getDetails() != null) {
            for (RentalOrderDetail detail : order.getDetails()) {
                Book b = detail.getBook();
                if (b != null)
                    bookModel.addRow(new Object[] { b.getBookCode(), b.getTitle(), b.getAuthor() });
            }
        }
        JTable bookTable = new JTable(bookModel);
        bookTable.setRowHeight(30);
        JScrollPane scroll = new JScrollPane(bookTable);

        JPanel booksWrapper = new JPanel(new BorderLayout());
        booksWrapper.setBackground(Color.WHITE);
        booksWrapper.setBorder(BorderFactory.createTitledBorder("Danh sách sách thuê"));
        booksWrapper.add(scroll, BorderLayout.CENTER);
        mainPanel.add(booksWrapper, BorderLayout.CENTER);

        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dialog.dispose());
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBottom.setBackground(Color.WHITE);
        pnlBottom.add(btnClose);
        mainPanel.add(pnlBottom, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showCreateOrderDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tạo đơn thuê mới", true);
        dialog.setSize(650, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Form chọn khách hàng
        JPanel custPanel = new JPanel(new BorderLayout(5, 5));
        custPanel.setOpaque(false);
        JLabel lblCust = new JLabel("Chọn khách hàng:");
        lblCust.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JComboBox<String> cbCustomer = new JComboBox<>();
        cbCustomer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbCustomer.setPreferredSize(new Dimension(0, 38));

        try {
            List<Customer> customers = customerController.getAllCustomers();
            if (customers != null) {
                for (Customer c : customers) {
                    cbCustomer.addItem(c.getCustomerCode() + " - " + c.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        custPanel.add(lblCust, BorderLayout.NORTH);
        custPanel.add(cbCustomer, BorderLayout.CENTER);
        mainPanel.add(custPanel, BorderLayout.NORTH);

        // Danh sách chọn sách
        JPanel bookPanel = new JPanel(new BorderLayout(5, 5));
        bookPanel.setOpaque(false);
        JLabel lblBooks = new JLabel("Chọn sách cho thuê (giữ Ctrl để chọn nhiều):");
        lblBooks.setFont(new Font("Segoe UI", Font.BOLD, 13));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> bookList = new JList<>(listModel);
        bookList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bookList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        try {
            List<Book> books = bookController.getAllBooks();
            if (books != null) {
                for (Book b : books) {
                    if ("Available".equalsIgnoreCase(b.getBookStatus())) {
                        listModel.addElement(b.getBookCode() + " - " + b.getTitle());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JScrollPane scrollBooks = new JScrollPane(bookList);
        scrollBooks.setPreferredSize(new Dimension(0, 200));
        bookPanel.add(lblBooks, BorderLayout.NORTH);
        bookPanel.add(scrollBooks, BorderLayout.CENTER);
        mainPanel.add(bookPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnSave = new JButton("Tạo đơn");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setPreferredSize(new Dimension(100, 40));
        btnSave.setEnabled(true);

        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.setPreferredSize(new Dimension(100, 40));
        btnCancel.setFocusPainted(false);

        btnSave.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSave.setBackground(new Color(65, 130, 240));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnSave.setBackground(new Color(88, 150, 255));
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> {
            int custIdx = cbCustomer.getSelectedIndex();
            if (custIdx == -1) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn khách hàng!", "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<String> selectedBooksStr = bookList.getSelectedValuesList();
            if (selectedBooksStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn ít nhất một quyển sách!", "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                List<Customer> customers = customerController.getAllCustomers();
                Customer selectedCust = customers.get(custIdx);

                RentalOrder order = new RentalOrder();
                order.setOrderCode("O" + System.currentTimeMillis());
                order.setCustomerId(selectedCust.getCustomerId());
                order.setCustomerName(selectedCust.getName());
                order.setRentDate(LocalDateTime.now());
                order.setExpectedReturnDate(LocalDateTime.now().plusDays(7));
                order.setOrderStatus("Pending");

                List<Book> allBooks = bookController.getAllBooks();
                for (String bStr : selectedBooksStr) {
                    String bCode = bStr.split(" - ")[0];
                    Book bookObj = allBooks.stream().filter(b -> b.getBookCode().equals(bCode)).findFirst()
                            .orElse(null);
                    if (bookObj != null) {
                        RentalOrderDetail detail = new RentalOrderDetail();
                        detail.setBookId(bookObj.getBookid());
                        detail.setBook(bookObj);
                        order.getDetails().add(detail);
                    }
                }

                int total = order.getDetails().size() * 10000;
                order.setTotalAmount(total);
                order.setTotalDeposit(total * 2);
                order.setTotalRentalFee(total);
                order.setLateFee(0);

                if (orderController.addOrder(order)) {
                    JOptionPane.showMessageDialog(dialog, "Tạo đơn thuê thành công!");
                    dialog.dispose();
                    loadDataAsync();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Tạo đơn thuê thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.add(mainPanel);
        dialog.setVisible(true);
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

    // --- HIỂN THỊ TRẠNG THÁI ĐƠN HÀNG DẠNG BADGE ---
    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            String status = value != null ? value.toString() : "";

            if (status.equalsIgnoreCase("Pending")) {
                lbl.setForeground(new Color(217, 119, 6));
            } else if (status.equalsIgnoreCase("Renting")) {
                lbl.setForeground(new Color(37, 99, 235));
                lbl.setText("● Đang thuê");
            } else if (status.equalsIgnoreCase("Returned")) {
                lbl.setForeground(new Color(22, 163, 74));
                lbl.setText("● Đã trả");
            } else if (status.equalsIgnoreCase("Rejected")) {
                lbl.setForeground(new Color(220, 38, 38));
                lbl.setText("● Từ chối");
            }
            lbl.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return lbl;
        }
    }

    private class ActionCellRenderer implements TableCellRenderer {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        private final JButton btnView = new JButton();
        private final JButton btnApprove = new JButton();
        private final JButton btnReject = new JButton();

        public ActionCellRenderer() {
            panel.setOpaque(true);
            btnView.setIcon(IconUtils.loadIconForComponent(Constants.IC_EDIT, btnView));
            btnApprove.setIcon(IconUtils.loadIconForComponent(Constants.IC_APPROVE,
                    btnApprove));
            btnReject.setIcon(IconUtils.loadIconForComponent(Constants.IC_REJECT,
                    btnReject));

            styleIconBtn(btnView);
            styleIconBtn(btnApprove);
            styleIconBtn(btnReject);

            panel.add(btnView);
            panel.add(btnApprove);
            panel.add(btnReject);
        }

        private void styleIconBtn(JButton btn) {
            btn.setPreferredSize(new Dimension(32, 32));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            int modelRow = table.convertRowIndexToModel(row);
            String status = tableModel.getValueAt(modelRow, 5).toString();

            if ("Pending".equalsIgnoreCase(status)) {
                btnView.setVisible(true);
                btnApprove.setVisible(true);
                btnReject.setVisible(true);
            } else if ("Renting".equalsIgnoreCase(status)) {
                btnView.setVisible(true);
                btnApprove.setVisible(true);
                btnReject.setVisible(false);
            } else {
                btnView.setVisible(true);
                btnApprove.setVisible(false);
                btnReject.setVisible(false);
            }

            // Màu sắc nút bình thường
            btnView.setBackground(new Color(239, 246, 255));
            btnApprove.setBackground(new Color(240, 253, 244));
            btnReject.setBackground(new Color(254, 242, 242));

            // Hiệu ứng hover theo dòng được tối ưu nhanh mượt
            if (row == hoveredRow) {
                btnView.setBackground(new Color(219, 234, 254));
                btnApprove.setBackground(new Color(220, 252, 231));
                btnReject.setBackground(new Color(254, 226, 226));
            }

            return panel;
        }
    }
}