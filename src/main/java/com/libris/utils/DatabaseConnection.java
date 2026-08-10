package com.libris.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.libris.config.Constants;

public class DatabaseConnection {

    private static boolean isInitialized = false;
    private static final Object INITIALIZATION_LOCK = new Object();

    private DatabaseConnection() {}

    /**
     * Lấy kết nối cơ sở dữ liệu
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");

            Properties props = new Properties();
            props.setProperty("journal_mode", "WAL");
            props.setProperty("synchronous", "NORMAL");
            props.setProperty("foreign_keys", "ON");
            props.setProperty("busy_timeout", "30000");

            Connection conn = DriverManager.getConnection(Constants.URL, props);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA journal_mode=WAL;");
                stmt.execute("PRAGMA synchronous=NORMAL;");
                stmt.execute("PRAGMA foreign_keys=ON;");
                stmt.execute("PRAGMA busy_timeout=30000;");
            }

            synchronized (INITIALIZATION_LOCK) {
                if (!isInitialized) {
                    initializeDatabase(conn);
                    isInitialized = true;
                }
            }

            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC Driver không tìm thấy!", e);
        }
    }

    /**
     * Đóng kết nối cơ sở dữ liệu
     */
    public static void closeConnection() {
        // Không giữ kết nối singleton nữa; phương thức này chỉ reset trạng thái khởi tạo.
        synchronized (INITIALIZATION_LOCK) {
            isInitialized = false;
        }
        System.out.println("Đóng kết nối cơ sở dữ liệu thành công!");
    }

    /**
     * Kiểm tra trạng thái kết nối cơ sở dữ liệu
     */
    public static boolean isConnectionOpen() {
        try {
            getConnection();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi kết nối database: " + e.getMessage());
            return false;
        }
    }

    private static void initializeDatabase(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Kiểm tra bảng Users tồn tại chưa
            try {
                stmt.executeQuery("SELECT 1 FROM Users LIMIT 1");
                // Bảng đã tồn tại
                return;
            } catch (SQLException e) {
                // Bảng chưa tồn tại, tạo mới
                System.out.println("Đang tạo database SQLite...");
                executeSqlScript(conn);
                System.out.println("Database SQLite đã được tạo thành công!");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khởi tạo database: " + e.getMessage());
        }
    }

    private static void executeSqlScript(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            InputStream inputStream = DatabaseConnection.class
                .getClassLoader()
                .getResourceAsStream("database.sql");
            
            if (inputStream == null) {
                System.err.println("Lỗi: Không tìm thấy file database.sql trong resources!");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sql = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                // Bỏ qua comment và dòng trống
                if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                    continue;
                }
                sql.append(line);
                // Khi gặp dấu ; thì thực thi câu lệnh
                if (line.trim().endsWith(";")) {
                    String statement = sql.toString();
                    // Bỏ qua các câu lệnh SELECT không cần thiết
                    if (!statement.trim().toUpperCase().startsWith("SELECT")) {
                        try {
                            stmt.execute(statement);
                        } catch (SQLException e) {
                            // Bỏ qua lỗi nếu bảng đã tồn tại
                            if (!e.getMessage().contains("already exists")) {
                                System.err.println("Lỗi khi thực thi: " + statement.substring(0, Math.min(100, statement.length())));
                            }
                        }
                    }
                    sql = new StringBuilder();
                }
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("Lỗi thực thi script SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getDatabasePath() {
        return Constants.DATABASE_NAME + ".db";
    }

    /* 
    public static void main(String[] args) {
        isConnectionOpen();
    }*/
}
