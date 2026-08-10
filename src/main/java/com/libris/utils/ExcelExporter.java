package com.libris.utils;

import com.libris.model.Book;
import com.libris.model.Customer;
import com.libris.model.RentalOrder;
import com.libris.model.ReportStat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelExporter {

    public static boolean exportBooks(List<Book> books, File file) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách sách");

            Row header = sheet.createRow(0);
            String[] headers = {"Mã sách", "Tên sách", "Tác giả", "Thể loại", "NXB", "Năm XB", "Trạng thái", "Giá thuê", "Tiền cọc"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            int rowNum = 1;
            for (Book book : books) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(book.getBookCode());
                row.createCell(1).setCellValue(book.getTitle());
                row.createCell(2).setCellValue(book.getAuthor());
                row.createCell(3).setCellValue(book.getCategory());
                row.createCell(4).setCellValue(book.getPublisher());
                row.createCell(5).setCellValue(book.getPublishYear());
                row.createCell(6).setCellValue(book.getBookStatus());
                row.createCell(7).setCellValue(book.getRentalPrice());
                row.createCell(8).setCellValue(book.getDepositPrice());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean exportCustomers(List<Customer> customers, File file) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách khách hàng");

            Row header = sheet.createRow(0);
            String[] headers = {"Mã KH", "Họ tên", "Số điện thoại", "Email", "Địa chỉ"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            int rowNum = 1;
            for (Customer c : customers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(c.getCustomerCode());
                row.createCell(1).setCellValue(c.getName());
                row.createCell(2).setCellValue(c.getPhone());
                row.createCell(3).setCellValue(c.getEmail());
                row.createCell(4).setCellValue(c.getAddress());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean exportOrders(List<RentalOrder> orders, File file) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách đơn thuê");

            Row header = sheet.createRow(0);
            String[] headers = {"Mã đơn", "Mã KH", "Khách hàng", "Ngày thuê", "Hạn trả", "Trạng thái", "Tổng tiền"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            int rowNum = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (RentalOrder order : orders) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(order.getOrderCode());
                row.createCell(1).setCellValue(order.getCustomerCode() != null ? order.getCustomerCode() : "");
                row.createCell(2).setCellValue(order.getCustomerName());
                row.createCell(3).setCellValue(order.getRentDate() != null ? order.getRentDate().format(formatter) : "");
                row.createCell(4).setCellValue(order.getExpectedReturnDate() != null ? order.getExpectedReturnDate().format(formatter) : "");
                row.createCell(5).setCellValue(order.getOrderStatus());
                row.createCell(6).setCellValue(order.getTotalAmount());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean exportRevenueReport(List<ReportStat> monthlyRevenue, File file) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo cáo doanh thu");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Tháng");
            header.createCell(1).setCellValue("Doanh thu (VNĐ)");
            header.getCell(0).setCellStyle(createHeaderStyle(workbook));
            header.getCell(1).setCellStyle(createHeaderStyle(workbook));

            int rowNum = 1;
            for (ReportStat stat : monthlyRevenue) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(stat.getLabel());
                row.createCell(1).setCellValue(stat.getDoubleValue());
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}