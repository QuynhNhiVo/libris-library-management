package com.libris.config;

import com.libris.helpers.PropertiesHelpers;

public class Constants {
    public static final String DATABASE_NAME = "Libris";
    public static final String DATABASE_URL_PREFIX = "jdbc:sqlite:";
    public static final String DATABASE_FILE_EXTENSION = ".db";
    public static final String URL = DATABASE_URL_PREFIX + DATABASE_NAME + DATABASE_FILE_EXTENSION;

    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_CUSTOMER = "Customer";

    public static final String BOOK_STATUS_AVAILABLE = "Available";
    public static final String BOOK_STATUS_RENTED = "Rented";
    public static final String BOOK_STATUS_PENDING = "Pending";

    public static final String ORDER_STATUS_PENDING = "Pending";
    public static final String ORDER_STATUS_RENTING = "Renting";
    public static final String ORDER_STATUS_RETURNED = "Returned";
    public static final String ORDER_STATUS_REJECTED = "Rejected";

    public static final String UI_LABEL_DASHBOARD = "Tổng quan";
    public static final String UI_LABEL_BOOKS = "Quản lý sách";
    public static final String UI_LABEL_CUSTOMERS = "Khách hàng";
    public static final String UI_LABEL_ORDERS = "Đơn thuê";
    public static final String UI_LABEL_REPORTS = "Báo cáo";
    public static final String UI_LABEL_RENT = "Thuê sách";
    public static final String UI_LABEL_MY_RENTALS = "Sách đang thuê";
    public static final String UI_LABEL_PROFILE = "Hồ sơ";
    public static final String UI_LABEL_LOGOUT = "Đăng xuất";
    public static final String UI_LABEL_ADMIN_ROLE = "Quản trị";
    public static final String UI_LABEL_CUSTOMER_ROLE = "Khách hàng";

    public static final String DATE_PATTERN_DMY = "dd/MM/yyyy";
    public static final String DATE_PATTERN_SQL = "yyyy-MM-dd HH:mm:ss";

    public static final String IC_BOOKS = PropertiesHelpers.getValue("icon.book_open.url");

    public static final String IC_LOGOUT = PropertiesHelpers.getValue("icon.logout.url");

    public static final String IC_DASHBOARD = PropertiesHelpers.getValue("icon.dashboard.url");

    public static final String IC_MBOOK = PropertiesHelpers.getValue("icon.mbook.url");

    public static final String IC_USERS = PropertiesHelpers.getValue("icon.users.url");

    public static final String IC_RENTALS = PropertiesHelpers.getValue("icon.rentals.url");

    public static final String IC_REPORTS = PropertiesHelpers.getValue("icon.reports.url");

    public static final String IC_RENT_BOOK = PropertiesHelpers.getValue("icon.rent.book.url");

    public static final String IC_RENTED_BOOKS = PropertiesHelpers.getValue("icon.rented.books.url");

    public static final String IC_PROFILE = PropertiesHelpers.getValue("icon.profile.url");

    public static final String IC_TOTAL_BOOKS = PropertiesHelpers.getValue("icon.stats.total.books.url");

    public static final String IC_AVAILABLE_BOOKS = PropertiesHelpers.getValue("icon.stats.available.books.url");

    public static final String IC_RENTED_BOOKS_STATS = PropertiesHelpers.getValue("icon.stats.rented.books.url");

    public static final String IC_EDIT = PropertiesHelpers.getValue("icon.edit.url");

    public static final String IC_DELETE = PropertiesHelpers.getValue("icon.delete.url");

    public static final String IC_SEARCH = PropertiesHelpers.getValue("icon.search.url");

    public static final String IC_REFRESH = PropertiesHelpers.getValue("icon.refresh.url");

    public static final String IC_ADD = PropertiesHelpers.getValue("icon.add.url");

    public static final String IC_APPROVE = PropertiesHelpers.getValue("icon.approve.url");

    public static final String IC_REJECT = PropertiesHelpers.getValue("icon.reject.url");

    public static final String IC_RENT = PropertiesHelpers.getValue("icon.rent.url");

    public static final String IC_RORATE = PropertiesHelpers.getValue("icon.rorate.url");

    public static final String IC_EMAIL = PropertiesHelpers.getValue("icon.email.url");

    public static final String IC_PHONE = PropertiesHelpers.getValue("icon.phone.url");

    public static final String IC_SHIELD = PropertiesHelpers.getValue("icon.shield.url");

    public static final String IC_USERC = PropertiesHelpers.getValue("icon.user.circle.url");

    public static final String IC_KEY = PropertiesHelpers.getValue("icon.key.round.url");

    
}
