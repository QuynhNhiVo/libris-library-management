package com.libris.view;

import com.libris.controller.BookController;
import com.libris.model.Book;
import com.libris.view.components.*;
import com.libris.view.interfaces.IBooksView;
import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class BooksView extends JPanel implements IBooksView {
    private com.libris.model.User currentUser;
    private FilterToolbar filterToolbar;
    private DataTablePanel tablePanel;
    private InspectorPanel inspectorPanel;

    private StatCard cardTotal;
    private StatCard cardAvailable;
    private StatCard cardRented;
    private StatCard cardPending;

    private JComboBox<String> cbCategory;
    private JComboBox<String> cbStatus;
    private JButton btnAddBook;

    private ModalDialog bookModal;
    private JTextField txtBookCode, txtTitle, txtAuthor, txtCategory, txtPublisher, txtYear, txtRentalPrice, txtDepositPrice;
    private JComboBox<String> cbFormStatus;

    private List<Book> currentBooks = new ArrayList<>();
    private Book selectedBook;
    private BookController controller;

    private ActionListener searchListener;
    private ActionListener saveBookListener;
    private ActionListener deleteBookListener;
    private Runnable selectBookCallback;

    public BooksView() {
        initComponents();
        this.controller = new BookController(this, null);
    }

    public BooksView(com.libris.model.User user) {
        this.currentUser = user;
        initComponents();
        this.controller = new BookController(this, null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(LibrisColors.CANVAS);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Top KPI Stat Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        statsPanel.setOpaque(false);

        cardTotal = new StatCard("TỔNG SÁCH", "0", "Trong danh mục kho", null, LibrisColors.PRIMARY);
        cardAvailable = new StatCard("SÁCH CÓ SẴN", "0", "Đang sẵn sàng", null, LibrisColors.STATUS_SUCCESS);
        cardRented = new StatCard("ĐANG CHO THUÊ", "0", "Độc giả đang giữ", null, LibrisColors.STATUS_RENTED);
        cardPending = new StatCard("CHỜ DUYỆT THUÊ", "0", "Yêu cầu mới", null, LibrisColors.STATUS_WARNING);

        statsPanel.add(cardTotal);
        statsPanel.add(cardAvailable);
        statsPanel.add(cardRented);
        statsPanel.add(cardPending);

        // Filter Toolbar (Đã bỏ xuất excel và nhập danh mục)
        filterToolbar = new FilterToolbar("Tìm theo mã B001..., tên sách, tác giả...");

        // Thêm option "Tất cả" cho Filter hoạt động tốt
        cbCategory = new JComboBox<>(new String[]{"Tất cả", "Thiếu nhi", "Văn học", "Thơ ca", "Khoa học", "Tiểu thuyết", "Truyện tranh", "Kỹ năng sống", "Ngoại văn", "Kinh tế", "Lịch sử"});
        cbStatus = new JComboBox<>(new String[]{"Tất cả", "Available", "Rented", "Pending"});

        filterToolbar.addFilterComboBox(cbCategory);
        filterToolbar.addFilterComboBox(cbStatus);

        boolean isAdmin = currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole());
        if (isAdmin) {
            btnAddBook = FilterToolbar.createPrimaryButton("+ Thêm sách mới");
            btnAddBook.addActionListener(e -> openAddBookModal());
            filterToolbar.addActionButton(btnAddBook);
        }
        
        // Main Grid Layout Container (Table 70% / Inspector 30%)
        JPanel mainGrid = new JPanel(new BorderLayout(16, 0));
        mainGrid.setOpaque(false);

        // Left Table
        String[] cols = {"Mã sách", "Tên sách", "Tác giả", "Thể loại", "NXB / Năm", "Trạng thái", "Giá thuê / Ngày", "Tiền cọc"};
        tablePanel = new DataTablePanel(cols);
        tablePanel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablePanel.getTable().getSelectedRow();
                int modelRow = row >= 0 ? tablePanel.getTable().convertRowIndexToModel(row) : -1;
                if (modelRow >= 0 && modelRow < currentBooks.size()) {
                    selectedBook = currentBooks.get(modelRow);
                    showBookDetailsInInspector(selectedBook);
                    if (selectBookCallback != null) selectBookCallback.run();
                }
            }
        });

        // Right 352px Inspector Panel
        inspectorPanel = new InspectorPanel("Chi tiết sách");

        mainGrid.add(tablePanel, BorderLayout.CENTER);
        mainGrid.add(inspectorPanel, BorderLayout.EAST);

        JPanel topContainer = new JPanel(new BorderLayout(0, 16));
        topContainer.setOpaque(false);
        topContainer.add(statsPanel, BorderLayout.NORTH);
        topContainer.add(filterToolbar, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);
        add(mainGrid, BorderLayout.CENTER);

        initBookModal();
    }

    private void showBookDetailsInInspector(Book b) {
        if (b == null) {
            inspectorPanel.clearContent();
            return;
        }
        inspectorPanel.setInspectorTitle(b.getTitle());
        inspectorPanel.setInspectorSubtitle(b.getAuthor() + " • " + b.getCategory());

        JPanel p = inspectorPanel.getContentPanel();
        p.removeAll();

        // Book Cover Simulation Box
        JPanel coverBox = new JPanel(new BorderLayout());
        coverBox.setBackground(LibrisColors.PRIMARY);
        coverBox.setPreferredSize(new Dimension(0, 120));
        coverBox.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lblCoverTitle = new JLabel("<html><center><font color='#ffffff'><b>" + b.getTitle() + "</b></font><br><font color='#f59e0b' size='2'>" + b.getAuthor() + "</font></center></html>", SwingConstants.CENTER);
        lblCoverTitle.setFont(LibrisFonts.HEADLINE_LG);
        coverBox.add(lblCoverTitle, BorderLayout.CENTER);

        p.add(coverBox);
        p.add(Box.createRigidArea(new Dimension(0, 16)));

        // ISBN & Barcode Box
        p.add(createDetailRow("Mã ấn phẩm:", b.getBookCode()));
        p.add(createDetailRow("Nhà xuất bản:", b.getPublisher() + " (" + b.getPublishYear() + ")"));
        p.add(createDetailRow("Trạng thái hiện tại:", b.getBookStatus()));
        p.add(createDetailRow("Giá thuê / ngày:", String.format("%,d VNĐ", b.getRentalPrice())));
        p.add(createDetailRow("Tiền cọc yêu cầu:", String.format("%,d VNĐ", b.getDepositPrice())));
        p.add(createDetailRow("Lượt mượn:", b.getRentCount() + " lần"));

        p.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel actions = inspectorPanel.getActionPanel();
        actions.removeAll();

        if (currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole())) {
            JButton btnEdit = FilterToolbar.createSecondaryButton("Chỉnh sửa");
            btnEdit.addActionListener(e -> openEditBookModal(b));
            JButton btnDel = FilterToolbar.createSecondaryButton("Xóa");
            btnDel.addActionListener(e -> {
                if (deleteBookListener != null) deleteBookListener.actionPerformed(e);
            });
            actions.add(btnEdit);
            actions.add(btnDel);
        }
        inspectorPanel.revalidate();
        inspectorPanel.repaint();
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

    private void initBookModal() {
        Window topWindow = SwingUtilities.getWindowAncestor(this);
        bookModal = new ModalDialog(topWindow, "Thông tin sách");
        
        JPanel body = bookModal.getBodyPanel();
        body.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtBookCode = new JTextField(15);
        txtTitle = new JTextField(15);
        txtAuthor = new JTextField(15);
        txtCategory = new JTextField(15);
        txtPublisher = new JTextField(15);
        txtYear = new JTextField(15);
        txtRentalPrice = new JTextField(15);
        txtDepositPrice = new JTextField(15);
        cbFormStatus = new JComboBox<>(new String[]{"Available", "Rented", "Pending"});

        addFormField(body, gbc, 0, "Mã sách:", txtBookCode);
        addFormField(body, gbc, 1, "Tên sách:", txtTitle);
        addFormField(body, gbc, 2, "Tác giả:", txtAuthor);
        addFormField(body, gbc, 3, "Thể loại:", txtCategory);
        addFormField(body, gbc, 4, "Nhà xuất bản:", txtPublisher);
        addFormField(body, gbc, 5, "Năm XB:", txtYear);
        addFormField(body, gbc, 6, "Giá thuê (VNĐ):", txtRentalPrice);
        addFormField(body, gbc, 7, "Tiền cọc (VNĐ):", txtDepositPrice);
        addFormField(body, gbc, 8, "Trạng thái:", cbFormStatus);

        bookModal.getSaveButton().addActionListener(e -> {
            if (saveBookListener != null) {
                saveBookListener.actionPerformed(e);
            }
            bookModal.dispose();
        });
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridy = row;
        
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(LibrisFonts.BODY_SM);
        panel.add(lbl, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        field.setFont(LibrisFonts.BODY_MD);
        panel.add(field, gbc);
    }

    private void openAddBookModal() {
        selectedBook = null;
        txtBookCode.setText("");
        txtBookCode.setEditable(true);
        txtTitle.setText("");
        txtAuthor.setText("");
        txtCategory.setText("");
        txtPublisher.setText("");
        txtYear.setText("2024");
        txtRentalPrice.setText("10000");
        txtDepositPrice.setText("100000");
        cbFormStatus.setSelectedItem("Available");
        bookModal.setVisible(true);
    }

    private void openEditBookModal(Book b) {
        if (b == null) return;
        selectedBook = b;
        txtBookCode.setText(b.getBookCode());
        txtBookCode.setEditable(false);
        txtTitle.setText(b.getTitle());
        txtAuthor.setText(b.getAuthor());
        txtCategory.setText(b.getCategory());
        txtPublisher.setText(b.getPublisher());
        txtYear.setText(String.valueOf(b.getPublishYear()));
        txtRentalPrice.setText(String.valueOf(b.getRentalPrice()));
        txtDepositPrice.setText(String.valueOf(b.getDepositPrice()));
        cbFormStatus.setSelectedItem(b.getBookStatus());
        bookModal.setVisible(true);
    }

    @Override
    public void updateKPIs(int total, int available, int rented, int pending) {
        cardTotal.setValue(String.valueOf(total));
        cardAvailable.setValue(String.valueOf(available));
        cardRented.setValue(String.valueOf(rented));
        cardPending.setValue(String.valueOf(pending));
    }

    @Override
    public void showBooks(List<Book> books) {this.currentBooks = books != null ? books : new ArrayList<>();
        DefaultTableModel model = tablePanel.getTableModel();
        model.setRowCount(0);

        for (Book b : currentBooks) {
            model.addRow(new Object[]{
                    b.getBookCode(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getCategory(),
                    b.getPublisher() + " (" + b.getPublishYear() + ")",
                    b.getBookStatus(),
                    String.format("%,d", b.getRentalPrice()),
                    String.format("%,d", b.getDepositPrice())
            });
        }
        tablePanel.updateFooterCount();
    }

    @Override
    public void showError(String message) {
        ToastPanel.showToast(SwingUtilities.getWindowAncestor(this), message, true);
    }

    @Override
    public void showMessage(String message) {
        ToastPanel.showToast(SwingUtilities.getWindowAncestor(this), message, false);
    }

    @Override
    public Book getSelectedBook() {
        return selectedBook;
    }

    @Override
    public Book getBookFormData() {
        Book b = new Book();
        if (selectedBook != null) {
            b.setBookId(selectedBook.getBookId());
        }
        b.setBookCode(txtBookCode.getText().trim());
        b.setTitle(txtTitle.getText().trim());
        b.setAuthor(txtAuthor.getText().trim());
        b.setCategory(txtCategory.getText().trim());
        b.setPublisher(txtPublisher.getText().trim());
        try { b.setPublishYear(Integer.parseInt(txtYear.getText().trim())); } catch (Exception ignored) {}
        try { b.setRentalPrice(Integer.parseInt(txtRentalPrice.getText().trim())); } catch (Exception ignored) {}
        try { b.setDepositPrice(Integer.parseInt(txtDepositPrice.getText().trim())); } catch (Exception ignored) {}
        b.setBookStatus((String) cbFormStatus.getSelectedItem());
        return b;
    }

    @Override
    public void setBookFormData(Book book) {
        if (book != null) openEditBookModal(book);
    }

    @Override
    public String getSearchKeyword() {
        return filterToolbar.getSearchField().getText().trim();
    }

    @Override
    public String getCategoryFilter() {
        return (String) cbCategory.getSelectedItem();
    }

    @Override
    public String getStatusFilter() {
        return (String) cbStatus.getSelectedItem();
    }

    @Override
    public void addSearchListener(ActionListener listener) {
        this.searchListener = listener;
        filterToolbar.getSearchField().addActionListener(listener);
        cbCategory.addActionListener(listener);
        cbStatus.addActionListener(listener);
    }

    @Override
    public void addSaveBookListener(ActionListener listener) {
        this.saveBookListener = listener;
    }

    @Override
    public void addDeleteBookListener(ActionListener listener) {
        this.deleteBookListener = listener;
    }

    @Override
    public void addSelectBookListener(Runnable callback) {
        this.selectBookCallback = callback;
    }

    public void refreshData() {
        if (controller != null) {
            controller.loadBooks();
        }
    }
}
