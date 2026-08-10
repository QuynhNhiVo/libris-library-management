package pages;

import javax.swing.JTable;
import javax.swing.JTextField;

import java.awt.Component;
import java.awt.Container;
import com.libris.ui.RentView;

public class RentPage {
    private RentView rentView;
    private JTextField tfSearch;
    private JTable table;

    public RentPage(RentView rentView) {
        this.rentView = rentView;
        findComponents(rentView);
    }

    /** Tìm kiếm sách có sẵn trên trang thuê sách */
    public RentPage searchBook(String keyword) {
        if (tfSearch != null) {
            tfSearch.setText(keyword);
            sleep(500);
        }
        return this;
    }

    /** Lấy số lượng dòng sách đang hiển thị trên bảng */
    public int getTableRowCount() {
        if (table != null) {
            return table.getRowCount();
        }
        return 0;
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
