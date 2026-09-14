<div align="center">

# 📚 LIBRIS - HỆ THỐNG QUẢN LÝ THƯ VIỆN THÔNG MINH
### Smart Library Management System

**Đồ án môn học IE303 - Công nghệ Java**

[![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![GUI](https://img.shields.io/badge/GUI-Java%20Swing-007396?style=for-the-badge&logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![UI Theme](https://img.shields.io/badge/Look%20%26%20Feel-FlatLaf%203.7.1-4A154B?style=for-the-badge)](https://www.formdev.com/flatlaf/)
[![Database](https://img.shields.io/badge/Database-SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)](https://www.sqlite.org/)
[![Build Tool](https://img.shields.io/badge/Build-Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Chart](https://img.shields.io/badge/Charts-JFreeChart-FF6F00?style=for-the-badge)](https://www.jfree.org/jfreechart/)
[![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)](LICENSE)

*Một giải pháp phần mềm Desktop hiện đại, tối ưu hóa toàn bộ quy trình quản lý sách, độc giả, mượn/trả và thống kê báo cáo cho thư viện số.*

---

</div>

## 📑 Mục lục (Table of Contents)
- [1. Giới thiệu tổng quan](#1-giới-thiệu-tổng-quan)
- [2. Vấn đề & Giải pháp](#2-vấn-đề--giải-pháp)
- [3. Kiến trúc hệ thống (Architecture)](#3-kiến-trúc-hệ-thống-architecture)
- [4. Tính năng cốt lõi (Key Features)](#4-tính-năng-cốt-lõi-key-features)
- [5. Công nghệ sử dụng (Tech Stack)](#5-công-nghệ-sử-dụng-tech-stack)
- [6. Hướng dẫn cài đặt & Chạy ứng dụng (Getting Started)](#6-hướng-dẫn-cài-đặt--chạy-ứng-dụng-getting-started)
- [7. Demo & Hình ảnh giao diện](#7-demo--hình-ảnh-giao-diện)
- [8. Cấu trúc thư mục dự án](#8-cấu-trúc-thư-mục-dự-án)
- [9. Đóng góp & Bản quyền (License)](#9-đóng-góp--bản-quyền-license)

---

## 1. Giới thiệu tổng quan
**Libris** là hệ thống phần mềm quản lý thư viện trên nền tảng Desktop được phát triển trong khuôn khổ môn học **IE303 - Công nghệ Java**. 

Ứng dụng kết hợp sức mạnh xử lý của **Java 25**, giao diện thiết kế hiện đại với **FlatLaf**, cùng cơ sở dữ liệu **SQLite** gọn nhẹ nhưng mạnh mẽ. Libris giúp các trường học, trung tâm thông tin hoặc thư viện số hóa toàn bộ quy trình quản lý thủ công một cách chính xác, nhanh chóng và trực quan.

---

## 2. Vấn đề & Giải pháp

| 🛑 Thách thức trong thư viện truyền thống | 💡 Giải pháp số hóa của Libris |
| :--- | :--- |
| **Tìm kiếm thủ công**: Tốn thời gian tra cứu vị trí và tình trạng sách trong kho. | **Tìm kiếm tức thì**: Tra cứu sách theo từ khóa, thể loại, tác giả với bộ lọc đa tiêu chí. |
| **Ghi chép mượn/trả phức tạp**: Dễ sai sót ngày hẹn trả, tính sai phí phạt trễ hạn. | **Mượn/Trả tự động**: Tự động tính hạn trả, tính tiền phạt trễ hạn và quản lý trạng thái phiếu mượn. |
| **Báo cáo dữ liệu thụ động**: Khó thống kê danh mục sách mượn nhiều nhất hay doanh thu phạt. | **Dashboard trực quan**: Tích hợp **JFreeChart** vẽ biểu đồ báo cáo và xuất file **Excel (.xlsx)** nhanh chóng. |
| **Giao diện thô cứng**: Ứng dụng Desktop Java truyền thống thường có giao diện lỗi thời. | **Thiết kế FlatLaf sang trọng**: Chuẩn UX/UI hiện đại với trải nghiệm mượt mà, chuyên nghiệp. |

---

## 3. Kiến trúc hệ thống (Architecture)

Dự án được xây dựng tuân thủ theo mô hình thiết kế chuẩn **MVC (Model - View - Controller)** kết hợp với **DAO (Data Access Object) Pattern**, đảm bảo tính bóc tách trách nhiệm (Separation of Concerns), dễ bảo trì và mở rộng code.

```
┌──────────────────────────────────────────────────────────┐
│                      VIEW (GUI Layer)                    │
│   (Java Swing + FlatLaf + Custom UI Components)          │
└────────────┬──────────────────────────────▲──────────────┘
             │ User Actions                 │ UI Update
             ▼                              │
┌───────────────────────────────────────────┴──────────────┐
│                    CONTROLLER Layer                      │
│   (Business Logic Validation, Event Listeners)           │
└────────────┬──────────────────────────────▲──────────────┘
             │ Operations                   │ Data Objects
             ▼                              │
┌───────────────────────────────────────────┴──────────────┐
│                      DAO Layer                           │
│   (Data Access Objects: BookDAO, CustomerDAO, etc.)      │
└────────────┬──────────────────────────────▲──────────────┘
             │ SQL Queries / JDBC           │ ResultSet
             ▼                              │
┌───────────────────────────────────────────┴──────────────┐
│                  DATABASE (SQLite Engine)                │
│   (Libris.db - Write-Ahead Logging WAL Enabled)          │
└──────────────────────────────────────────────────────────┘
```

> 📌 **Architecture Diagram Placeholder**:
> ```
> [Client UI] ---> [Controllers] ---> [DAO Services] ---> [SQLite JDBC] ---> [Libris.db]
> ```

---

## 4. Tính năng cốt lõi (Key Features)

* 🔐 **Đăng nhập & Phân quyền tài khoản (Authentication & Authorization)**:
  * Phân quyền người dùng rõ ràng giữa **Thủ thư (Admin)** và **Độc giả (Customer)**.
  * Tính năng đổi mật khẩu, cập nhật hồ sơ cá nhân an toàn.
* 📚 **Quản lý Kho Sách (Books Management)**:
  * Thêm, sửa, xóa, cập nhật số lượng và danh mục sách.
  * Tìm kiếm linh hoạt theo mã sách, tên sách, tác giả, nhà xuất bản, thể loại.
* 👥 **Quản lý Độc giả (Customers Management)**:
  * Quản lý danh sách thẻ thư viện, thông tin liên lạc, lịch sử mượn sách của từng độc giả.
  * Khóa/Mở khóa tài khoản khi vi phạm quy định thư viện.
* 🔄 **Quản lý Mượn / Trả Sách (Rentals & Orders)**:
  * Lập phiếu mượn sách với thông tin ngày mượn và hạn trả.
  * Xử lý trả sách, kiểm tra trễ hạn và tự động tính phí phạt trễ hạn.
* 📊 **Báo cáo & Thống kê trực quan (Reports & Dashboard)**:
  * Tổng quan chỉ số trên Dashboard: Số lượng sách, độc giả active, phiếu mượn đang lưu hành.
  * Biểu đồ thống kê **JFreeChart** trực quan theo khoảng thời gian.
* 📑 **Xuất/Nhập dữ liệu Excel (Export & Import)**:
  * Xuất danh sách sách, danh sách độc giả và báo cáo mượn trả ra định dạng **Excel (.xlsx)** bằng Apache POI.

---

## 5. Công nghệ sử dụng (Tech Stack)

* **Core Runtime**: Java 25 (OpenJDK 25)
* **GUI Engine**: Java Swing (javax.swing)
* **UI Theme & Look and Feel**: FlatLaf 3.7.1
* **Database**: SQLite 3 (Driver `org.xerial:sqlite-jdbc:3.53.2.0`)
* **Charting**: JFreeChart 1.5.6 & JCommon
* **File Export**: Apache POI 5.5.1 (OOXML Excel Processor)
* **Build System**: Apache Maven 3.x
* **Testing Framework**: JUnit 5 Jupiter & AssertJ Swing

---

## 6. Hướng dẫn cài đặt & Chạy ứng dụng (Getting Started)

### Yêu cầu hệ thống (Prerequisites)
* **JDK**: Java Development Kit 25 trở lên.
* **Build Tool**: Apache Maven 3.8+ (đã được cài đặt và cấu hình `PATH`).
* **OS**: Windows / macOS / Linux.

### Các bước khởi chạy ứng dụng

#### Bước 1: Clone dự án về máy cục bộ
```bash
git clone https://github.com/your-username/libris-library-management.git
cd libris-library-management
```

#### Bước 2: Biên dịch dự án bằng Maven
```bash
mvn clean compile
```

#### Bước 3: Chạy ứng dụng

##### 🔹 Cách 1: Sử dụng lệnh Maven (Khuyên dùng)
```bash
mvn compile exec:java -Dexec.mainClass=com.libris.view.LoginView
```

##### 🔹 Cách 2: Sử dụng file kịch bản Windows (`run.bat`)
Chỉ cần nhấp đôi chuột vào file `run.bat` hoặc chạy lệnh trong Command Prompt / PowerShell:
```cmd
.\run.bat
```

##### 🔹 Cách 3: Đóng gói thành file `.jar` hoàn chỉnh
```bash
mvn clean package
java -jar target/library-management-system-1.0-SNAPSHOT.jar
```

#### 🧪 Chạy Kiểm thử (Unit Tests & Integration Tests)
```bash
mvn test
```

---

## 7. Cấu trúc thư mục dự án

```
libris-library-management/
├── src/
│   ├── main/
│   │   ├── java/com/libris/
│   │   │   ├── config/        # Cấu hình kết nối Database & Hệ thống
│   │   │   ├── controller/    # Xử lý logic điều hướng & sự kiện
│   │   │   ├── dao/           # Các lớp truy xuất dữ liệu SQLite (Data Access Object)
│   │   │   ├── helpers/       # Helper xử lý Excel, mã hóa, định dạng
│   │   │   ├── model/         # Các đối tượng dữ liệu Entity (Book, Customer, Rent,...)
│   │   │   ├── utils/         # Các tiện ích hệ thống
│   │   │   └── view/          # Giao diện người dùng Java Swing & FlatLaf Theme
│   │   └── resources/
│   │       ├── database.sql   # Script khởi tạo cấu trúc bảng SQLite
│   │       └── icons/         # Tài nguyên hình ảnh, icon
│   └── test/                  # Các kịch bản kiểm thử tự động (Unit & Integration Tests)
├── docs/                      # Tài liệu tài nguyên dự án (SRS, Reports, Testcases)
├── Libris.db                  # File Cơ sở dữ liệu SQLite chính
├── pom.xml                    # Cấu hình Maven dependencies & build plugins
├── pom.test.xml               # Cấu hình Maven dependencies & build plugins thực hiện các kiểm thử
└── run.bat                    # Script khởi chạy nhanh ứng dụng trên Windows
```

---

## 9. Đóng góp & Bản quyền (License)

* **Tên đồ án**: Đồ án môn học IE303 - Công nghệ Java
* **Bản quyền**: Dự án thuộc sở hữu của Nhóm phát triển sinh viên và giảng viên hướng dẫn môn học IE303.
* **Giấy phép**: Phát hành theo giấy phép [MIT License](LICENSE).

---

<div align="center">
  <sub>Báo lỗi hoặc đóng góp ý kiến? Vui lòng tạo <a href="https://github.com/your-username/libris-library-management/issues">Issue</a> hoặc gửi <a href="https://github.com/your-username/libris-library-management/pulls">Pull Request</a>.</sub>
</div>
