package pages;

import javax.swing.JTable;

import java.awt.Component;
import java.awt.Container;

import com.libris.ui.MyRentalsView;

public class MyRentalsPage {
    private MyRentalsView myRentalsView;
    private JTable table;

    public MyRentalsPage(MyRentalsView myRentalsView) {
        this.myRentalsView = myRentalsView;
        findComponents(myRentalsView);
    }

    /** Lấy số lượng đơn thuê cá nhân hiển thị trên bảng */
    public int getTableRowCount() {
        if (table != null) {
            return table.getRowCount();
        }
        return 0;
    }

    private void findComponents(Container container) {
        if (container == null) return;
        for (Component c : container.getComponents()) {
            if (c instanceof JTable && table == null) {
                table = (JTable) c;
            } else if (c instanceof Container) {
                findComponents((Container) c);
            }
        }
    }
}
