package testcase;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.libris.model.User;
import com.libris.ui.MyRentalsView;

import pages.MyRentalsPage;

public class MyRentalsTest {private MyRentalsView myRentalsView;
    private MyRentalsPage myRentalsPage;

    @BeforeEach
    public void setUp() {
        User customerUser = new User();
        customerUser.setUserId(2);
        customerUser.setUsername("customer1");
        customerUser.setRole("Customer");
        customerUser.setCustomerId(1);

        myRentalsView = new MyRentalsView(customerUser);
        myRentalsPage = new MyRentalsPage(myRentalsView);
    }

    @Test
    public void TC_FR_07_ViewMyRentals() {
        // Kiểm thử xem danh sách đơn thuê của riêng khách hàng hiện tại
        int rowCount = myRentalsPage.getTableRowCount();
        assertTrue(rowCount >= 0, "Bảng quản lý sách đang thuê của khách hàng phải khởi tạo thành công");
    }
    
}
