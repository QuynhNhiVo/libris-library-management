package testcase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pages.LoginPage;

public class LoginTest {
    private LoginPage loginPage;

    @BeforeEach
    public void setUp() {
        loginPage = new LoginPage();
    }

    @Test
    public void TC_FR_01() {
        // 1. Case đăng nhập thành công
        loginPage.run()
                 .login("admin", "123")
                 .waitForLoginSuccess(15000); 

        assertTrue(loginPage.isLoginClosedOrRedirected(), 
                   "Đăng nhập thành công, cửa sổ LoginView phải đóng để chuyển sang MainFrame");
    }

    @Test
    public void TC_FR_01_EmptyFields() {
        // 2. Case để trống thông tin
        loginPage.run()
                 .login("", "");

        String errorMsg = loginPage.getErrorMessage();
        assertEquals("Vui lòng nhập đủ thông tin!", errorMsg, 
                     "Hệ thống phải hiển thị thông báo yêu cầu nhập đủ thông tin");
        assertTrue(loginPage.isLoginClosedOrRedirected() == false, 
                   "Để trống thông tin thì cửa sổ Login không được đóng");
    }

    @Test
    public void TC_FR_01_WrongCredentials() {
        // 3. Case sai tên đăng nhập hoặc mật khẩu
        loginPage.run()
                 .login("admin", "wrong_password_123");

        assertFalse(loginPage.isLoginClosedOrRedirected(), 
                    "Đăng nhập sai thông tin thì cửa sổ LoginView phải giữ nguyên (không được đóng)");
    }
}
