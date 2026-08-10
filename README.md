# Libris - Library Management System

Libris là ứng dụng quản lý thư viện desktop được phát triển bằng Java Swing và SQLite.

## Công nghệ sử dụng
- **Ngôn ngữ:** Java SE 25
- **Giao diện:** Java Swing, FlatLaf (UI/UX)
- **Cơ sở dữ liệu:** SQLite (nhúng)
- **Công cụ:** Apache Maven (quản lý dependency & build)
- **Thư viện chính:**
    - `sqlite-jdbc`: Kết nối cơ sở dữ liệu.
    - `flatlaf`: Giao diện.
    - `jfreechart`: Báo cáo thống kê.
    - `poi-ooxml`: Xuất dữ liệu ra Excel.

## Cách chạy ứng dụng

### Yêu cầu
- Đã cài đặt [Java JDK 25](https://adoptium.net/).
- Đã cài đặt [Apache Maven](https://maven.apache.org/).

### Cách chạy nhanh (Windows)
Sử dụng file `.bat` đã được cấu hình sẵn:
1. Mở terminal tại thư mục gốc của project.
2. Chạy lệnh:
   ```bash
   run.bat
