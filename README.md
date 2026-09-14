<div align="center">

# 📚 LIBRIS - HỆ THỐNG QUẢN LÝ THƯ VIỆN THÔNG MINH
### Smart Library Management System (Desktop Application)

**Đồ án môn học IE303 - Công nghệ Java | Trường Đại học Công nghệ Thông tin (UIT - VNUHCM)**

[![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![GUI](https://img.shields.io/badge/GUI-Java%20Swing-007396?style=for-the-badge&logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![UI Theme](https://img.shields.io/badge/Look%20%26%20Feel-FlatLaf%203.7.1-4A154B?style=for-the-badge)](https://www.formdev.com/flatlaf/)
[![Database](https://img.shields.io/badge/Database-SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)](https://www.sqlite.org/)
[![Build Tool](https://img.shields.io/badge/Build-Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Excel](https://img.shields.io/badge/Export-Apache%20POI%205.5.1-217346?style=for-the-badge&logo=microsoftexcel&logoColor=white)](https://poi.apache.org/)
[![Chart](https://img.shields.io/badge/Charts-JFreeChart-FF6F00?style=for-the-badge)](https://www.jfree.org/jfreechart/)
[![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)](LICENSE)

*Giải pháp phần mềm Desktop quản lý thư viện hiện đại, tối ưu hóa quy trình quản lý sách, độc giả, giao dịch mượn/trả, tự động hóa tính phạt trễ hạn và trực quan hóa báo cáo tài chính.*

---

</div>

## 📑 Mục lục (Table of Contents)
- [1. Giới thiệu tổng quan (Overview)](#1-giới-thiệu-tổng-quan-overview)
- [2. Vấn đề giải quyết & Giá trị mang lại](#2-vấn-đề-giải-quyết--giá-trị-mang-lại)
- [3. Kiến trúc Hệ thống (System Architecture)](#3-kiến-trúc-hệ-thống-system-architecture)
- [4. Các Tính năng Cốt lõi (Key Features)](#4-các-tính-năng-cốt-lõi-key-features)
- [5. Công nghệ & Thư viện (Tech Stack)](#5-công-nghệ--thư-viện-tech-stack)
- [6. Hướng dẫn Cài đặt (Getting Started)](#6-hướng-dẫn-cài-đặt-getting-started)
- [7. Hướng dẫn Chi tiết các Cách Chạy Ứng dụng](#7-hướng-dẫn-chi-tiết-các-cách-chạy-ứng-dụng)
- [8. Tài liệu Dự án & Báo cáo Trực tuyến (Online Documentation)](#8-tài-liệu-dự-án--báo-cáo-trực-tuyến-online-documentation)
- [9. Cấu trúc Thư mục Dự án (Directory Structure)](#9-cấu-trúc-thư-mục-dự-án-directory-structure)
- [10. Đóng góp & Bản quyền (License & Credits)](#10-đóng-góp--bản-quyền-license--credits)

---

## 1. Giới thiệu tổng quan (Overview)

**Libris Library Management System** là ứng dụng quản lý thư viện máy trạm (Desktop Application) được phát triển trong khuôn khổ môn học **IE303 - Công nghệ Java**. 

Ứng dụng được xây dựng trên nền tảng **Java 25**, tích hợp bộ giao diện hiện đại **FlatLaf 3.7.1 Look and Feel**, hệ quản trị cơ sở dữ liệu nhúng **SQLite 3**, công cụ vẽ biểu đồ **JFreeChart** và thư viện xuất báo cáo **Apache POI**. Libris mang đến một giải pháp số hóa toàn diện, thay thế phương thức quản lý thư viện thủ công truyền thống bằng quy trình tự động, chính xác và trực quan.

---

## 2. Vấn đề giải quyết & Giá trị mang lại

| 🛑 Thách thức Thư viện Truyền thống | 💡 Giải pháp Số hóa của Libris |
| :--- | :--- |
| **Tra cứu thủ công tốn thời gian**: Tìm kiếm sách qua sổ sách hoặc thẻ giấy mất nhiều chi phí cơ hội. | **Tìm kiếm đa tiêu chí tức thì**: Tra cứu danh mục sách theo Tiêu đề, Tác giả, Thể loại, Nhà xuất bản với tốc độ dưới **50ms**. |
| **Sai sót quản lý mượn/trả**: Nhầm lẫn ngày hẹn trả, tính sai tiền phạt quá hạn thủ công. | **Tự động hóa giao dịch DB**: Cập nhật tồn kho nguyên tố (Atomic Transaction), tự động tính tiền phạt trễ hạn **$0.5/ngày**. |
| **Thiếu báo cáo trực quan**: Ban quản lý không nắm bắt được xu hướng đọc và doanh thu phạt. | **Dashboard trực quan hóa**: 6 thẻ KPI chỉ số, 3 biểu đồ **JFreeChart** phân tích chi tiết và xuất báo cáo **Excel (.xlsx)**. |
| **Giao diện Desktop cũ kỹ**: Ứng dụng Swing mặc định có thiết kế thô cứng, thiếu trải nghiệm người dùng. | **Giao diện FlatLaf phẳng cao cấp**: Chuẩn UX/UI sang trọng, giao diện responsive mượt mà với hiệu ứng hover và icon sắc nét. |

---

## 3. Kiến trúc Hệ thống (System Architecture)

Dự án tuân thủ nghiêm ngặt mô hình kiến trúc **3 tầng (Layered Architecture)** kết hợp với hai mẫu thiết kế **MVC (Model - View - Controller)** và **DAO (Data Access Object)**, giúp tách biệt hoàn toàn giữa giao diện, logic xử lý nghiệp vụ và truy xuất dữ liệu:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          PRESENTATION LAYER (View)                      │
│        Java Swing + FlatLaf Theme + CardLayout Single-Window Frame      │
│   (LoginView, DashboardView, BookView, CustomerView, RentalOrderView)   │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ User Action Events / Data Binding
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         BUSINESS LOGIC LAYER (Controller)               │
│      Business Rules Validation, Event Handling & Chart Rendering        │
│ (UserController, BookController, CustomerController, RentalController)  │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ DTO Objects / Logic Requests
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                          PERSISTENCE LAYER (DAO)                        │
│             Data Access Objects & Singleton Connection Pool             │
│    (UserDAO, BookDAO, CustomerDAO, RentalOrderDAO, DatabaseConnection)  │
└────────────────────────────────────┬────────────────────────────────────┘
                                     │ SQL Queries (JDBC Driver)
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        DATA STORAGE (SQLite Engine)                     │
│               Embedded Relational DB (Libris.db / WAL Mode)             │
│             [Users] ◄─── [RentalOrders] ───► [Customers]                │
│                                  │                                      │
│                                  ▼                                      │
│                               [Books]                                   │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Các Tính năng Cốt lõi (Key Features)

* 🔐 **Đăng nhập & Phân quyền Đa vai trò (RBAC Authentication)**:
  * **Role Admin (Thủ thư)**: Toàn quyền quản lý sách, độc giả, duyệt/xử lý đơn mượn trả và xem báo cáo tài chính.
  * **Role Customer (Độc giả)**: Đăng nhập cá nhân, tra cứu sách online, gửi yêu cầu mượn sách trực tuyến và theo dõi lịch sử mượn trả.

* 📚 **Quản lý Kho Sách (Book Catalog Management)**:
  * Quản lý CRUD (Thêm, Sửa, Xóa, Cập nhật số lượng & giá mượn).
  * Tìm kiếm linh hoạt tức thì theo Mã sách, Tiêu đề, Tác giả, Thể loại, Nhà xuất bản.
  * Ràng buộc toàn vẹn khóa ngoại (Prevent delete if book has active rental orders).

* 👥 **Quản lý Độc giả (Customer Management)**:
  * Quản lý thông tin độc giả, số điện thoại (Unique constraint), email, địa chỉ và ngày đăng ký.
  * Theo dõi lịch sử mượn trả và công nợ phạt của từng độc giả.

* 🔄 **Quản lý Mượn / Trả Sách & Xử lý Giao dịch (Rental Transactions)**:
  * Tạo đơn mượn sách mới với cơ chế **Transaction Rollback** nguyên tố (Tự động trừ 1 số lượng sách trong kho).
  * Xử lý trả sách, tự động tính toán số ngày quá hạn và tiền phạt quá hạn (**$0.5 / ngày**).
  * Duyệt hoặc Từ chối các đơn mượn đăng ký trực tuyến từ phía Độc giả.

* 💻 **Cổng Tra cứu Độc giả (Customer Self-Service Portal)**:
  * Độc giả tự tra cứu danh mục sách khả dụng.
  * Đăng ký mượn sách trực tuyến (Tạo phiếu trạng thái `Pending` chờ Thủ thư phê duyệt).

* 📊 **Dashboard & Báo cáo Thống kê Trực quan (Analytics & Reports)**:
  * 6 Thẻ KPI chỉ số tổng quan: Tổng số sách, Sách đang mượn, Tổng độc giả, Đơn chờ duyệt, Doanh thu phạt,...
  * 3 Biểu đồ đồ họa **JFreeChart**: Biểu đồ doanh thu tháng, Thống kê thể loại sách, Top 5 sách mượn nhiều nhất.
  * Xuất toàn bộ báo cáo và danh mục ra file **Excel (.xlsx)** thông qua **Apache POI**.

---

## 5. Công nghệ & Thư viện (Tech Stack)

| Thành phần | Công nghệ / Thư viện | Phiên bản | Mô tả chi tiết |
| :--- | :--- | :---: | :--- |
| **Language Runtime** | **OpenJDK / Java** | `25` | Môi trường thực thi Java mới nhất. |
| **GUI Framework** | **Java Swing** | Standard | Khung phát triển giao diện desktop ứng dụng. |
| **UI Theme** | **FlatLaf & Extras** | `3.7.1` | Bộ giao diện phẳng sang trọng, hỗ trợ SVG vector icon. |
| **Database Engine** | **SQLite JDBC** | `3.53.2.0` | Cơ sở dữ liệu quan hệ nhúng cục bộ siêu nhẹ. |
| **Data Export** | **Apache POI (ooxml)** | `5.5.1` | Thư viện xử lý và xuất file Excel (.xlsx). |
| **Data Visualization** | **JFreeChart & JCommon** | `1.5.6` | Thư viện tạo biểu đồ đồ họa phân tích dữ liệu. |
| **Build & Dependency** | **Apache Maven** | `3.8+` | Trình quản lý dependency và đóng gói tự động. |
| **Automated Testing** | **JUnit 5 & AssertJ** | `5.10.2` | Khung kiểm thử tự động Unit Test & Integration Test. |

---

## 6. Hướng dẫn Cài đặt (Getting Started)

### Yêu cầu Hệ thống (Prerequisites)
* **Java Development Kit (JDK)**: Phiên bản **Java 17** trở lên (Khuyên dùng **JDK 25**).
* **Apache Maven**: Phiên bản **3.8.0** trở lên.
* **Hệ điều hành**: Windows 10/11, macOS, hoặc Linux.

### Các bước chuẩn bị dự án

1. **Clone repository về máy cục bộ**:
   ```bash
   git clone https://github.com/your-username/libris-library-management.git
   cd libris-library-management
   ```

2. **Kiểm tra file Cơ sở Dữ liệu**:
   * File cơ sở dữ liệu mẫu `Libris.db` đã được đính kèm sẵn ở thư mục gốc của repository (chứa dữ liệu mẫu người dùng, sách và đơn mượn).

---

## 7. Hướng dẫn Chi tiết các Cách Chạy Ứng dụng

Bạn có thể chạy ứng dụng Libris bằng một trong các cách dưới đây:

### 🔹 Cách 1: Chạy bằng lệnh Maven CLI (Khuyên dùng)
Biên dịch và chạy trực tiếp màn hình Đăng nhập `LoginView` thông qua plugin `exec-maven-plugin`:
```bash
mvn clean compile exec:java -Dexec.mainClass="com.libris.view.LoginView"
```

### 🔹 Cách 2: Chạy trực tiếp từ IDE (IntelliJ IDEA / Eclipse / VS Code)
1. Mở dự án `libris-library-management` trong IDE của bạn dưới dạng **Maven Project**.
2. Tìm đến file chứa hàm `main`:
   `src/main/java/com/libris/view/LoginView.java`
3. Nhấp chuột phải vào file `LoginView.java` và chọn **Run 'LoginView.main()'** (hoặc nhấn `Shift + F10` trong IntelliJ).

### 🔹 Cách 3: Chạy bằng Script kịch bản Windows (`run.bat`)
Nếu sử dụng hệ điều hành Windows, bạn chỉ cần nhấp đôi chuột vào file `run.bat` ở thư mục gốc hoặc chạy lệnh sau trong PowerShell / Command Prompt:
```cmd
.\run.bat
```

### 🔹 Cách 4: Đóng gói Fat JAR & Chạy độc lập
1. Đóng gói ứng dụng thành file `.jar` chứa đầy đủ thư viện phụ thuộc:
   ```bash
   mvn clean package
   ```
2. Chạy file `.jar` đã đóng gói trong thư mục `target/`:
   ```bash
   java -jar target/library-management-system-1.0-SNAPSHOT.jar
   ```

### 🧪 Chạy Kiểm thử Tự động (Unit & Integration Tests)
Ứng dụng hỗ trợ 2 file cấu hình Maven cho kiểm thử:
- Chạy kiểm thử tiêu chuẩn với `pom.xml`:
  ```bash
  mvn test
  ```
- Chạy kiểm thử mở rộng (bao gồm cả Mockito & AssertJ Swing) với `pom.test.xml`:
  ```bash
  mvn test -f pom.test.xml
  ```

---

## 🔑 Tài khoản Đăng nhập Mặc định (Seed Accounts)

Sau khi khởi chạy ứng dụng, bạn có thể đăng nhập bằng các tài khoản mẫu có sẵn trong `Libris.db`:

| Vai trò (Role) | Tên tài khoản (Username) | Mật khẩu (Password) | Quyền hạn chính |
| :--- | :---: | :---: | :--- |
| **Thủ thư (Admin)** | `admin` | `123` | Quyền Quản trị viên tối cao (Quản lý Sách, Độc giả, Duyệt đơn, Xem Báo cáo Dashboard). |
| **Độc giả (Customer)** | `customer1` | `123` | Quyền Khách hàng (Tra cứu danh mục sách, Đăng ký mượn online, Xem lịch sử cá nhân). |
| **Độc giả (Customer)** | `customer2` | `123` | Quyền Khách hàng (Tra cứu danh mục sách, Đăng ký mượn online, Xem lịch sử cá nhân). |

---

## 8. Tài liệu Dự án & Báo cáo Trực tuyến (Online Documentation)

Toàn bộ tài liệu phân tích thiết kế, đặc tả yêu cầu và báo cáo kiểm thử của dự án đã được xuất bản trực tuyến tại các liên kết dưới đây:

* 📊 **Báo cáo Tổng kết Đồ án (Project Summary Report)**:  
  [https://quynhnhivo.github.io/libris-library-management/report.html](https://quynhnhivo.github.io/libris-library-management/report.html)

* 📜 **Đặc tả Yêu cầu Phần mềm (Software Requirements Specification - SRS)**:  
  [https://quynhnhivo.github.io/libris-library-management/srs.html](https://quynhnhivo.github.io/libris-library-management/srs.html)

* 🧪 **Danh sách & Kết quả Kiểm thử Chi tiết (Full Test Cases & QA Matrix)**:  
  [https://quynhnhivo.github.io/libris-library-management/test-cases.html](https://quynhnhivo.github.io/libris-library-management/test-cases.html)

---

## 9. Cấu trúc Thư mục Dự án (Directory Structure)

```
libris-library-management/
├── src/
│   ├── main/
│   │   ├── java/com/libris/
│   │   │   ├── config/         # Cấu hình hằng số & kết nối DB (Constants, ConstantsTest)
│   │   │   ├── controller/     # Controllers xử lý sự kiện & nghiệp vụ (BookController,...)
│   │   │   ├── dao/            # Data Access Objects kết nối SQLite (BookDAO, UserDAO,...)
│   │   │   ├── helpers/        # Lớp tiện ích hỗ trợ (ExcelExporter, DateUtils,...)
│   │   │   ├── model/          # Các đối tượng Entity (Book, Customer, RentalOrder, User)
│   │   │   ├── utils/          # Tiện ích giao diện & biểu đồ (ChartGenerator, IconUtils)
│   │   │   └── view/           # Màn hình giao diện Swing (LoginView, MainFrame,...)
│   │   └── resources/
│   │       ├── database.sql    # Script khởi tạo cơ sở dữ liệu SQLite
│   │       └── icons/          # Tài nguyên biểu tượng SVG / PNG
│   └── test/                   # Kịch bản kiểm thử tự động (DatabaseIntegrationTest,...)
├── docs/                       # Thư mục chứa tài liệu báo cáo HTML & đặc tả dự án
│   ├── report.html             # Báo cáo tổng kết đồ án (Project Summary Report)
│   ├── srs.html                # Tài liệu Đặc tả Yêu cầu Phần mềm (SRS)
│   └── test-cases.html         # Báo cáo kết quả 35 Test Cases chi tiết
├── Libris.db                   # File Cơ sở dữ liệu SQLite chính (Seed Database)
├── pom.xml                     # Cấu hình Maven dependencies & build plugins chính
├── pom.test.xml                # Cấu hình Maven mở rộng cho kiểm thử (Mockito, AssertJ Swing, JUnit 5)
├── run.bat                     # Script khởi chạy nhanh ứng dụng trên Windows
└── README.md                   # Tài liệu hướng dẫn dự án
```

---

## 10. Đóng góp & Bản quyền (License & Credits)

* **Dự án**: Đồ án môn học **IE303 - Công nghệ Java**
* **Trường**: Đại học Công nghệ Thông tin - ĐHQG-HCM (UIT - VNUHCM)
* **Bản quyền**: Dự án thuộc sở hữu của Nhóm phát triển sinh viên và Giảng viên hướng dẫn môn học IE303.
* **Giấy phép**: Đồ án được phát hành theo giấy phép mở [MIT License](LICENSE).

<div align="center">

---

</div>
