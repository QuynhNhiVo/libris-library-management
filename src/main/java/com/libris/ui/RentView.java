package com.libris.ui;

import com.libris.controller.RentController;
import com.libris.controller.BookController;
import com.libris.model.Book;
import com.libris.model.User;
import com.libris.model.RentRequest;
import com.libris.utils.IconUtils;
import com.libris.config.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RentView extends JPanel {
    private RentController rentController;
    private BookController bookController;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField tfSearch;
    private JComboBox<String> cbCategory;

    private List<Book> selectedBooks = new ArrayList<>();
    private List<Book> currentFilteredBooks = new ArrayList<>();

    private JLabel lblSelectedCount;
    private User currentUser;
    private int customerId;

    private int currentPage = 1;
    private final int pageSize = 8;
    private int totalPages = 1;
    private JTextField tfPageInput;
    private JLabel lblTotalPages;
    private JButton btnPrev;
    private JButton btnNext;

    public RentView(com.libris.model.User user) {
        rentController = new RentController();
        bookController = new BookController();
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

        // 1. HEADER:
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleWrapper.setOpaque(false);
        JLabel lblTitle = new JLabel("Thư viện");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(30, 41, 59));

        titleWrapper.add(lblTitle);
        headerPanel.add(titleWrapper, BorderLayout.WEST);

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
            cbCategory.setSelectedIndex(0);
            loadData();
        });

        headerPanel.add(btnRefresh, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 2. KHUNG NỘI DUNG CHÍNH
        JPanel cardPanel = new RoundedPanel(16, Color.WHITE);
        cardPanel.setLayout(new BorderLayout(0, 15));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel filterBar = new JPanel(new BorderLayout());
        filterBar.setOpaque(false);

        JPanel filterLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterLeft.setOpaque(false);

        JPanel searchBox = new JPanel(new FlowLayout(
                FlowLayout.LEFT,
                8,
                0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

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
        lblSearchIcon.setVerticalAlignment(SwingConstants.CENTER);

        tfSearch = new JTextField(19) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(160, 174, 192));
                    g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                    String placeholder = "Tìm kiếm theo tên, mã sách, tác giả...";
                    FontMetrics fm = g2.getFontMetrics();
                    int x = 0;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(placeholder, x, y);
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
        filterLeft.add(searchBox);

        String[] categories = { "Tất cả thể loại", "Văn học", "Thiếu nhi", "Văn học nước ngoài", "Thơ ca",
                "Truyện tranh", "Kỹ năng sống", "Khoa học" };
        cbCategory = new JComboBox<>(categories);
        cbCategory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbCategory.setPreferredSize(new Dimension(160, 40));
        filterLeft.add(cbCategory);

        filterBar.add(filterLeft, BorderLayout.WEST);

        JButton btnClearFilter = new JButton("Xóa lọc") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
            }
        };
        btnClearFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClearFilter.setForeground(new Color(100, 116, 139));
        btnClearFilter.setBackground(new Color(241, 245, 249));
        btnClearFilter.setContentAreaFilled(false);
        btnClearFilter.setFocusPainted(false);
        btnClearFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClearFilter.setPreferredSize(new Dimension(110, 38));
        btnClearFilter.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnClearFilter.setBackground(new Color(226, 232, 240));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnClearFilter.setBackground(new Color(241, 245, 249));
            }
        });
        btnClearFilter.addActionListener(e -> {
            tfSearch.setText("");
            cbCategory.setSelectedIndex(0);
            loadData();
        });

        JPanel filterRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filterRight.setOpaque(false);
        filterRight.add(btnClearFilter);
        filterBar.add(filterRight, BorderLayout.EAST);

        cardPanel.add(filterBar, BorderLayout.NORTH);

        // Bảng danh sách sách
        String[] columns = { "Mã sách", "Tên sách", "Tác giả", "Thể loại", "Trạng thái", "Chọn" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 5 ? Boolean.class : String.class;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(45);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setPreferredSize(new Dimension(0, 42));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));

        table.getColumnModel().getColumn(0).setMaxWidth(90); // Mã sách
        table.getColumnModel().getColumn(0).setMinWidth(80);
        table.getColumnModel().getColumn(3).setMaxWidth(130); // Thể loại
        table.getColumnModel().getColumn(3).setMinWidth(110);
        table.getColumnModel().getColumn(5).setMaxWidth(60); // Checkbox
        table.getColumnModel().getColumn(5).setMinWidth(50);

        // Renderer header
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(226, 232, 240));
                c.setForeground(new Color(15, 23, 42));

                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setHorizontalAlignment(SwingConstants.CENTER);

                setOpaque(true);
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        headerRenderer.setBackground(new Color(160, 160, 160));

        // Renderer
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());

        // Renderer Checkbox header & cell
        table.getColumnModel().getColumn(5).setCellRenderer(table.getDefaultRenderer(Boolean.class));

        tableModel.addTableModelListener(e -> {
            if (e.getColumn() == 5) {
                updateSelectedBooksFromTable();
            }
        });

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.add(table.getTableHeader(), BorderLayout.NORTH);
        tableContainer.add(table, BorderLayout.CENTER);
        cardPanel.add(tableContainer, BorderLayout.CENTER);

        // --- PHẦN PHÂN TRANG---
        JPanel southWrapper = new JPanel(new BorderLayout(0, 15));
        southWrapper.setOpaque(false);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
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

        tfPageInput = new JTextField("1", 3);
        tfPageInput.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tfPageInput.setHorizontalAlignment(JTextField.CENTER);
        tfPageInput.setPreferredSize(new Dimension(45, 30));
        tfPageInput.addActionListener(e -> jumpToPage());

        lblTotalPages = new JLabel("/ 1");
        lblTotalPages.setFont(new Font("Segoe UI", Font.PLAIN, 13));

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
        southWrapper.add(paginationPanel, BorderLayout.NORTH);

        // Thanh thông tin sách
        JPanel selectedPanel = new JPanel(new BorderLayout());
        selectedPanel.setBackground(new Color(248, 250, 252));
        selectedPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        lblSelectedCount = new JLabel("0 sách được chọn");
        lblSelectedCount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSelectedCount.setForeground(new Color(30, 41, 59));
        selectedPanel.add(lblSelectedCount, BorderLayout.WEST);

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actionBtns.setOpaque(false);

        JButton btnClear = new JButton("Bỏ chọn") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
            }
        };
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClear.setForeground(Color.WHITE);
        btnClear.setBackground(new Color(239, 68, 68)); // Đỏ chủ đạo
        btnClear.setContentAreaFilled(false);
        btnClear.setFocusPainted(false);
        btnClear.setBorderPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.setPreferredSize(new Dimension(110, 38));
        btnClear.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnClear.setBackground(new Color(185, 28, 28));
            } // Đỏ đậm khi hover

            @Override
            public void mouseExited(MouseEvent e) {
                btnClear.setBackground(new Color(239, 68, 68));
            }
        });
        btnClear.addActionListener(e -> clearSelection());

        JButton btnRent = new JButton("Thuê sách") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
            }
        };
        btnRent.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRent.setForeground(Color.WHITE);
        btnRent.setBackground(new Color(22, 163, 74));
        btnRent.setContentAreaFilled(false);
        btnRent.setBorderPainted(false);
        btnRent.setFocusPainted(false);
        btnRent.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRent.setPreferredSize(new Dimension(130, 38));
        btnRent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnRent.setBackground(new Color(21, 128, 61));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnRent.setBackground(new Color(22, 163, 74));
            }
        });
        btnRent.addActionListener(e -> createRentRequest());

        actionBtns.add(btnClear);
        actionBtns.add(btnRent);
        selectedPanel.add(actionBtns, BorderLayout.EAST);

        southWrapper.add(selectedPanel, BorderLayout.SOUTH);
        cardPanel.add(southWrapper, BorderLayout.SOUTH);

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

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(new Color(239, 246, 255));
                    btn.setForeground(new Color(37, 99, 235));
                    btn.setBorder(BorderFactory.createLineBorder(new Color(37, 99, 235)));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(Color.WHITE);
                    btn.setForeground(new Color(71, 85, 105));
                    btn.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
                }
            }
        });
        return btn;
    }

    private void jumpToPage() {
        try {
            int targetPage = Integer.parseInt(tfPageInput.getText().trim());
            if (targetPage >= 1 && targetPage <= totalPages) {
                currentPage = targetPage;
                renderTablePage();
            } else {
                JOptionPane.showMessageDialog(this, "Số trang không hợp lệ! Nhập từ 1 đến " + totalPages);
                tfPageInput.setText(String.valueOf(currentPage));
            }
        } catch (NumberFormatException ex) {
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
        cbCategory.addActionListener(e -> filter());
    }

    private void loadData() {
        try {
            List<Book> books = rentController.getAvailableBooks();
            if (books != null) {
                currentFilteredBooks = new ArrayList<>(books);
            } else {
                currentFilteredBooks.clear();
            }
            currentPage = 1;
            updatePagination();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filter() {
        String searchText = tfSearch.getText().toLowerCase().trim();
        String category = cbCategory.getSelectedItem() != null ? cbCategory.getSelectedItem().toString()
                : "Tất cả thể loại";

        try {
            List<Book> books = rentController.getAvailableBooks();
            currentFilteredBooks.clear();
            if (books != null) {
                for (Book b : books) {
                    boolean matchSearch = searchText.isEmpty()
                            || (b.getTitle() != null && b.getTitle().toLowerCase().contains(searchText))
                            || (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(searchText))
                            || (b.getBookCode() != null && b.getBookCode().toLowerCase().contains(searchText));

                    boolean matchCategory = category.equals("Tất cả thể loại")
                            || (b.getCategory() != null && b.getCategory().equals(category));

                    if (matchSearch && matchCategory) {
                        currentFilteredBooks.add(b);
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
        int totalRecords = currentFilteredBooks.size();
        totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / pageSize));
        if (currentPage > totalPages)
            currentPage = totalPages;
        renderTablePage();
    }

    private void renderTablePage() {
        tableModel.setRowCount(0);
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, currentFilteredBooks.size());

        for (int i = startIndex; i < endIndex; i++) {
            Book book = currentFilteredBooks.get(i);
            boolean isChecked = selectedBooks.stream().anyMatch(b -> b.getBookCode().equals(book.getBookCode()));

            tableModel.addRow(new Object[] {
                    book.getBookCode(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getCategory(),
                    book.getBookStatus(),
                    isChecked
            });
        }

        tfPageInput.setText(String.valueOf(currentPage));
        lblTotalPages.setText("/ " + totalPages);
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    private void updateSelectedBooksFromTable() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean checked = (Boolean) tableModel.getValueAt(i, 5);
            String code = tableModel.getValueAt(i, 0).toString();

            Book targetBook = currentFilteredBooks.stream()
                    .filter(b -> b.getBookCode().equals(code))
                    .findFirst().orElse(null);

            if (targetBook != null) {
                boolean exists = selectedBooks.stream().anyMatch(b -> b.getBookCode().equals(code));
                if (checked && !exists) {
                    selectedBooks.add(targetBook);
                } else if (!checked && exists) {
                    selectedBooks.removeIf(b -> b.getBookCode().equals(code));
                }
            }
        }
        updateSelectedCount();
    }

    private void updateSelectedCount() {
        lblSelectedCount.setText("🛒 " + selectedBooks.size() + " sách được chọn");
    }

    private void clearSelection() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(false, i, 5);
        }
        selectedBooks.clear();
        updateSelectedCount();
        table.repaint();
    }

    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            String status = value != null ? value.toString() : "";

            if (status.equalsIgnoreCase("Available") || status.equalsIgnoreCase("Có sẵn")) {
                lbl.setForeground(new Color(22, 163, 74));
                lbl.setText("● Có sẵn");
            } else if (status.equalsIgnoreCase("Rented") || status.equalsIgnoreCase("Đã mượn")) {
                lbl.setForeground(new Color(217, 119, 6));
                lbl.setText("● Đã mượn");
            } else {
                lbl.setForeground(new Color(100, 116, 139));
                lbl.setText("● " + status);
            }
            lbl.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return lbl;
        }
    }

    private void createRentRequest() {
        if (selectedBooks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một sách để thuê!", "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Xác nhận thuê sách", true);
        dialog.setSize(540, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel lblHeader = new JLabel("Thông tin phiếu thuê sách");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(30, 41, 59));
        mainPanel.add(lblHeader, BorderLayout.NORTH);

        // Panel thông tin tổng quan & danh sách
        JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
        centerPanel.setOpaque(false);

        // Thẻ tóm tắt số lượng và tổng giá cọc/thuê
        JPanel summaryCard = new JPanel(new GridLayout(1, 2, 15, 0));
        summaryCard.setOpaque(false);

        int totalRentalPrice = selectedBooks.stream().mapToInt(Book::getRentalPrice).sum();
        int totalDepositPrice = selectedBooks.stream().mapToInt(Book::getDepositPrice).sum();

        summaryCard.add(createSummaryBox("Số lượng sách", selectedBooks.size() + " cuốn", new Color(239, 246, 255),
                new Color(37, 99, 235)));
        summaryCard.add(createSummaryBox("Tổng tiền thuê / cọc", String.format("%,d đ", totalRentalPrice),
                new Color(240, 253, 244), new Color(22, 163, 74)));

        centerPanel.add(summaryCard, BorderLayout.NORTH);

        // Danh sách sách chọn thuê
        JTextArea taBooks = new JTextArea(6, 30);
        taBooks.setEditable(false);
        taBooks.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        taBooks.setMargin(new Insets(8, 8, 8, 8));
        for (Book book : selectedBooks) {
            taBooks.append(
                    "• [" + book.getBookCode() + "] " + book.getTitle() + " - " + book.getRentalPrice() + " đ\n");
        }
        JScrollPane scrollPane = new JScrollPane(taBooks);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                "Danh sách sách chọn thuê",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(71, 85, 105)));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Ngày trả dự kiến to rõ
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        datePanel.setOpaque(false);
        JLabel lblDate = new JLabel("Ngày trả dự kiến:");
        lblDate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDate.setForeground(new Color(71, 85, 105));

        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
        spinner.setEditor(editor);
        spinner.setPreferredSize(new Dimension(150, 36));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        java.util.Date date = new java.util.Date();
        date.setTime(date.getTime() + 7 * 24 * 60 * 60 * 1000L);
        spinner.setValue(date);

        datePanel.add(lblDate);
        datePanel.add(spinner);
        centerPanel.add(datePanel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnCancel = new JButton("Hủy bỏ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
            }
        };
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setForeground(new Color(100, 116, 139));
        btnCancel.setBackground(new Color(241, 245, 249));
        btnCancel.setContentAreaFilled(false);
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.setPreferredSize(new Dimension(110, 38));
        btnCancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCancel.setBackground(new Color(226, 232, 240));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnCancel.setBackground(new Color(241, 245, 249));
            }
        });
        btnCancel.addActionListener(e -> dialog.dispose());

        JButton btnConfirm = new JButton("Xác nhận thuê") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
            }
        };
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setBackground(new Color(37, 99, 235));
        btnConfirm.setContentAreaFilled(false);
        btnConfirm.setBorderPainted(false);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirm.setPreferredSize(new Dimension(140, 38));
        btnConfirm.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnConfirm.setBackground(new Color(29, 78, 216));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnConfirm.setBackground(new Color(37, 99, 235));
            }
        });

        btnConfirm.addActionListener(e -> {
            try {
                RentRequest request = new RentRequest();
                request.setCustomerId(customerId);
                request.setBooks(new ArrayList<>(selectedBooks));
                request.setRequestDate(LocalDateTime.now());

                java.util.Date returnDate = (java.util.Date) spinner.getValue();
                request.setExpectedReturnDate(
                        returnDate.toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDateTime());
                request.setStatus("Pending");

                if (rentController.createRentRequest(request)) {
                    JOptionPane.showMessageDialog(dialog, "Gửi yêu cầu thuê sách thành công!");
                    dialog.dispose();
                    clearSelection();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Gửi yêu cầu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnConfirm);

        JPanel bottomWrapper = new JPanel(new BorderLayout());
        bottomWrapper.setOpaque(false);
        bottomWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bottomWrapper.add(btnPanel, BorderLayout.EAST);
        mainPanel.add(bottomWrapper, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // Hàm phụ trợ tạo hộp thông tin tóm tắt (Summary Box) nổi bật
    private JPanel createSummaryBox(String title, String val, Color bgColor, Color textColor) {
        JPanel box = new JPanel(new GridLayout(2, 1, 0, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        box.setOpaque(false);
        box.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(new Color(100, 116, 139));

        JLabel lblVal = new JLabel(val);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblVal.setForeground(textColor);

        box.add(lblTitle);
        box.add(lblVal);
        return box;
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