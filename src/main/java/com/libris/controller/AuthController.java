package com.libris.controller;

import com.libris.dao.UserDAO;
import com.libris.model.User;

public class AuthController {
    private UserDAO userDao = new UserDAO();

    public AuthController() {
        this.userDao = new UserDAO();
    }

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
