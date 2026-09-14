package com.libris.view;

import com.libris.controller.CustomerController;
import com.libris.model.Customer;
import com.libris.view.components.*;
import com.libris.view.interfaces.ICustomersView;
import com.libris.view.theme.LibrisColors;
import com.libris.view.theme.LibrisFonts;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CustomersView extends JPanel implements ICustomersView {
    private FilterToolbar filterToolbar;
    private KpiTabBar tierTabBar;
    private DataTablePanel tablePanel;
    private InspectorPanel inspectorPanel;

    private StatCard cardTotal;
    private StatCard cardActiveRentals;
    private StatCard cardVip;
    private StatCard cardWarnings;

    private JButton btnAddCustomer;

    private ModalDialog customerModal;
    private JTextField txtCode, txtName, txtPhone, txtAddress, txtEmail;

    private List<Customer> customerList = new ArrayList<>();
    private Customer selectedCustomer;
    private CustomerController controller;

    private ActionListener searchListener;
    private ActionListener saveCustomerListener;
    private ActionListener deleteCustomerListener;
    private Runnable selectCustomerCallback;

    public CustomersView() {
        initComponents();
        this.controller = new CustomerController(this, null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 16));
        setBackground(LibrisColors.CANVAS);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Top Member KPI Metric Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        statsPanel.setOpaque(false);

        cardTotal = new StatCard("TỔNG ĐỘC GIẢ", "13", "Mã C001 đến C013", null, LibrisColors.PRIMARY);
        cardActiveRentals = new StatCard("ĐƠN MƯỢN HIỆU LỰC", "8", "Đang giữ tài liệu", null,
                LibrisColors.STATUS_RENTED);
        cardVip = new StatCard("HẠNG KIM CƯƠNG / VIP", "3", "Bạn đọc thân thiết", null, LibrisColors.SECONDARY);
        cardWarnings = new StatCard("CẢNH BÁO QUÁ HẠN", "2", "Cần nhắc trả sách", null, LibrisColors.STATUS_ERROR);

        statsPanel.add(cardTotal);
        statsPanel.add(cardActiveRentals);
        statsPanel.add(cardVip);
        statsPanel.add(cardWarnings);

        // Filter Toolbar & Tier Tabs
        filterToolbar = new FilterToolbar("Tìm theo tên, SĐT, CCCD...");
        btnAddCustomer = FilterToolbar.createPrimaryButton("+ Thêm khách hàng mới");
        btnAddCustomer.addActionListener(e -> openAddModal());

        filterToolbar.addActionButton(btnAddCustomer);

        String[] tabs = { "Tất cả (13)", "Kim Cương (3)", "Vàng (4)", "Tiêu Chuẩn (6)", "Cảnh báo (2)" };
        tierTabBar = new KpiTabBar(tabs, idx -> {
            // TODO(data-model): Filter by customer rank
        });

        JPanel toolbarContainer = new JPanel(new BorderLayout(0, 8));
        toolbarContainer.setOpaque(false);
        toolbarContainer.add(filterToolbar, BorderLayout.NORTH);
        toolbarContainer.add(tierTabBar, BorderLayout.SOUTH);

        // Main Grid Layout Container (Table 65% / Inspector 35%)
        JPanel mainGrid = new JPanel(new BorderLayout(16, 0));
        mainGrid.setOpaque(false);

        // Member Directory Table
        String[] cols = { "Mã KH", "Họ và tên", "Liên hệ (SĐT / Email)", "Địa bàn cư trú", "Phân hạng thẻ"};
        tablePanel = new DataTablePanel(cols);

        tablePanel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablePanel.getTable().getSelectedRow();
                int modelRow = row >= 0 ? tablePanel.getTable().convertRowIndexToModel(row) : -1;
                if (modelRow >= 0 && modelRow < customerList.size()) {
                    selectedCustomer = customerList.get(modelRow);
                    showCustomerInspector(selectedCustomer);
                    if (selectCustomerCallback != null)
                        selectCustomerCallback.run();
                }
            }
        });

        // 352px Inspector Panel
        inspectorPanel = new InspectorPanel("Hồ sơ bạn đọc chi tiết");

        mainGrid.add(tablePanel, BorderLayout.CENTER);
        mainGrid.add(inspectorPanel, BorderLayout.EAST);

        JPanel topContainer = new JPanel(new BorderLayout(0, 12));
        topContainer.setOpaque(false);
        topContainer.add(statsPanel, BorderLayout.NORTH);
        topContainer.add(toolbarContainer, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);
        add(mainGrid, BorderLayout.CENTER);

        initCustomerModal();
    }

    private void showCustomerInspector(Customer c) {
        if (c == null) {
            inspectorPanel.clearContent();
            return;
        }
        inspectorPanel.setInspectorTitle(c.getFullName());
        inspectorPanel.setInspectorSubtitle("Mã KH: " + c.getCustomerCode() + " • Hạng Tiêu Chuẩn");

        JPanel p = inspectorPanel.getContentPanel();
        p.removeAll();

        // Profile Portrait & Trust Score Box
        JPanel portraitBox = new JPanel(new BorderLayout(12, 0));
        portraitBox.setBackground(LibrisColors.CANVAS);
        portraitBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        String initials = getInitials(c.getFullName());
        JLabel lblAvatar = new JLabel(initials, SwingConstants.CENTER);
        lblAvatar.setFont(LibrisFonts.HEADLINE_LG);
        lblAvatar.setOpaque(true);
        lblAvatar.setBackground(LibrisColors.PRIMARY);
        lblAvatar.setForeground(LibrisColors.ON_PRIMARY);
        lblAvatar.setPreferredSize(new Dimension(50, 50));

        JPanel scoreText = new JPanel(new GridLayout(2, 1, 0, 2));
        scoreText.setOpaque(false);
        JLabel lblRank = new JLabel("Mã KH: " + c.getCustomerCode());
        lblRank.setFont(LibrisFonts.BODY_SM);
        lblRank.setForeground(LibrisColors.ON_SURFACE_VARIANT);

        JLabel lblScore = new JLabel("Thông tin liên hệ đã xác minh");
        lblScore.setFont(LibrisFonts.TITLE_MD);
        lblScore.setForeground(LibrisColors.STATUS_SUCCESS_TEXT);

        scoreText.add(lblRank);
        scoreText.add(lblScore);

        portraitBox.add(lblAvatar, BorderLayout.WEST);
        portraitBox.add(scoreText, BorderLayout.CENTER);
        p.add(portraitBox);
        p.add(Box.createRigidArea(new Dimension(0, 12)));

        p.add(createDetailRow("Số điện thoại:", c.getPhone()));
        p.add(createDetailRow("Email liên hệ:", c.getEmail()));
        p.add(createDetailRow("Địa chỉ cư trú:", c.getAddress()));

        p.add(Box.createRigidArea(new Dimension(0, 10)));
        // 3 Summary Metrics

        p.add(Box.createRigidArea(new Dimension(0, 12)));
        JLabel lblHeldHeader = new JLabel("SÁCH BẠN ĐỌC ĐANG MƯỢN VẬT LÝ");
        lblHeldHeader.setFont(LibrisFonts.LABEL_SM);
        lblHeldHeader.setForeground(LibrisColors.PLACEHOLDER);
        p.add(lblHeldHeader);
        //p.add(Box.createRigidArea(new Dimension(0, 8)));

        try {
            List<com.libris.model.RentalOrder> rentals = new com.libris.dao.RentDAO().getCustomerRentals(c.getCustomerId());
            boolean hasActive = false;
            for (com.libris.model.RentalOrder ro : rentals) {
                if ("Renting".equalsIgnoreCase(ro.getOrderStatus()) || "Pending".equalsIgnoreCase(ro.getOrderStatus())) {
                    for(com.libris.model.RentalOrderDetail detail : ro.getDetails()) {
                        p.add(createHeldBookRow(detail.getBook().getTitle(), "Đơn: #" + ro.getOrderCode() + " | Trạng thái: " + ro.getOrderStatus()));
                        hasActive = true;
                    }
                }
            }
            if (!hasActive) p.add(createHeldBookRow("Không có sách đang mượn", ""));
        } catch (Exception ex) {
             p.add(createHeldBookRow("Lỗi tải dữ liệu", ""));
        }

        JPanel actions = inspectorPanel.getActionPanel();
        actions.removeAll();

        JButton btnNewOrder = FilterToolbar.createPrimaryButton("➕ Tạo phiếu mượn");
        btnNewOrder.setBackground(LibrisColors.PRIMARY);
        btnNewOrder.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof MainFrame) {
                ((MainFrame) window).navigateToRentWithCustomer(c.getCustomerCode()); 
            }
        });

        JButton btnEdit = FilterToolbar.createSecondaryButton("✏️ Sửa hồ sơ");
        btnEdit.addActionListener(e -> openEditModal(c));

        JButton btnLock = FilterToolbar.createSecondaryButton("🔒 Tạm khóa");
        btnLock.setForeground(LibrisColors.STATUS_ERROR_TEXT);

        actions.add(btnNewOrder);
        actions.add(btnEdit);
        actions.add(btnLock);

        inspectorPanel.revalidate();
        inspectorPanel.repaint();
    }

    private JPanel createMiniStat(String title, String value) {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.setBackground(LibrisColors.CANVAS);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LibrisColors.HAIRLINE_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)));
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(LibrisFonts.LABEL_SM);
        t.setForeground(LibrisColors.ON_SURFACE_VARIANT);
        JLabel v = new JLabel(value, SwingConstants.CENTER);
        v.setFont(LibrisFonts.TITLE_MD);
        v.setForeground(LibrisColors.PRIMARY);
        p.add(t);
        p.add(v);
        return p;
    }

    private JPanel createHeldBookRow(String title, String due) {
        JPanel r = new JPanel(new BorderLayout());
        r.setBackground(LibrisColors.STATUS_RENTED_BG);
        r.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LibrisColors.STATUS_RENTED_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        JLabel l1 = new JLabel(
                "<html><b>" + title + "</b><br><font color='#1d4ed8' size='2'>" + due + "</font></html>");
        l1.setFont(LibrisFonts.BODY_SM);
        r.add(l1, BorderLayout.CENTER);
        return r;
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty())
            return "KH";
        String[] parts = name.trim().split(" ");
        if (parts.length >= 2) {
            return (parts[parts.length - 2].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        }
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
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

    private void initCustomerModal() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        customerModal = new ModalDialog(owner, "Thông tin khách hàng");
        JPanel body = customerModal.getBodyPanel();

        txtCode = new JTextField();
        txtName = new JTextField();
        txtPhone = new JTextField();
        txtAddress = new JTextField();
        txtEmail = new JTextField();

        body.add(new JLabel("Mã khách hàng:"));
        body.add(txtCode);
        body.add(new JLabel("Họ và tên:"));
        body.add(txtName);
        body.add(new JLabel("Số điện thoại:"));
        body.add(txtPhone);
        body.add(new JLabel("Địa chỉ:"));
        body.add(txtAddress);
        body.add(new JLabel("Email:"));
        body.add(txtEmail);

        customerModal.getSaveButton().addActionListener(e -> {
            if (saveCustomerListener != null) {
                saveCustomerListener.actionPerformed(e);
            }
            customerModal.dispose();
        });
    }

    private void openAddModal() {
        selectedCustomer = null;
        txtCode.setText("");
        txtCode.setEditable(true);
        txtName.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        txtEmail.setText("");
        customerModal.setVisible(true);
    }

    private void openEditModal(Customer c) {
        if (c == null)
            return;
        selectedCustomer = c;
        txtCode.setText(c.getCustomerCode());
        txtCode.setEditable(false);
        txtName.setText(c.getFullName());
        txtPhone.setText(c.getPhone());
        txtAddress.setText(c.getAddress());
        txtEmail.setText(c.getEmail());
        customerModal.setVisible(true);
    }

    @Override
    public void showCustomers(List<Customer> customers) {
        this.customerList = customers != null ? customers : new ArrayList<>();
        DefaultTableModel model = tablePanel.getTableModel();
        model.setRowCount(0);
        for (Customer c : customerList) {
            model.addRow(new Object[] { c.getCustomerCode(), c.getFullName(), c.getPhone() + " / " + c.getEmail(), c.getAddress() != null ? c.getAddress() : "", "", "" });
        }
        tablePanel.updateFooterCount();

        int total = customerList.size();
        cardTotal.setValue(String.valueOf(total));
        cardVip.setValue("0"); 
        cardWarnings.setValue("0");
        cardActiveRentals.setValue("Đang cập nhật");

        tierTabBar.updateTabLabels(new String[] { "Tất cả (" + total + ")", "Kim Cương (0)", "Vàng (0)", "Tiêu Chuẩn (" + total + ")", "Cảnh báo (0)" });
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
    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    @Override
    public Customer getCustomerFormData() {
        Customer c = new Customer();
        if (selectedCustomer != null) {
            c.setCustomerId(selectedCustomer.getCustomerId());
        }
        c.setCustomerCode(txtCode.getText().trim());
        c.setFullName(txtName.getText().trim());
        c.setPhone(txtPhone.getText().trim());
        c.setAddress(txtAddress.getText().trim());
        c.setEmail(txtEmail.getText().trim());
        return c;
    }

    @Override
    public void updateKPIs(int total, int vip, int active, int warning) {
        cardTotal.setValue(String.valueOf(total));
        cardVip.setValue(String.valueOf(vip));
        cardActiveRentals.setValue(String.valueOf(active));
        cardWarnings.setValue(String.valueOf(warning));
        
        tierTabBar.updateTabLabels(new String[] { 
            "Tất cả (" + total + ")", "Kim Cương (" + vip + ")", 
            "Vàng (0)", "Tiêu Chuẩn (" + (total - vip) + ")", "Cảnh báo (" + warning + ")" 
        });
    }

    @Override
    public void setCustomerFormData(Customer customer) {
        if (customer != null)
            openEditModal(customer);
    }

    @Override
    public String getSearchKeyword() {
        return filterToolbar.getSearchField().getText().trim();
    }

    @Override
    public void addSearchListener(ActionListener listener) {
        this.searchListener = listener;
        filterToolbar.getSearchField().addActionListener(listener);
    }

    @Override
    public void addSaveCustomerListener(ActionListener listener) {
        this.saveCustomerListener = listener;
    }

    @Override
    public void addDeleteCustomerListener(ActionListener listener) {
        this.deleteCustomerListener = listener;
    }

    @Override
    public void addSelectCustomerListener(Runnable callback) {
        this.selectCustomerCallback = callback;
    }

    public void refreshData() {
        if (controller != null) {
            controller.loadCustomers();
        }
    }
}
