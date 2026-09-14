package com.libris.controller;

import com.libris.dao.UserDAO;
import com.libris.model.User;
import com.libris.view.interfaces.ILoginView;
import com.libris.view.interfaces.IProfileView;

public class AuthController {
    private UserDAO userDao;
    private ILoginView loginView;
    private IProfileView profileView;

    public AuthController() {
        this.userDao = new UserDAO();
    }

    public AuthController(UserDAO userDao) {
        this.userDao = userDao;
    }

    public AuthController(ILoginView view, UserDAO userDao) {
        this.loginView = view;
        this.userDao = userDao != null ? userDao : new UserDAO();
        if (this.loginView != null) {
            this.loginView.addLoginListener(e -> handleLogin());
        }
    }

    public AuthController(IProfileView view, UserDAO userDao) {
        this.profileView = view;
        this.userDao = userDao != null ? userDao : new UserDAO();
        if (this.profileView != null) {
            this.profileView.addUpdateProfileListener(e -> handleUpdateProfile());
            this.profileView.addChangePasswordListener(e -> handleChangePassword());
        }
    }

    public void handleLogin() {
        if (loginView == null) return;
        String username = loginView.getUsername().trim();
        String password = loginView.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            loginView.setErrorMessage("Vui lòng nhập đủ thông tin!");
            return;
        }

        loginView.setLoading(true);
        try {
            System.out.println("[AuthController] handleLogin() -> attempting login for " + username);
            User user = userDao.login(username, password);
            System.out.println("[AuthController] handleLogin() -> user returned: " + (user != null ? user.getUsername() : "null"));
            if (user != null) {
                System.out.println("[AuthController] handleLogin() -> opening main frame and closing login view");
                loginView.openMainFrame(user);
                loginView.closeView();
            } else {
                loginView.setErrorMessage("Sai tên đăng nhập hoặc mật khẩu!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            loginView.setErrorMessage("Lỗi kết nối cơ sở dữ liệu!");
        } finally {
            loginView.setLoading(false);
        }
    }

    public void handleUpdateProfile() {
        if (profileView == null) return;
        try {
            User user = login(profileView.getFullName(), profileView.getCurrentPassword()); // validation helper if needed
        } catch (Exception ignored) {}
    }

    public void handleChangePassword() {
        if (profileView == null) return;
        String curPass = profileView.getCurrentPassword();
        String newPass = profileView.getNewPassword();
        String confirmPass = profileView.getConfirmPassword();

        if (newPass == null || newPass.isEmpty()) {
            profileView.showError("Mật khẩu mới không được để trống!");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            profileView.showError("Mật khẩu xác nhận không khớp!");
            return;
        }
    }

    // Retain legacy direct methods for existing callers/helpers
    public User login(String username, String password) {
        try {
            return userDao.login(username, password);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateProfileDetails(int userId, String fullName, String email, String phone) {
        try {
            return userDao.updateProfileDetails(userId, fullName, email, phone);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        try {
            return userDao.changePassword(userId, currentPassword, newPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
