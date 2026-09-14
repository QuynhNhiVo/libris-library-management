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

    public static boolean exportFullReport(File file, int year, List<ReportStat> rev, List<ReportStat> books, List<ReportStat> cats, List<ReportStat> custs, List<RentalOrder> overdue) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Sheet 1: Doanh thu
            Sheet s1 = workbook.createSheet("Doanh thu " + year);
            Row h1 = s1.createRow(0); h1.createCell(0).setCellValue("Tháng"); h1.createCell(1).setCellValue("Doanh thu (VNĐ)"); h1.createCell(2).setCellValue("Số đơn mượn");
            int r = 1; for (ReportStat s : rev) { Row row = s1.createRow(r++); row.createCell(0).setCellValue(s.getLabel()); row.createCell(1).setCellValue(s.getDoubleValue()); row.createCell(2).setCellValue(s.getValue()); }
            
            // Sheet 2: Top Sách
            Sheet s2 = workbook.createSheet("Top Sách");
            Row h2 = s2.createRow(0); h2.createCell(0).setCellValue("Tên sách"); h2.createCell(1).setCellValue("Lượt mượn"); h2.createCell(2).setCellValue("Doanh thu (VNĐ)");
            r = 1; for (ReportStat s : books) { Row row = s2.createRow(r++); row.createCell(0).setCellValue(s.getLabel()); row.createCell(1).setCellValue(s.getValue()); row.createCell(2).setCellValue(s.getDoubleValue()); }
            
            // Sheet 3: Thể loại
            Sheet s3 = workbook.createSheet("Thể loại");
            Row h3 = s3.createRow(0); h3.createCell(0).setCellValue("Thể loại"); h3.createCell(1).setCellValue("Số đầu sách"); h3.createCell(2).setCellValue("Tổng lượt mượn");
            r = 1; for (ReportStat s : cats) { Row row = s3.createRow(r++); row.createCell(0).setCellValue(s.getLabel()); row.createCell(1).setCellValue(s.getValue()); row.createCell(2).setCellValue(s.getDoubleValue()); }
            
            // Sheet 4: Khách hàng
            Sheet s4 = workbook.createSheet("Khách hàng");
            Row h4 = s4.createRow(0); h4.createCell(0).setCellValue("Tên khách hàng"); h4.createCell(1).setCellValue("Số đơn"); h4.createCell(2).setCellValue("Tổng chi (VNĐ)");
            r = 1; for (ReportStat s : custs) { Row row = s4.createRow(r++); row.createCell(0).setCellValue(s.getLabel()); row.createCell(1).setCellValue(s.getValue()); row.createCell(2).setCellValue(s.getDoubleValue()); }
            
            // Sheet 5: Quá hạn
            Sheet s5 = workbook.createSheet("Quá hạn");
            Row h5 = s5.createRow(0); h5.createCell(0).setCellValue("Mã đơn"); h5.createCell(1).setCellValue("Khách hàng"); h5.createCell(2).setCellValue("Hạn trả"); h5.createCell(3).setCellValue("Tiền phạt dự kiến (VNĐ)");
            r = 1; for (RentalOrder o : overdue) { Row row = s5.createRow(r++); row.createCell(0).setCellValue(o.getOrderCode()); row.createCell(1).setCellValue(o.getCustomerName()); row.createCell(2).setCellValue(o.getExpectedReturnDate()!=null?o.getExpectedReturnDate().toString():""); row.createCell(3).setCellValue(o.getTotalAmount()); }
            
            try (FileOutputStream out = new FileOutputStream(file)) { workbook.write(out); }
            return true;
        } catch (Exception e) { return false; }
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