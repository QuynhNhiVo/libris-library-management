package com.libris.model;

public class Customer {
    private int customerId;
    private String customerCode;
    private String name;
    private String phone;
    private String address;
    private String email;

    public Customer() {
    }

    public Customer(int customerId, String customerCode, String name, String phone, String address, String email) {
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.email = email;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    
}
