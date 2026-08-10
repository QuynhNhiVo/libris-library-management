package com.libris.ui;

import com.libris.controller.BookController;
import com.libris.model.Book;
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
import java.util.ArrayList;
import java.util.List;

public class BooksView extends JPanel {
    private BookController bookController;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField tfSearch;
    private JComboBox<String> cbCategory;
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

    private List<Book> currentFilteredBooks = new ArrayList<>();

    public BooksView() {
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

        // 1. HEADER: Tiêu đề & Nút Thêm Sách
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Quản lý sách");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(30, 41, 59));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnAdd = new JButton("+ Thêm Sách") {
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

        btnAdd.addActionListener(e -> showAddEditDialog(null));

        headerPanel.add(btnAdd, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // 2. CARD CHỨA BẢNG VÀ BỘ LỌC
        JPanel cardPanel = new RoundedPanel(16, Color.WHITE);
        cardPanel.setLayout(new BorderLayout(0, 15));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Thanh công cụ bộ lọc phía trên bảng
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

                // Shadow
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(
                        1,
                        2,
                        w - 2,
                        h - 1,
                        24,
                        24);

                // Background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(
                        0,
                        0,
                        w - 2,
                        h - 2,
                        24,
                        24);

                // Border
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

        String[] categories = { "Tất cả thể loại", "Văn học", "Thiếu nhi", "Văn học nước ngoài", "Thơ ca",
                "Truyện tranh", "Kỹ năng sống", "Khoa học" };
        cbCategory = new JComboBox<>(categories);
        cbCategory.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbCategory.setPreferredSize(new Dimension(160, 40));

        cbStatus = new JComboBox<>(new String[] { "Tất cả trạng thái", Constants.BOOK_STATUS_AVAILABLE, Constants.BOOK_STATUS_RENTED, Constants.BOOK_STATUS_PENDING });
        cbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbStatus.setPreferredSize(new Dimension(150, 40));

        // Refresh button
        JButton btnRefresh = new JButton();
        btnRefresh.setIcon(IconUtils.loadIconForComponent(Constants.IC_REFRESH, btnRefresh));
        btnRefresh.setPreferredSize(new Dimension(40, 40));
        btnRefresh.setBackground(new Color(241, 245, 249));
        btnRefresh.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setToolTipText("Làm mới dữ liệu");
        btnRefresh.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnRefresh.setBackground(new Color(226, 232, 240)); }
            @Override public void mouseExited(MouseEvent e) { btnRefresh.setBackground(new Color(241, 245, 249)); }
        });
        btnRefresh.addActionListener(e -> {
            tfSearch.setText("");
            cbCategory.setSelectedIndex(0);
            cbStatus.setSelectedIndex(0);
            loadData();
        });

        filterBar.add(searchBox);
        filterBar.add(cbCategory);
        filterBar.add(cbStatus);
        filterBar.add(btnRefresh);

        cardPanel.add(filterBar, BorderLayout.NORTH);

        // Bảng hiển thị dữ liệu
        String[] columns = { "Mã sách", "Tên sách", "Tác giả", "Thể loại", "Năm XB", "Trạng thái", "Giá thuê", "Giá cọc", "Thao tác" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return column == 8; }
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

        table.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());
        table.getColumnModel().getColumn(8).setCellRenderer(new ActionCellRenderer());
        //table.getColumnModel().getColumn(8).setCellEditor(new ActionCellEditor(new JCheckBox(), this));
        table.getColumnModel().getColumn(8).setMaxWidth(95);
        table.getColumnModel().getColumn(8).setMinWidth(95);

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 8 && row != -1) {
                    hoveredRow = row;
                    Rectangle cellRect = table.getCellRect(row, col, false);
                    int relativeX = e.getX() - cellRect.x;
                    // Sửa (0), Xóa (1)
                    if (relativeX < cellRect.width / 2) {
                        hoveredButton = 0;
                    } else {
                        hoveredButton = 1;
                    }
                } else {
                    hoveredRow = -1;
                    hoveredButton = -1;
                }
                table.repaint();
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                hoveredButton = -1;
                table.repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 8 && row != -1) {
                    table.setRowSelectionInterval(row, row);
                    Rectangle cellRect = table.getCellRect(row, col, false);
                    int relativeX = e.getX() - cellRect.x;
                    if (relativeX < cellRect.width / 2) {
                        editSelectedBook();
                    } else {
                        deleteSelectedBook();
                    }
                }
            }
        });

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.add(table.getTableHeader(), BorderLayout.NORTH);
        tableContainer.add(table, BorderLayout.CENTER);
        cardPanel.add(tableContainer, BorderLayout.CENTER);

        // --- THANH ĐIỀU HƯỚNG PHÂN TRANG---
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        paginationPanel.setOpaque(false);

        btnPrev = createStyledPaginationButton("◄ Trang trước");
        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                renderTablePage();
            }
        });

        // Nhập trang
        JPanel pageInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        pageInputPanel.setOpaque(false);
        
        JLabel lblPageLabel = new JLabel("Trang");
        lblPageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPageLabel.setForeground(new Color(71, 85, 105));

        tfPageInput = new JTextField("1", 3);
        tfPageInput.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tfPageInput.setHorizontalAlignment(JTextField.CENTER);
        tfPageInput.setPreferredSize(new Dimension(40, 30));
        
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
                JOptionPane.showMessageDialog(this, "Số trang không hợp lệ! Vui lòng nhập từ 1 đến " + totalPages, "Thông báo", JOptionPane.WARNING_MESSAGE);
                tfPageInput.setText(String.valueOf(currentPage));
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng chỉ nhập số nguyên!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            tfPageInput.setText(String.valueOf(currentPage));
        }
    }

    private void setupEvents() {
        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
        });
        cbCategory.addActionListener(e -> filter());
        cbStatus.addActionListener(e -> filter());
    }

    private void loadData() {
        try {
            List<Book> books = bookController.getAllBooks();
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
        String category = cbCategory.getSelectedItem() != null ? cbCategory.getSelectedItem().toString() : "Tất cả thể loại";
        String status = cbStatus.getSelectedItem() != null ? cbStatus.getSelectedItem().toString() : "Tất cả trạng thái";

        try {
            List<Book> allBooks = bookController.getAllBooks();
            currentFilteredBooks.clear();
            if (allBooks != null) {
                for (Book book : allBooks) {
                    boolean matchSearch = searchText.isEmpty()
                            || book.getTitle().toLowerCase().contains(searchText)
                            || book.getAuthor().toLowerCase().contains(searchText)
                            || book.getBookCode().toLowerCase().contains(searchText);

                    boolean matchCategory = category.equals("Tất cả thể loại")
                            || (book.getCategory() != null && book.getCategory().equals(category));
                    boolean matchStatus = status.equals("Tất cả trạng thái")
                            || (book.getBookStatus() != null && book.getBookStatus().equals(status));

                    if (matchSearch && matchCategory && matchStatus) {
                        currentFilteredBooks.add(book);
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
        if (currentPage > totalPages) currentPage = totalPages;
        renderTablePage();
    }

    private void renderTablePage() {
        tableModel.setRowCount(0);
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, currentFilteredBooks.size());

        for (int i = startIndex; i < endIndex; i++) {
            Book book = currentFilteredBooks.get(i);
            tableModel.addRow(new Object[] {
                    book.getBookCode(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getCategory(),
                    book.getPublishYear(),
                    book.getBookStatus(),
                    book.getRentalPrice() + " đ",
                    book.getDepositPrice() + " đ",
                    ""
            });
        }

        tfPageInput.setText(String.valueOf(currentPage));
        lblTotalPages.setText("/ " + totalPages + " (Tổng: " + currentFilteredBooks.size() + " sách)");
        
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    public void editSelectedBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng sách cần sửa trên bảng!");
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        String bookCode = tableModel.getValueAt(modelRow, 0).toString();
        try {
            List<Book> books = bookController.getAllBooks();
            Book selectedBook = books.stream().filter(b -> b.getBookCode().equals(bookCode)).findFirst().orElse(null);
            if (selectedBook != null) {
                showAddEditDialog(selectedBook);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSelectedBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng sách cần xóa trên bảng!");
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        String bookCode = tableModel.getValueAt(modelRow, 0).toString();
        String title = tableModel.getValueAt(modelRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa sách \"" + title + "\"?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                List<Book> books = bookController.getAllBooks();
                Book bookToDelete = books.stream().filter(b -> b.getBookCode().equals(bookCode)).findFirst()
                        .orElse(null);
                if (bookToDelete != null && bookController.deleteBook(bookToDelete.getBookid())) {
                    JOptionPane.showMessageDialog(this, "Xóa sách thành công!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa sách này do đang có đơn thuê liên quan!", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddEditDialog(Book book) {
        boolean isEdit = book != null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Sửa thông tin sách" : "Thêm sách mới", true);
        dialog.setSize(650, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = createFormPanel(book);
        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        btnPanel.setBackground(new Color(248, 250, 252));

        JButton btnSave = new JButton(isEdit ? "Cập nhật" : "Thêm mới");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setPreferredSize(new Dimension(100, 40));
        btnSave.setEnabled(true);

        JButton btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setPreferredSize(new Dimension(100, 40));
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
        dialog.add(btnPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> {
            if (saveBook(dialog, formPanel, isEdit)) {
                dialog.dispose();
                loadData();
            }
        });
        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private JPanel createFormPanel(Book book) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField tfBookCode = new JTextField(20);
        JTextField tfTitle = new JTextField(20);
        JTextField tfAuthor = new JTextField(20);
        JComboBox<String> cbCat = new JComboBox<>(new String[] { "Văn học", "Thiếu nhi", "Văn học nước ngoài", "Thơ ca",
                "Truyện tranh", "Kỹ năng sống", "Khoa học" });
        JTextField tfPublisher = new JTextField(20);
        JTextField tfYear = new JTextField(20);
        JComboBox<String> cbStat = new JComboBox<>(new String[] { Constants.BOOK_STATUS_AVAILABLE, Constants.BOOK_STATUS_RENTED, Constants.BOOK_STATUS_PENDING });
        JTextField tfRentalPrice = new JTextField(20);
        JTextField tfDepositPrice = new JTextField(20);

        Component[] fields = { tfBookCode, tfTitle, tfAuthor, cbCat, tfPublisher, tfYear, cbStat, tfRentalPrice,
                tfDepositPrice };
        for (Component c : fields) {
            c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            if (c instanceof JComponent)
                ((JComponent) c).setPreferredSize(new Dimension(0, 36));
        }

        if (book != null) {
            tfBookCode.setText(book.getBookCode());
            tfBookCode.setEnabled(false);
            tfTitle.setText(book.getTitle());
            tfAuthor.setText(book.getAuthor());
            cbCat.setSelectedItem(book.getCategory());
            tfPublisher.setText(book.getPublisher());
            tfYear.setText(String.valueOf(book.getPublishYear()));
            cbStat.setSelectedItem(book.getBookStatus());
            tfRentalPrice.setText(String.valueOf(book.getRentalPrice()));
            tfDepositPrice.setText(String.valueOf(book.getDepositPrice()));
        }

        int row = 0;
        addFormRow(panel, gbc, "Mã sách:", tfBookCode, row++);
        addFormRow(panel, gbc, "Tên sách:", tfTitle, row++);
        addFormRow(panel, gbc, "Tác giả:", tfAuthor, row++);
        addFormRow(panel, gbc, "Thể loại:", cbCat, row++);
        addFormRow(panel, gbc, "Nhà xuất bản:", tfPublisher, row++);
        addFormRow(panel, gbc, "Năm xuất bản:", tfYear, row++);
        addFormRow(panel, gbc, "Trạng thái:", cbStat, row++);
        addFormRow(panel, gbc, "Giá thuê (VNĐ):", tfRentalPrice, row++);
        addFormRow(panel, gbc, "Tiền cọc (VNĐ):", tfDepositPrice, row++);

        panel.putClientProperty("tfBookCode", tfBookCode);
        panel.putClientProperty("tfTitle", tfTitle);
        panel.putClientProperty("tfAuthor", tfAuthor);
        panel.putClientProperty("cbCategory", cbCat);
        panel.putClientProperty("tfPublisher", tfPublisher);
        panel.putClientProperty("tfYear", tfYear);
        panel.putClientProperty("cbStatus", cbStat);
        panel.putClientProperty("tfRentalPrice", tfRentalPrice);
        panel.putClientProperty("tfDepositPrice", tfDepositPrice);

        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(71, 85, 105));
        panel.add(lbl, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(field, gbc);
    }

    private boolean saveBook(JDialog dialog, JPanel formPanel, boolean isEdit) {
        try {
            JTextField tfBookCode = (JTextField) formPanel.getClientProperty("tfBookCode");
            JTextField tfTitle = (JTextField) formPanel.getClientProperty("tfTitle");
            JTextField tfAuthor = (JTextField) formPanel.getClientProperty("tfAuthor");
            JComboBox<?> cbCat = (JComboBox<?>) formPanel.getClientProperty("cbCategory");
            JTextField tfPublisher = (JTextField) formPanel.getClientProperty("tfPublisher");
            JTextField tfYear = (JTextField) formPanel.getClientProperty("tfYear");
            JComboBox<?> cbStat = (JComboBox<?>) formPanel.getClientProperty("cbStatus");
            JTextField tfRentalPrice = (JTextField) formPanel.getClientProperty("tfRentalPrice");
            JTextField tfDepositPrice = (JTextField) formPanel.getClientProperty("tfDepositPrice");

            if (tfBookCode.getText().trim().isEmpty() || tfTitle.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Mã sách và Tên sách không được để trống!", "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }

            Book book = new Book();
            book.setBookCode(tfBookCode.getText().trim());
            book.setTitle(tfTitle.getText().trim());
            book.setAuthor(tfAuthor.getText().trim());
            book.setCategory(cbCat.getSelectedItem().toString());
            book.setPublisher(tfPublisher.getText().trim());
            book.setPublishYear(Integer.parseInt(tfYear.getText().trim()));
            book.setBookStatus(cbStat.getSelectedItem().toString());
            book.setRentalPrice(Integer.parseInt(tfRentalPrice.getText().trim()));
            book.setDepositPrice(Integer.parseInt(tfDepositPrice.getText().trim()));

            if (isEdit) {
                return bookController.updateBook(book);
            } else {
                return bookController.addBook(book);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đúng định dạng số cho Năm, Giá thuê và Tiền cọc!",
                    "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
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

    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            String status = value != null ? value.toString() : "";

            if (status.equalsIgnoreCase(Constants.BOOK_STATUS_AVAILABLE)) {
                lbl.setForeground(new Color(22, 163, 74));
                lbl.setText("● Có sẵn");
            } else if (status.equalsIgnoreCase(Constants.BOOK_STATUS_RENTED)) {
                lbl.setForeground(new Color(37, 99, 235));
                lbl.setText("● Đang thuê");
            } else if (status.equalsIgnoreCase("Pending")) {
                lbl.setForeground(new Color(217, 119, 6));
                lbl.setText("● Chờ duyệt");
            }
            lbl.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return lbl;
        }
    }

   private class ActionCellRenderer implements TableCellRenderer {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        private final JButton btnEdit = new JButton();
        private final JButton btnDelete = new JButton();

        public ActionCellRenderer() {
            panel.setOpaque(true);
            btnEdit.setIcon(IconUtils.loadIconForComponent(Constants.IC_EDIT, btnEdit));
            btnDelete.setIcon(IconUtils.loadIconForComponent(Constants.IC_DELETE, btnDelete));
            
            styleIconBtn(btnEdit);
            styleIconBtn(btnDelete);

            panel.add(btnEdit);
            panel.add(btnDelete);
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

            // Kiểm tra xem chuột đang hover vào nút nào trong hàng hiện tại
            if (row == hoveredRow) {
                if (hoveredButton == 0) {
                    btnEdit.setBackground(new Color(219, 234, 254)); // Hover Sửa đậm màu
                    btnDelete.setBackground(new Color(254, 242, 242));
                } else if (hoveredButton == 1) {
                    btnEdit.setBackground(new Color(239, 246, 255));
                    btnDelete.setBackground(new Color(254, 226, 226)); // Hover Xóa đậm màu
                } else {
                    btnEdit.setBackground(new Color(239, 246, 255));
                    btnDelete.setBackground(new Color(254, 242, 242));
                }
            } else {
                btnEdit.setBackground(new Color(239, 246, 255));
                btnDelete.setBackground(new Color(254, 242, 242));
            }
            return panel;
        }
    }
}