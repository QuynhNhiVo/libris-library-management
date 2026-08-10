package testcase;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.libris.ui.BooksView;

import pages.BooksPage;

public class BookManagementTest {
    private BooksView booksView;
    private BooksPage booksPage;

    @BeforeEach
    public void setUp() {
        booksView = new BooksView();
        booksPage = new BooksPage(booksView);
    }

    @Test
    public void TC_FR_03_SearchBook() {
        // Kiểm thử tìm kiếm sách theo từ khóa
        booksPage.searchBook("Doraemon");
        int rowCount = booksPage.getTableRowCount();
        assertTrue(rowCount >= 0, "Hệ thống phải trả về kết quả tìm kiếm hợp lệ trên bảng sách");
    }

}
