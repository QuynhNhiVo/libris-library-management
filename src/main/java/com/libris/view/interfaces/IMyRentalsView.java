package com.libris.view.interfaces;

import com.libris.model.RentalOrder;
import java.awt.event.ActionListener;
import java.util.List;

public interface IMyRentalsView {
    void showMyRentals(List<RentalOrder> rentals);
    void showError(String message);
    void showSuccessMessage(String message);
    RentalOrder getSelectedRentalOrder();
    void addReturnBookListener(ActionListener listener);
}
