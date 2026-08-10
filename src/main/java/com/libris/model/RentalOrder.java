package com.libris.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RentalOrder {
    private int orderId;
    private String orderCode;
    private int customerId;
    private String customerName;  
    private String customerCode;
    private LocalDateTime rentDate;
    private LocalDateTime expectedReturnDate;
    private LocalDateTime returnDate;
    private String orderStatus;      // 'Pending', 'Renting', 'Returned', 'Rejected'
    private int totalDeposit;
    private int totalRentalFee;
    private int lateFee;
    private int totalAmount;

    private List<RentalOrderDetail> details = new ArrayList<>();

    public RentalOrder() {
    }

    public RentalOrder(int orderId, String orderCode, int customerId, String customerName, LocalDateTime rentDate, LocalDateTime expectedReturnDate, LocalDateTime returnDate, String orderStatus, int totalDeposit, int totalRentalFee, int lateFee, int totalAmount) {
        this.orderId = orderId;
        this.orderCode = orderCode;
        this.customerId = customerId;
        this.customerName = customerName;
        this.rentDate = rentDate;
        this.expectedReturnDate = expectedReturnDate;
        this.returnDate = returnDate;
        this.orderStatus = orderStatus;
        this.totalDeposit = totalDeposit;
        this.totalRentalFee = totalRentalFee;
        this.lateFee = lateFee;
        this.totalAmount = totalAmount;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerCode(){
        return customerCode;
    }

    public void setCustomerCode(String customerCode){
        this.customerCode = customerCode;
    }

    public LocalDateTime getRentDate() {
        return rentDate;
    }

    public void setRentDate(LocalDateTime rentDate) {
        this.rentDate = rentDate;
    }

    public LocalDateTime getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(LocalDateTime expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getTotalDeposit() {
        return totalDeposit;
    }

    public void setTotalDeposit(int totalDeposit) {
        this.totalDeposit = totalDeposit;
    }

    public int getTotalRentalFee() {
        return totalRentalFee;
    }

    public void setTotalRentalFee(int totalRentalFee) {
        this.totalRentalFee = totalRentalFee;
    }

    public int getLateFee() {
        return lateFee;
    }

    public void setLateFee(int lateFee) {
        this.lateFee = lateFee;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<RentalOrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<RentalOrderDetail> details) {
        this.details = details;
    }

    
}
