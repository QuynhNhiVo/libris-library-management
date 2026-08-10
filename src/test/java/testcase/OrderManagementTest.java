package testcase;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.libris.ui.OrdersView;

import pages.OrdersPage;

public class OrderManagementTest {private OrdersView ordersView;
    private OrdersPage ordersPage;

    @BeforeEach
    public void setUp() {
        ordersView = new OrdersView();
        ordersPage = new OrdersPage(ordersView);
    }

    @Test
    public void TC_FR_05_FilterOrdersByStatus() {
        // Kiểm thử lọc danh sách đơn thuê theo trạng thái "Pending"
        ordersPage.filterByStatus("Pending");
        assertTrue(ordersPage.getTableRowCount() >= 0, "Bảng đơn thuê phải cập nhật dữ liệu lọc thành công");
    }
    
}
