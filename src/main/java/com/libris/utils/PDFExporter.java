package com.libris.utils;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;

public class PDFExporter {

    public static void exportTableToPDF(Component parent, JTable table, String title) {
        try {
            File file = new File(System.getProperty("java.io.tmpdir"), title.replaceAll("[^a-zA-Z0-9.-]", "_") + ".html");
            try (FileWriter fw = new FileWriter(file)) {
                fw.write("<html><head><meta charset='UTF-8'><style>body{font-family: Arial, sans-serif; padding: 20px;} table{width: 100%; border-collapse: collapse; margin-top: 10px;} th, td{border: 1px solid #ddd; padding: 10px; text-align: left;} th{background-color: #f8fafc; color: #0f172a;}</style></head><body>");
                fw.write("<h2>" + title + "</h2><table><thead><tr>");
                TableModel model = table.getModel();
                for (int i = 0; i < model.getColumnCount(); i++) fw.write("<th>" + model.getColumnName(i) + "</th>");
                fw.write("</tr></thead><tbody>");
                for (int i = 0; i < model.getRowCount(); i++) {
                    fw.write("<tr>");
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object val = model.getValueAt(i, j);
                        fw.write("<td>" + (val != null ? val.toString() : "") + "</td>");
                    }
                    fw.write("</tr>");
                }
                fw.write("</tbody></table>");
                fw.write("<p style='color: gray; font-size: 12px; margin-top: 20px;'><i>* Dữ liệu kết xuất từ hệ thống Libris. Vui lòng bấm Ctrl + P để lưu thành file PDF.</i></p>");
                fw.write("</body></html>");
            }
            Desktop.getDesktop().browse(file.toURI());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Lỗi xuất báo cáo PDF: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void exportTextToPDF(Component parent, String text, String title) {
        try {
            File file = new File(System.getProperty("java.io.tmpdir"), title.replaceAll("[^a-zA-Z0-9.-]", "_") + ".html");
            try (FileWriter fw = new FileWriter(file)) {
                fw.write("<html><head><meta charset='UTF-8'><style>body{font-family: monospace; white-space: pre-wrap; padding: 20px; line-height: 1.6; background-color: #fcfcfc;} .receipt{background: white; padding: 20px; border: 1px dashed #ccc; max-width: 600px; margin: auto;}</style></head><body>");
                fw.write("<div class='receipt'><h2>" + title + "</h2>");
                fw.write("<div>" + text.replace("\n", "<br>") + "</div>");
                fw.write("<br><p style='color: gray; font-size: 12px; text-align:center;'><i>* Biên lai điện tử Libris. Bấm Ctrl + P để lưu file PDF.</i></p></div>");
                fw.write("</body></html>");
            }
            Desktop.getDesktop().browse(file.toURI());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Lỗi xuất biên lai PDF: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}