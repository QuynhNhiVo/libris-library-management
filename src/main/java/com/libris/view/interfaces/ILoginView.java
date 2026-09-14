package com.libris.view.interfaces;

import com.libris.model.User;
import java.awt.event.ActionListener;

public interface ILoginView {
    String getUsername();
    String getPassword();
    void setErrorMessage(String message);
    void setLoading(boolean loading);
    void addLoginListener(ActionListener listener);
    void closeView();
    void openMainFrame(User user);
}
