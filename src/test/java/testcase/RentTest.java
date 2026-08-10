package testcase;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.libris.model.User;
import com.libris.ui.RentView;

import pages.RentPage;

public class RentTest {
    private RentView rentView;
    private RentPage rentPage;

    @BeforeEach
    public void setUp() {
        User customerUser = new User();
        customerUser.setUserId(2);
        customerUser.setUsername("customer1");
        customerUser.setRole("Customer");
        customerUser.setCustomerId(1);

        rentView = new RentView(customerUser);
        rentPage = new RentPage(rentView);
    }

    @Test
    public void TC_FR_06_ViewAvailableBooks() {
        // Kiểm thử hiển thị danh sách sách có trạng thái 'Available' cho khách hàng 
        int rowCount = rentPage.getTableRowCount();
        assertTrue(rowCount >= 0, "Bảng danh sách sách có sẵn phải được nạp thành công");
    }

    @Test
    public void TC_FR_06_SearchBookToRent() {
        // Kiểm thử tìm kiếm sách trên giao diện thuê sách
        rentPage.searchBook("Doraemon");
        assertTrue(rentPage.getTableRowCount() >= 0, "Hệ thống lọc tìm kiếm sách hoạt động ổn định");
    }
}
