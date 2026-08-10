package pages;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JTable;
import javax.swing.JTextField;

import com.libris.ui.BooksView;

public class BooksPage {
    private BooksView booksView;
    private JTextField tfSearch;
    private JTable table;

    public BooksPage(BooksView booksView) {
        this.booksView = booksView;
        findComponents(booksView);
    }

    /** Tìm kiếm sách trên giao diện quản lý sách */
    public BooksPage searchBook(String keyword) {
        if (tfSearch != null) {
            tfSearch.setText(keyword);
            sleep(500);
        }
        return this;
    }

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
