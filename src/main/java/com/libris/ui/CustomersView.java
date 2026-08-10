package com.libris.ui;

import com.libris.controller.CustomerController;
import com.libris.model.Customer;
import com.libris.utils.IconUtils;
import com.libris.config.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

public class CustomersView extends JPanel {
    private CustomerController customerController;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField tfSearch;

    private int currentPage = 1;
    private final int pageSize = 10;
    private int totalPages = 1;
    private JTextField tfPageInput;
    private JLabel lblTotalPages;
    private JButton btnPrev;
    private JButton btnNext;

    private int hoveredRow = -1;
    private int hoveredButton = -1;

    private List<Customer> currentFilteredCustomers = new ArrayList<>();

    public CustomersView() {
        customerController = new CustomerController();
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

        // 1. HEADER DÒNG TRÊN CÙNG
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Quản lý khách hàng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(30, 41, 59));
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnAdd = new JButton("+ Thêm Khách Hàng") {
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
        btnAdd.setPreferredSize(new Dimension(180, 40));

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

        // 2. KHUNG NỘI DUNG CHÍNH (Card chứa bộ lọc và bảng)
        JPanel cardPanel = new RoundedPanel(16, Color.WHITE);
        cardPanel.setLayout(new BorderLayout(0, 15));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Thanh công cụ / Tìm kiếm
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

        tfSearch = new JTextField(20) {
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

                    String placeholder = "Tìm kiếm theo tên, mã KH, SĐT...";

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
            loadData();
        });

        filterBar.add(searchBox);
        filterBar.add(btnRefresh);
        cardPanel.add(filterBar, BorderLayout.NORTH);

        String[] columns = { "Mã KH", "Họ tên", "Số điện thoại", "Email", "Địa chỉ", "Thao tác" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
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

        table.getColumnModel().getColumn(5).setCellRenderer(new ActionCellRenderer());

        table.getColumnModel().getColumn(5).setMaxWidth(95);
        table.getColumnModel().getColumn(5).setMinWidth(95);

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 5 && row != -1) {
                    hoveredRow = row;
                    Rectangle cellRect = table.getCellRect(row, col, false);
                    int relativeX = e.getX() - cellRect.x;
                    if (relativeX < cellRect.width / 2) {
                        hoveredButton = 0; // Sửa
                    } else {
                        hoveredButton = 1; // Xóa
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
                if (col == 5 && row != -1) {
                    table.setRowSelectionInterval(row, row);
                    Rectangle cellRect = table.getCellRect(row, col, false);
                    int relativeX = e.getX() - cellRect.x;
                    if (relativeX < cellRect.width / 2) {
                        editSelectedCustomer();
                    } else {
                        deleteSelectedCustomer();
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

        // --- THANH ĐIỀU HƯỚNG PHÂN TRANG ---
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
    }

    private void loadData() {
        try {
            List<Customer> customers = customerController.getAllCustomers();
            if (customers != null) {
                currentFilteredCustomers = new ArrayList<>(customers);
            } else {
                currentFilteredCustomers.clear();
            }
            currentPage = 1;
            updatePagination();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu khách hàng: " + e.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filter() {
        String searchText = tfSearch.getText().toLowerCase().trim();
        try {
            List<Customer> allCustomers = customerController.getAllCustomers();
            currentFilteredCustomers.clear();
            if (allCustomers != null) {
                for (Customer c : allCustomers) {
                    boolean match = searchText.isEmpty()
                            || (c.getName() != null && c.getName().toLowerCase().contains(searchText))
                            || (c.getCustomerCode() != null && c.getCustomerCode().toLowerCase().contains(searchText))
                            || (c.getPhone() != null && c.getPhone().toLowerCase().contains(searchText))
                            || (c.getEmail() != null && c.getEmail().toLowerCase().contains(searchText));
                    if (match) {
                        currentFilteredCustomers.add(c);
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
        int totalRecords = currentFilteredCustomers.size();
        totalPages = Math.max(1, (int) Math.ceil((double) totalRecords / pageSize));
        if (currentPage > totalPages)
            currentPage = totalPages;
        renderTablePage();
    }

    private void renderTablePage() {
        tableModel.setRowCount(0);
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, currentFilteredCustomers.size());

        for (int i = startIndex; i < endIndex; i++) {
            Customer c = currentFilteredCustomers.get(i);
            tableModel.addRow(new Object[] {
                    c.getCustomerCode(),
                    c.getName(),
                    c.getPhone(),
                    c.getEmail(),
                    c.getAddress(),
                    ""
            });
        }

        tfPageInput.setText(String.valueOf(currentPage));
        lblTotalPages.setText("/ " + totalPages + " (Tổng: " + currentFilteredCustomers.size() + " khách hàng)");

        btnPrev.setEnabled(currentPage > 1);
    }

    public void editSelectedCustomer() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng khách hàng cần sửa trên bảng!");
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        String customerCode = tableModel.getValueAt(modelRow, 0).toString();
        try {
            List<Customer> customers = customerController.getAllCustomers();
            Customer selected = customers.stream().filter(c -> c.getCustomerCode().equals(customerCode)).findFirst()
                    .orElse(null);
            if (selected != null)
                showAddEditDialog(selected);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSelectedCustomer() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng khách hàng cần xóa trên bảng!");
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        String name = tableModel.getValueAt(modelRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa khách hàng \"" + name + "\"?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String customerCode = tableModel.getValueAt(modelRow, 0).toString();
                List<Customer> customers = customerController.getAllCustomers();
                Customer customer = customers.stream().filter(c -> c.getCustomerCode().equals(customerCode)).findFirst()
                        .orElse(null);
                if (customer != null && customerController.deleteCustomer(customer.getCustomerId())) {
                    JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa khách hàng do đang có đơn thuê liên quan!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddEditDialog(Customer customer) {
        boolean isEdit = customer != null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Sửa thông tin khách hàng" : "Thêm khách hàng mới", true);
        dialog.setSize(600, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = createFormPanel(customer);
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
            if (saveCustomer(dialog, formPanel, isEdit)) {
                dialog.dispose();
                loadData();
            }
        });
        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private JPanel createFormPanel(Customer customer) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField tfCode = new JTextField(20);
        JTextField tfName = new JTextField(20);
        JTextField tfPhone = new JTextField(20);
        JTextField tfEmail = new JTextField(20);
        JTextField tfAddress = new JTextField(20);

        Component[] fields = { tfCode, tfName, tfPhone, tfEmail, tfAddress };
        for (Component c : fields) {
            c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            if (c instanceof JComponent)
                ((JComponent) c).setPreferredSize(new Dimension(0, 36));
        }

        if (customer != null) {
            tfCode.setText(customer.getCustomerCode());
            tfCode.setEnabled(false);
            tfName.setText(customer.getName());
            tfPhone.setText(customer.getPhone());
            tfEmail.setText(customer.getEmail());
            tfAddress.setText(customer.getAddress());
        }

        int row = 0;
        addFormRow(panel, gbc, "Mã KH:", tfCode, row++);
        addFormRow(panel, gbc, "Họ tên:", tfName, row++);
        addFormRow(panel, gbc, "Số điện thoại:", tfPhone, row++);
        addFormRow(panel, gbc, "Email:", tfEmail, row++);
        addFormRow(panel, gbc, "Địa chỉ:", tfAddress, row++);

        panel.putClientProperty("tfCode", tfCode);
        panel.putClientProperty("tfName", tfName);
        panel.putClientProperty("tfPhone", tfPhone);
        panel.putClientProperty("tfEmail", tfEmail);
        panel.putClientProperty("tfAddress", tfAddress);

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
    // --- PANEL---
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

    private boolean saveCustomer(JDialog dialog, JPanel formPanel, boolean isEdit) {
        try {
            JTextField tfCode = (JTextField) formPanel.getClientProperty("tfCode");
            JTextField tfName = (JTextField) formPanel.getClientProperty("tfName");
            JTextField tfPhone = (JTextField) formPanel.getClientProperty("tfPhone");
            JTextField tfEmail = (JTextField) formPanel.getClientProperty("tfEmail");
            JTextField tfAddress = (JTextField) formPanel.getClientProperty("tfAddress");

            if (tfCode.getText().trim().isEmpty() || tfName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Mã khách hàng và Họ tên không được để trống!", "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }

            Customer c = new Customer();
            c.setCustomerCode(tfCode.getText().trim());
            c.setName(tfName.getText().trim());
            c.setPhone(tfPhone.getText().trim());
            c.setEmail(tfEmail.getText().trim());
            c.setAddress(tfAddress.getText().trim());

            if (isEdit) {
                return customerController.updateCustomer(c);
            } else {
                return customerController.addCustomer(c);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // --- RENDERER---
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
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);

            if (row == hoveredRow) {
                if (hoveredButton == 0) {
                    btnEdit.setBackground(new Color(219, 234, 254));
                    btnDelete.setBackground(new Color(254, 242, 242));
                } else if (hoveredButton == 1) {
                    btnEdit.setBackground(new Color(239, 246, 255));
                    btnDelete.setBackground(new Color(254, 226, 226));
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