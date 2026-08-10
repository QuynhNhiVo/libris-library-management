package pages;

import javax.swing.JTable;
import javax.swing.JTextField;

import java.awt.Component;
import java.awt.Container;

import com.libris.ui.CustomersView;

public class CustomersPage {
    private CustomersView customersView;
    private JTextField tfSearch;
    private JTable table;

    public CustomersPage(CustomersView customersView) {
        this.customersView = customersView;
        findComponents(customersView);
    }

    public CustomersPage searchCustomer(String keyword) {
        if (tfSearch != null) {
            tfSearch.setText(keyword);
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
            if (c instanceof JTextField && tfSearch == null) {
                tfSearch = (JTextField) c;
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
