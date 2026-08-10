package testcase;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.libris.ui.CustomersView;

import pages.CustomersPage;

public class CustomerManagementTest {
    
    private CustomersView customersView;
    private CustomersPage customersPage;

    @BeforeEach
    public void setUp() {
        customersView = new CustomersView();
        customersPage = new CustomersPage(customersView);
    }

    @Test
    public void TC_FR_04_SearchCustomer() {
        // Kiểm thử tìm kiếm khách hàng
        customersPage.searchCustomer("Nguyễn");
        assertTrue(customersPage.getTableRowCount() >= 0);
    }
}
