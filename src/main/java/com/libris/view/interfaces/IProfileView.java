package com.libris.view.interfaces;

import com.libris.model.User;
import java.awt.event.ActionListener;

public interface IProfileView {
    void setUserProfile(User user);
    String getFullName();
    String getEmail();
    String getPhone();
    String getCurrentPassword();
    String getNewPassword();
    String getConfirmPassword();
    void showSuccessMessage(String message);
    void showError(String message);
    void addUpdateProfileListener(ActionListener listener);
    void addChangePasswordListener(ActionListener listener);
}
