package pages;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import com.libris.ui.LoginView;

public class LoginPage {

    private LoginView loginView;
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnLogin;

    public LoginPage run() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                loginView = new LoginView();
                loginView.setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }

        if (loginView != null) {
            findComponents(loginView);
        }
        
        return this;
    }

    public LoginPage login(String username, String password) {
        assertNotNull(tfUsername, "Không tìm thấy trường nhập Username");
        assertNotNull(pfPassword, "Không tìm thấy trường nhập Password");
        assertNotNull(btnLogin, "Không tìm thấy nút Đăng nhập");

        tfUsername.setText(username);
        pfPassword.setText(password);
        btnLogin.doClick();

        sleep(600);
        return this;
    }

    public LoginPage waitForLoginSuccess(int timeoutMillis) {
        long startTime = System.currentTimeMillis();
        while (loginView != null && loginView.isVisible() && (System.currentTimeMillis() - startTime) < timeoutMillis) {
            sleep(200);
        }
        return this;
    }

    public String getErrorMessage() {
        return findMessageLabelText(loginView);
    }

    public boolean isLoginClosedOrRedirected() {
        if (loginView == null) return true;
        return !loginView.isVisible();
    }

    private String findMessageLabelText(Container container) {
        if (container == null) return "";
        for (Component c : container.getComponents()) {
            if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                String text = lbl.getText();
                if (text != null && !text.trim().isEmpty() && 
                   (text.contains("Vui lòng") || text.contains("Sai") || text.contains("Lỗi"))) {
                    return text.trim();
                }
            }
            if (c instanceof Container) {
                String found = findMessageLabelText((Container) c);
                if (!found.isEmpty()) return found;
            }
        }
        return "";
    }

    private void findComponents(Container container) {
        if (container == null) return;

        for (Component c : container.getComponents()) {
            if (c instanceof JPasswordField) {
                if (pfPassword == null) pfPassword = (JPasswordField) c;
            } else if (c instanceof JTextField) {
                if (tfUsername == null) tfUsername = (JTextField) c;
            } else if (c instanceof JButton) {
                JButton btn = (JButton) c;
                String text = btn.getText();
                if (text != null && (text.contains("Đăng nhập") || text.contains("Login"))) {
                    btnLogin = btn;
                }
            }
            
            if (c instanceof Container) {
                findComponents((Container) c);
            }
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError(message);
        }
    }
}
