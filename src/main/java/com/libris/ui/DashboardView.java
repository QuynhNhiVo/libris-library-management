package com.libris.ui;

import com.libris.controller.BookController;
import com.libris.controller.CustomerController;
import com.libris.controller.RentalOrderController;
import com.libris.controller.ReportController;
import com.libris.model.Book;
import com.libris.model.Customer;
import com.libris.model.RentalOrder;
import com.libris.model.ReportStat;
import com.libris.utils.IconUtils;
import com.libris.config.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DashboardView extends JPanel {
    private BookController bookController;
    private CustomerController customerController;
    private RentalOrderController orderController;
    private ReportController reportController;
    private Consumer<String> navigator;

    private JLabel[] valueLabels;
    private DefaultTableModel recentOrdersModel;
    private JPanel topBooksContainer;

    public DashboardView(Consumer<String> navigator) {
        this.navigator = navigator;
        bookController = new BookController();
        customerController = new CustomerController();
        orderController = new RentalOrderController();
        reportController = new ReportController();

        initComponents();
    }

    public void refreshData() {
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        setBackground(new Color(245, 247, 250));

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);

        // --- PHẦN 1: HEADER & 4 THẺ THỐNG KÊ (CARDS) ---
        JPanel topWrapper = new JPanel(new BorderLayout(0, 15));
        topWrapper.setOpaque(false);
        topWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel lblTitle = new JLabel("Tổng quan hệ thống");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(30, 41, 59));
        topWrapper.add(lblTitle, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);

        String[][] stats = {
            { Constants.IC_TOTAL_BOOKS, "Tổng số sách" },
            { Constants.IC_AVAILABLE_BOOKS, "Sách có sẵn" },
            { Constants.IC_RENTED_BOOKS_STATS, "Sách đang thuê" },
            { Constants.IC_PROFILE, Constants.UI_LABEL_CUSTOMERS }
        };

        valueLabels = new JLabel[4];
        for (int i = 0; i < stats.length; i++) {
            JPanel card = createStatCard(stats[i][0], stats[i][1], "0");
            statsPanel.add(card);
            JPanel textPanel = (JPanel) card.getComponent(1);
            valueLabels[i] = (JLabel) textPanel.getComponent(0);
        }
        topWrapper.add(statsPanel, BorderLayout.CENTER);
        mainContent.add(topWrapper);
        mainContent.add(Box.createVerticalStrut(20));

        // --- PHẦN 2: BỐ CỤC 2 BẢNG XẾP DỌC (1 TRÊN - 1 DƯỚI) ---
        
        // 1. BẢNG TRÊN: ĐƠN THUÊ GẦN ĐÂY
        JPanel pnlRecentOrders = createWidgetPanel("Đơn thuê gần đây", true);
        pnlRecentOrders.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        pnlRecentOrders.setPreferredSize(new Dimension(0, 260));

        recentOrdersModel = new DefaultTableModel(new String[] { "Mã đơn", "Khách hàng", "Ngày thuê", "Trạng thái" }, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tableOrders = new JTable(recentOrdersModel);
        tableOrders.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableOrders.setRowHeight(38);
        tableOrders.setShowVerticalLines(false);
        tableOrders.setGridColor(new Color(238, 238, 238));
        tableOrders.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableOrders.getTableHeader().setBackground(new Color(248, 250, 252));
        tableOrders.getTableHeader().setPreferredSize(new Dimension(0, 40));

        JScrollPane scrollOrders = new JScrollPane(tableOrders);
        scrollOrders.setBorder(BorderFactory.createEmptyBorder());
        scrollOrders.getViewport().setBackground(Color.WHITE);
        pnlRecentOrders.add(scrollOrders, BorderLayout.CENTER);
        
        mainContent.add(pnlRecentOrders);
        mainContent.add(Box.createVerticalStrut(20));

        // 2. BẢNG DƯỚI: SÁCH ĐƯỢC THUÊ NHIỀU NHẤT
        JPanel pnlTopBooks = createWidgetPanel("Sách được thuê nhiều nhất", false);
        pnlTopBooks.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        pnlTopBooks.setPreferredSize(new Dimension(0, 300));

        topBooksContainer = new JPanel();
        topBooksContainer.setLayout(new BoxLayout(topBooksContainer, BoxLayout.Y_AXIS));
        topBooksContainer.setBackground(Color.WHITE);

        JScrollPane scrollBooks = new JScrollPane(topBooksContainer);
        scrollBooks.setBorder(BorderFactory.createEmptyBorder());
        scrollBooks.getViewport().setBackground(Color.WHITE);
        pnlTopBooks.add(scrollBooks, BorderLayout.CENTER);
        
        mainContent.add(pnlTopBooks);

        // JScrollPane 
        JScrollPane mainScrollPane = new JScrollPane(mainContent);
        mainScrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainScrollPane.getViewport().setBackground(new Color(245, 247, 250));
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(mainScrollPane, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String iconPath, String label, String value) {
        JPanel card = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.setColor(new Color(226, 232, 240));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblIcon = new JLabel();
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        lblIcon.setIcon(IconUtils.loadIconForComponent(iconPath, lblIcon));
        lblIcon.setForeground(new Color(37, 99, 235));
        card.add(lblIcon, BorderLayout.WEST);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(new Color(15, 23, 42));
        textPanel.add(lblValue);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLabel.setForeground(new Color(100, 116, 139));
        textPanel.add(lblLabel);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createWidgetPanel(String title, boolean hasViewAllButton) {
        JPanel panel = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(226, 232, 240));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };

        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(30, 41, 59));
        header.add(lblTitle, BorderLayout.WEST);

        if (hasViewAllButton) {
            JButton btnViewAll = new JButton("Xem tất cả ➔");
            btnViewAll.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnViewAll.setForeground(new Color(37, 99, 235));
            btnCustomStyle(btnViewAll);
            btnViewAll.addActionListener(e -> {
                if (navigator != null) navigator.accept("orders");
            });
            header.add(btnViewAll, BorderLayout.EAST);
        }

        panel.add(header, BorderLayout.NORTH);
        return panel;
    }

    private void btnCustomStyle(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel createTopBookRow(int rank, String title, String author, int rentCount) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        row.setPreferredSize(new Dimension(0, 50));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JLabel lblRank = new JLabel("#" + rank);
        lblRank.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblRank.setForeground(rank <= 3 ? new Color(37, 99, 235) : new Color(148, 163, 184));
        lblRank.setPreferredSize(new Dimension(40, 30));
        lblRank.setHorizontalAlignment(SwingConstants.CENTER);
        row.add(lblRank, BorderLayout.WEST);

        JPanel col1 = new JPanel(new GridLayout(2, 1, 0, 0));
        col1.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(30, 41, 59));

        JLabel lblAuthor = new JLabel("Tác giả: " + author);
        lblAuthor.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAuthor.setForeground(new Color(100, 116, 139));

        col1.add(lblTitle);
        col1.add(lblAuthor);
        row.add(col1, BorderLayout.CENTER);

        JPanel col2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        col2.setOpaque(false);
        JLabel lblCount = new JLabel(rentCount + " lượt thuê");
        lblCount.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCount.setForeground(new Color(16, 185, 129));
        col2.add(lblCount);
        row.add(col2, BorderLayout.EAST);

        return row;
    }

    private void loadData() {
        int totalBooks = 0, availableBooks = 0, rentedBooks = 0, totalCustomers = 0;
        List<Book> allBooks = null;

        // 1. Đồng bộ dữ liệu books
        try {
            allBooks = bookController.getAllBooks();
            if (allBooks != null) {
                totalBooks = allBooks.size();
                for (Book b : allBooks) {
                    if (Constants.BOOK_STATUS_AVAILABLE.equalsIgnoreCase(b.getBookStatus())) {
                        availableBooks++;
                    } else if (Constants.BOOK_STATUS_RENTED.equalsIgnoreCase(b.getBookStatus())) {
                        rentedBooks++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Đồng bộ dữ liệu Khách hàng
        try {
            List<Customer> customers = customerController.getAllCustomers();
            if (customers != null) {
                totalCustomers = customers.size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] values = {
            String.valueOf(totalBooks), 
            String.valueOf(availableBooks),
            String.valueOf(rentedBooks), 
            String.valueOf(totalCustomers)
        };
        for (int i = 0; i < valueLabels.length; i++) {
            valueLabels[i].setText(values[i]);
        }

        // 3. Đồng bộ danh sách Đơn thuê 
        recentOrdersModel.setRowCount(0);
        try {
            List<RentalOrder> orders = orderController.getAllOrders();
            if (orders != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_PATTERN_DMY);
                int count = 0;
                for (RentalOrder order : orders) {
                    if (count >= 5) break; 
                    recentOrdersModel.addRow(new Object[] {
                        order.getOrderCode(), 
                        order.getCustomerName(),
                        order.getRentDate() != null ? order.getRentDate().format(formatter) : "",
                        order.getOrderStatus()
                    });
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4. Đồng bộ danh sách Sách được thuê 
        topBooksContainer.removeAll();
        try {
            List<ReportStat> topBooks = reportController.getTopBooks(4);
            if (topBooks != null && allBooks != null) {
                Map<String, String> authorMap = allBooks.stream()
                        .collect(Collectors.toMap(Book::getTitle, Book::getAuthor, (a, b) -> a));

                int rank = 1;
                for (ReportStat stat : topBooks) {
                    String title = stat.getLabel();
                    String author = authorMap.getOrDefault(title, "Đang cập nhật");
                    topBooksContainer.add(createTopBookRow(rank, title, author, stat.getValue()));
                    rank++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        topBooksContainer.revalidate();
        topBooksContainer.repaint();
    }
}