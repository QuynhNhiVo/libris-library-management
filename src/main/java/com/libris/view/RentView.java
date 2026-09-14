package com.libris.view;

import com.libris.controller.RentController;
import com.libris.model.Book;
import com.libris.model.User;
import com.libris.view.components.*;
import com.libris.view.interfaces.IRentView;
import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;
import com.libris.view.theme.LibrisMetrics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentView extends JPanel implements IRentView {
    private FilterToolbar filterToolbar;
    private KpiTabBar categoryPillTabs;
    private JPanel gridContainer;
    private JScrollPane gridScrollPane;
    private JPanel cartPanel;

    private JPanel cartItemsListPanel;
    private JToggleButton btn7Days, btn14Days, btn30Days;
    private JTextArea txtNotes;
    private JLabel lblTotalCount, lblTotalRentalFee, lblTotalDeposit, lblGrandTotal, lblReturnDate;
    private JButton btnSubmitOrder;
    private JTextField txtCustomerCode;

    private List<Book> availableBooks = new ArrayList<>();
    private List<Book> cartBooks = new ArrayList<>();
    private int rentDays = 7;
    private User currentUser;
    private RentController controller;

    private ActionListener searchListener;
    private ActionListener submitListener;

    private JPanel filterHeader;
    private String[] currentCatArray = {"Tất cả"};

    public RentView(User user) {
        this.currentUser = user;
        initComponents();
        this.controller = new RentController(this, currentUser, null);
    }

    private String currentCategory = "Tất cả";

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(LibrisColors.CANVAS);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Top Title Banner
        JPanel headerPanel = new JPanel(new BorderLayout(16, 0));
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Kính mời độc giả chọn sách mượn");
        lblTitle.setFont(LibrisFonts.DISPLAY_MD);
        lblTitle.setForeground(LibrisColors.PRIMARY);

        JLabel lblUserInfo = new JLabel("<html><font color='#059669'><b>24 đầu sách khả dụng</b></font> • Độc giả: <b>"
                + (currentUser != null ? currentUser.getUsername() : "customer1") + "</b></html>");
        lblUserInfo.setFont(LibrisFonts.BODY_MD);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblUserInfo, BorderLayout.EAST);

        // Filter & Category Pills
        filterToolbar = new FilterToolbar("Tìm kiếm sách khả dụng trong thư viện...");
        String[] cats = { "Tất cả", "Văn học", "Thiếu nhi", "Khoa học", "Khác" };
        categoryPillTabs = new KpiTabBar(cats, idx -> {
            currentCategory = cats[idx];
            if (controller != null)
                controller.loadAvailableBooks();
        });

        filterHeader = new JPanel(new BorderLayout(0, 8));
        filterHeader.setOpaque(false);
        filterHeader.add(filterToolbar, BorderLayout.NORTH);
        //filterHeader.add(categoryPillTabs, BorderLayout.SOUTH);

        // Main Layout (65% Grid / 35% Cart)
        JPanel mainGrid = new JPanel(new BorderLayout(16, 0));
        mainGrid.setOpaque(false);

        // 3-Column Card Grid (65%)
        gridContainer = new JPanel(new GridLayout(0, 3, 12, 12));
        gridContainer.setBackground(LibrisColors.CANVAS);

        gridScrollPane = new JScrollPane(gridContainer);
        gridScrollPane.setBorder(null);
        gridScrollPane.getViewport().setBackground(LibrisColors.CANVAS);

        // Cart Sidebar (35%, 352px)
        cartPanel = createCartPanel();

        mainGrid.add(gridScrollPane, BorderLayout.CENTER);
        mainGrid.add(cartPanel, BorderLayout.EAST);

        JPanel topSection = new JPanel(new BorderLayout(0, 12));
        topSection.setOpaque(false);
        topSection.add(headerPanel, BorderLayout.NORTH);
        topSection.add(filterHeader, BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);
        add(mainGrid, BorderLayout.CENTER);
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setPreferredSize(new Dimension(LibrisMetrics.INSPECTOR_WIDTH, 0));
        panel.setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        // Header
        JLabel lblCartTitle = new JLabel("📋 PHIẾU MƯỢN SÁCH TƯƠNG TÁC");
        lblCartTitle.setFont(LibrisFonts.TITLE_MD);
        lblCartTitle.setForeground(LibrisColors.PRIMARY);
        panel.add(lblCartTitle, BorderLayout.NORTH);

        // Body Content
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);

        // Selected Items List Container
        JLabel lblSelected = new JLabel("DANH SÁCH SÁCH ĐÃ CHỌN");
        lblSelected.setFont(LibrisFonts.LABEL_SM);
        lblSelected.setForeground(LibrisColors.PLACEHOLDER);

        cartItemsListPanel = new JPanel();
        cartItemsListPanel.setLayout(new BoxLayout(cartItemsListPanel, BoxLayout.Y_AXIS));
        cartItemsListPanel.setOpaque(false);

        JScrollPane cartScroll = new JScrollPane(cartItemsListPanel);
        cartScroll.setPreferredSize(new Dimension(0, 110));
        cartScroll.setBorder(BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1));
        cartScroll.getViewport().setBackground(LibrisColors.CANVAS);

        // Duration Selectors
        JLabel lblDuration = new JLabel("THỜI HẠN MƯỢN DỰ KIẾN");
        lblDuration.setFont(LibrisFonts.LABEL_SM);
        lblDuration.setForeground(LibrisColors.PLACEHOLDER);

        JPanel daysPanel = new JPanel(new GridLayout(1, 3, 6, 0));
        daysPanel.setOpaque(false);

        ButtonGroup group = new ButtonGroup();
        btn7Days = new JToggleButton("7 Ngày", true);
        btn14Days = new JToggleButton("14 Ngày");
        btn30Days = new JToggleButton("30 Ngày");

        group.add(btn7Days);
        group.add(btn14Days);
        group.add(btn30Days);
        daysPanel.add(btn7Days);
        daysPanel.add(btn14Days);
        daysPanel.add(btn30Days);

        ActionListener dayChange = e -> {
            if (btn7Days.isSelected())
                rentDays = 7;
            else if (btn14Days.isSelected())
                rentDays = 14;
            else if (btn30Days.isSelected())
                rentDays = 30;
            updateCartUI();
        };

        btn7Days.addActionListener(dayChange);
        btn14Days.addActionListener(dayChange);
        btn30Days.addActionListener(dayChange);

        // Notes
        txtNotes = new JTextArea(2, 20);
        txtNotes.setFont(LibrisFonts.BODY_SM);
        txtNotes.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        JScrollPane notesScroll = new JScrollPane(txtNotes);
        bodyPanel.add(new JLabel("Ghi chú cho tiếp nhận:"));
        bodyPanel.add(notesScroll);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        notesScroll.setBorder(BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1));
        boolean isAdmin = currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole());
        if (isAdmin) {
            JLabel lblCust = new JLabel("Mã khách hàng (Admin tạo đơn):");
            lblCust.setFont(LibrisFonts.LABEL_SM);
            txtCustomerCode = new JTextField();
            txtCustomerCode.setFont(LibrisFonts.BODY_MD);
            bodyPanel.add(lblCust);
            bodyPanel.add(txtCustomerCode);
            bodyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // Calculations Breakdown
        JPanel summaryPanel = new JPanel(new GridLayout(5, 1, 0, 4));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, LibrisColors.HAIRLINE_BORDER),
                BorderFactory.createEmptyBorder(8, 0, 0, 0)));

        lblTotalCount = new JLabel("Số lượng tài liệu: 0 cuốn");
        lblTotalRentalFee = new JLabel("Phí thuê ước tính: 0 VNĐ");
        lblTotalDeposit = new JLabel("Tiền cọc thế chân: 0 VNĐ (Hoàn 100%)");
        lblGrandTotal = new JLabel("TẠM TÍNH THANH TOÁN: 0 VNĐ");
        lblGrandTotal.setFont(LibrisFonts.TITLE_LG);
        lblGrandTotal.setForeground(LibrisColors.PRIMARY);

        lblReturnDate = new JLabel("Hạn trả dự kiến: " + LocalDate.now().plusDays(rentDays));
        lblReturnDate.setFont(LibrisFonts.BODY_SM);
        lblReturnDate.setForeground(LibrisColors.STATUS_RENTED_TEXT);

        summaryPanel.add(lblTotalCount);
        summaryPanel.add(lblTotalRentalFee);
        summaryPanel.add(lblTotalDeposit);
        summaryPanel.add(lblGrandTotal);
        summaryPanel.add(lblReturnDate);

        bodyPanel.add(lblSelected);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        bodyPanel.add(cartScroll);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bodyPanel.add(lblDuration);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        bodyPanel.add(daysPanel);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bodyPanel.add(new JLabel("Ghi chú cho thủ thư tiếp nhận:"));
        bodyPanel.add(notesScroll);
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bodyPanel.add(summaryPanel);

        // Submit Button
        btnSubmitOrder = FilterToolbar.createPrimaryButton("✈ Gửi yêu cầu mượn sách");
        btnSubmitOrder.setBackground(LibrisColors.PRIMARY);
        btnSubmitOrder.setPreferredSize(new Dimension(0, 44));
        btnSubmitOrder.addActionListener(e -> {
            if (submitListener != null) {
                submitListener.actionPerformed(e);
            }
        });

        panel.add(bodyPanel, BorderLayout.CENTER);
        panel.add(btnSubmitOrder, BorderLayout.SOUTH);

        return panel;
    }

    private void renderBookGrid() {
        gridContainer.removeAll();
        for (Book b : availableBooks) {
            gridContainer.add(createBookCard(b));
        }
        gridContainer.revalidate();
        gridContainer.repaint();
    }

    private JPanel createBookCard(Book b) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(LibrisColors.SURFACE_CONTAINER_LOWEST);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        // Cover Top Header
        JPanel coverHeader = new JPanel(new BorderLayout());
        coverHeader.setBackground(LibrisColors.PRIMARY);
        coverHeader.setPreferredSize(new Dimension(0, 85));
        coverHeader.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel lblTitle = new JLabel(
                "<html><center><font color='#ffffff'><b>" + b.getTitle() + "</b></font></center></html>",
                SwingConstants.CENTER);
        lblTitle.setFont(LibrisFonts.TITLE_MD);
        coverHeader.add(lblTitle, BorderLayout.CENTER);

        // Card Info Body
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel lblAuthor = new JLabel(b.getAuthor());
        lblAuthor.setFont(LibrisFonts.BODY_SM);
        lblAuthor.setForeground(LibrisColors.ON_SURFACE_VARIANT);

        JLabel lblPrice = new JLabel(
                String.format("Giá thuê: %,dđ/ngày • Cọc: %,dđ", b.getRentalPrice(), b.getDepositPrice()));
        lblPrice.setFont(LibrisFonts.CODE_SM);
        lblPrice.setForeground(LibrisColors.PRIMARY);

        StatusBadge badge = new StatusBadge("Available");

        infoPanel.add(lblAuthor);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        infoPanel.add(lblPrice);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        infoPanel.add(badge);

        // Select CTA Button
        JButton btnAdd = FilterToolbar.createPrimaryButton("+ Chọn mượn");
        btnAdd.setFont(LibrisFonts.BODY_SM);
        btnAdd.setBackground(LibrisColors.PRIMARY);
        btnAdd.addActionListener(e -> {
            if (!cartBooks.contains(b)) {
                cartBooks.add(b);
                updateCartUI();
            }
        });

        card.add(coverHeader, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(btnAdd, BorderLayout.SOUTH);

        return card;
    }

    private void updateCartUI() {
        cartItemsListPanel.removeAll();
        int count = cartBooks.size();
        int totalFee = 0;
        int totalDeposit = 0;

        for (Book b : cartBooks) {
            totalFee += b.getRentalPrice() * rentDays;
            totalDeposit += b.getDepositPrice();

            JPanel itemRow = new JPanel(new BorderLayout());
            itemRow.setBackground(LibrisColors.CANVAS);
            itemRow.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, LibrisColors.HAIRLINE_BORDER),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)));

            JLabel lblName = new JLabel(b.getTitle());
            lblName.setFont(LibrisFonts.BODY_SM);

            JButton btnRemove = new JButton("✕");
            btnRemove.setFont(LibrisFonts.BODY_SM);
            btnRemove.setForeground(LibrisColors.STATUS_ERROR_TEXT);
            btnRemove.setFocusPainted(false);
            btnRemove.setContentAreaFilled(false);
            btnRemove.setBorderPainted(false);
            btnRemove.addActionListener(e -> {
                cartBooks.remove(b);
                updateCartUI();
            });

            itemRow.add(lblName, BorderLayout.CENTER);
            itemRow.add(btnRemove, BorderLayout.EAST);
            cartItemsListPanel.add(itemRow);
        }

        cartItemsListPanel.revalidate();
        cartItemsListPanel.repaint();

        lblTotalCount.setText("Số lượng tài liệu: " + count + " cuốn");
        lblTotalRentalFee.setText(String.format("Phí thuê (%d ngày): %,d VNĐ", rentDays, totalFee));
        lblTotalDeposit.setText(String.format("Tiền cọc thế chân: %,d VNĐ", totalDeposit));
        lblGrandTotal.setText(String.format("TẠM TÍNH THANH TOÁN: %,d VNĐ", totalFee + totalDeposit));
        lblReturnDate.setText("Hạn trả dự kiến: " + LocalDate.now().plusDays(rentDays));
    }

    public void setAdminCustomerInput(String code) {
        if (txtCustomerCode != null) {
            txtCustomerCode.setText(code);
        }
    }

    public void setCategories(List<String> categories) {
        List<String> catList = new ArrayList<>();
        catList.add("Tất cả");
        catList.addAll(categories);
        
        String[] newCats = catList.toArray(new String[0]);
        if (java.util.Arrays.equals(currentCatArray, newCats)) return;
        currentCatArray = newCats;
        
        if (categoryPillTabs != null) {
            filterHeader.remove(categoryPillTabs);
        }
        categoryPillTabs = new KpiTabBar(currentCatArray, idx -> {
            currentCategory = currentCatArray[idx];
            if (controller != null) controller.loadAvailableBooks();
        });
        filterHeader.add(categoryPillTabs, BorderLayout.SOUTH);
        filterHeader.revalidate(); filterHeader.repaint();
    }

    @Override
    public void showAvailableBooks(List<Book> books) {
        this.availableBooks = books != null ? books : new ArrayList<>();
        renderBookGrid();
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
    public String getSearchKeyword() {
        return filterToolbar.getSearchField().getText().trim();
    }

    @Override
    public List<Book> getCartBooks() {
        return cartBooks;
    }

    @Override
    public int getRentDays() {
        return rentDays;
    }

    @Override
    public String getNotes() {
        return txtNotes.getText().trim();
    }

    @Override
    public void clearCart() {
        cartBooks.clear();
        updateCartUI();
    }

    @Override
    public void addSearchListener(ActionListener listener) {
        this.searchListener = listener;
        filterToolbar.getSearchField().addActionListener(listener);
    }

    @Override
    public void addSubmitRentRequestListener(ActionListener listener) {
        this.submitListener = listener;
    }

    @Override
    public String getCategoryFilter() {
        return currentCategory;
    }

    @Override
    public String getAdminCustomerInput() {
        return txtCustomerCode != null ? txtCustomerCode.getText().trim() : "";
    }

    public void refreshData() {
        if (controller != null) {
            controller.loadAvailableBooks();
        }
    }

    public String getCurrentCategory() {
        return currentCategory;
    }
}