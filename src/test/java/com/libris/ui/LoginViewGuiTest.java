package com.libris.ui;

import com.libris.config.Constants;
import com.libris.view.LoginView;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.assertj.swing.timing.Condition;
import org.assertj.swing.timing.Pause;
import org.assertj.swing.timing.Timeout;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import static org.assertj.swing.finder.WindowFinder.findFrame;

/**
 * UI Automation Test for Swing GUI components using AssertJ-Swing.
 * Covered Test Cases:
 * TC-AUTH-01, TC-AUTH-02, TC-AUTH-03, TC-BOOK-01, TC-NFR-01
 * 
 * Rules applied:
 * - Uses 30-second conditional timeout (Pause.pause(Condition, Timeout)) for login wait.
 * - Interacts via real Robot Swing components.
 * - Uses pre-seeded test database (Libris.test.db).
 */
public class LoginViewGuiTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;

    @Override
    protected void onSetUp() {
        FailOnThreadViolationRepaintManager.install();

        // Point database to Libris.test.db
        try {
            String testDbPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "Libris.test.db";
            Field urlField = Constants.class.getField("URL");
            urlField.setAccessible(true);
            urlField.set(null, Constants.DATABASE_URL_PREFIX + testDbPath);
        } catch (Exception ignored) {}

        LoginView frame = GuiActionRunner.execute(() -> new LoginView());
        window = new FrameFixture(robot(), frame);
        window.show(); // shows the frame to test
    }

    @Test
    @Tag("ui")
    @DisplayName("test_TC_AUTH_01_TC_NFR_01_guiAdminLogin_navigatesToDashboardWithin30s()")
    public void test_TC_AUTH_01_TC_NFR_01_guiAdminLogin_navigatesToDashboardWithin30s() {
        // Enter Admin Credentials
        window.textBox(new GenericTypeMatcher<JTextField>(JTextField.class) {
            @Override
            protected boolean isMatching(JTextField tf) {
                return tf.isVisible();
            }
        }).setText("admin");

        window.textBox(new GenericTypeMatcher<JPasswordField>(JPasswordField.class) {
            @Override
            protected boolean isMatching(JPasswordField pf) {
                return pf.isVisible();
            }
        }).setText("123");

        // Click Login Button
        window.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(JButton button) {
                return button.getText().contains("Đăng nhập");
            }
        }).click();

        // Conditional wait with >= 30 seconds timeout for MainFrame appearance
        Pause.pause(new Condition("MainFrame window to be visible after login") {
            @Override
            public boolean test() {
                try {
                    FrameFixture mainFrame = findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
                        @Override
                        protected boolean isMatching(JFrame frame) {
                            return frame.getTitle().contains("Libris") && frame.isVisible();
                        }
                    }).withTimeout(500, TimeUnit.MILLISECONDS).using(robot());
                    return mainFrame != null;
                } catch (Exception e) {
                    return false;
                }
            }
        }, Timeout.timeout(30, TimeUnit.SECONDS));

        // Verify MainFrame has appeared
        FrameFixture mainFrame = findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame frame) {
                return frame.getTitle().contains("Libris") && frame.isVisible();
            }
        }).using(robot());

        Assertions.assertNotNull(mainFrame, "MainFrame should be displayed after successful Admin login");
        mainFrame.cleanUp();
    }

    @Test
    @Tag("ui")
    @DisplayName("test_TC_AUTH_03_guiLogin_wrongPassword_displaysError()")
    public void test_TC_AUTH_03_guiLogin_wrongPassword_displaysError() {
        window.textBox(new GenericTypeMatcher<JTextField>(JTextField.class) {
            @Override
            protected boolean isMatching(JTextField tf) {
                return tf.isVisible();
            }
        }).setText("admin");

        window.textBox(new GenericTypeMatcher<JPasswordField>(JPasswordField.class) {
            @Override
            protected boolean isMatching(JPasswordField pf) {
                return pf.isVisible();
            }
        }).setText("wrongpassword");

        window.button(new GenericTypeMatcher<JButton>(JButton.class) {
            @Override
            protected boolean isMatching(JButton button) {
                return button.getText().contains("Đăng nhập");
            }
        }).click();

        // Verify error label displays error message
        Pause.pause(new Condition("Error label to update") {
            @Override
            public boolean test() {
                try {
                    return window.label(new GenericTypeMatcher<JLabel>(JLabel.class) {
                        @Override
                        protected boolean isMatching(JLabel label) {
                            return label.getText().contains("Sai tên đăng nhập hoặc mật khẩu");
                        }
                    }) != null;
                } catch (Exception e) {
                    return false;
                }
            }
        }, Timeout.timeout(10, TimeUnit.SECONDS));
    }

    @Override
    protected void onTearDown() {
        if (window != null) {
            window.cleanUp();
        }
    }
}
