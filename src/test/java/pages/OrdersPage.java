package pages;

import javax.swing.JComboBox;
import javax.swing.JTable;

import java.awt.Component;
import java.awt.Container;

import com.libris.ui.OrdersView;

public class OrdersPage {private OrdersView ordersView;
    private JComboBox<String> cbStatus;
    private JTable table;

    public OrdersPage(OrdersView ordersView) {
        this.ordersView = ordersView;
        findComponents(ordersView);
    }

    /** Lọc đơn hàng theo trạng thái trên JComboBox (Pending, Renting, Returned, Rejected) */
    public OrdersPage filterByStatus(String status) {
        if (cbStatus != null) {
            cbStatus.setSelectedItem(status);
            sleep(500);
        }
        return this;
    }

    public int getTableRowCount() {
        return table != null ? table.getRowCount() : 0;
    }

    private void findComponents(Container container) {
        if (container == null) return;
        for (Component c : container.getComponents()) {
            if (c instanceof JComboBox && cbStatus == null) {
                cbStatus = (JComboBox<String>) c;
            } else if (c instanceof JTable && table == null) {
                table = (JTable) c;
            } else if (c instanceof Container) {
                findComponents((Container) c);
            }
        }
    }

    private void sleep(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
    
}
