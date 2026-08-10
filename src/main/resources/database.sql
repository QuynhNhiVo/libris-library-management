-- ==================== Database SQLite ====================

-- ==================== Bảng Books ====================
CREATE TABLE IF NOT EXISTS Books (
    BookID INTEGER PRIMARY KEY AUTOINCREMENT,
    BookCode VARCHAR(20) NOT NULL UNIQUE,
    Title VARCHAR(200) NOT NULL,
    Author VARCHAR(100) NOT NULL,
    Category VARCHAR(100),
    Publisher VARCHAR(100),
    PublishYear INTEGER,
    BookStatus VARCHAR(20) NOT NULL,
    RentalPrice INTEGER NOT NULL DEFAULT 10000,
    DepositPrice INTEGER NOT NULL DEFAULT 50000,
    CHECK (BookStatus IN ('Available', 'Rented', 'Pending'))
);

-- ==================== Bảng Customers ====================
CREATE TABLE IF NOT EXISTS Customers (
    CustomerID INTEGER PRIMARY KEY AUTOINCREMENT,
    CustomerCode VARCHAR(20) NOT NULL UNIQUE,
    FullName VARCHAR(100) NOT NULL,
    Phone VARCHAR(20),
    Address VARCHAR(200),
    Email VARCHAR(100)
);

-- ==================== Bảng Users ====================
CREATE TABLE IF NOT EXISTS Users (
    UserID INTEGER PRIMARY KEY AUTOINCREMENT,
    Username VARCHAR(50) NOT NULL UNIQUE,
    Password VARCHAR(100) NOT NULL,
    Role VARCHAR(20) NOT NULL,
    CustomerID INTEGER NULL,
    CHECK (Role IN ('Admin', 'Customer')),
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID)
);

-- ==================== Bảng RentalOrders ====================
CREATE TABLE IF NOT EXISTS RentalOrders (
    OrderID INTEGER PRIMARY KEY AUTOINCREMENT,
    OrderCode VARCHAR(20) NOT NULL UNIQUE,
    CustomerID INTEGER NOT NULL,
    RentDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ExpectedReturnDate DATETIME NOT NULL,
    ReturnDate DATETIME NULL,
    OrderStatus VARCHAR(20) NOT NULL,
    TotalDeposit INTEGER NOT NULL DEFAULT 0,
    TotalRentalFee INTEGER NOT NULL DEFAULT 0,
    LateFee INTEGER NOT NULL DEFAULT 0,
    TotalAmount INTEGER NOT NULL DEFAULT 0,
    CHECK (OrderStatus IN ('Pending', 'Renting', 'Returned', 'Rejected')),
    CHECK (ExpectedReturnDate >= RentDate),
    CHECK (ReturnDate IS NULL OR ReturnDate >= RentDate),
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID)
);

-- ==================== Bảng RentalOrderDetails ====================
CREATE TABLE IF NOT EXISTS RentalOrderDetails (
    OrderDetailID INTEGER PRIMARY KEY AUTOINCREMENT,
    OrderID INTEGER NOT NULL,
    BookID INTEGER NOT NULL,
    FOREIGN KEY (OrderID) REFERENCES RentalOrders(OrderID),
    FOREIGN KEY (BookID) REFERENCES Books(BookID)
);

-- ==================== Dữ liệu mẫu ====================
-- Books
INSERT OR IGNORE INTO Books (BookID, BookCode, Title, Author, Category, Publisher, PublishYear, BookStatus)
VALUES
(1, 'B001', 'Dế Mèn Phiêu Lưu Ký', 'Tô Hoài', 'Thiếu nhi', 'Kim Đồng', 2019, 'Available'),
(2, 'B002', 'Lão Hạc', 'Nam Cao', 'Văn học', 'Giáo Dục', 2018, 'Available'),
(3, 'B003', 'Tắt Đèn', 'Ngô Tất Tố', 'Văn học', 'Văn Học', 2020, 'Rented');

-- Customers
INSERT OR IGNORE INTO Customers (CustomerID, CustomerCode, FullName, Phone, Address, Email)
VALUES
(1, 'C001', 'Nguyễn Văn A', '0901111111', 'Hồ Chí Minh', 'a@gmail.com'),
(2, 'C002', 'Trần Thị B', '0902222222', 'Hà Nội', 'b@gmail.com'),
(4, 'C003', 'Le Van C', '0903333333', 'Da Nang', 'c@gmail.com');

-- Users
INSERT OR IGNORE INTO Users (UserID, Username, Password, Role, CustomerID)
VALUES
(3, 'admin', '123', 'Admin', NULL),
(4, 'customer1', '123', 'Customer', 1),
(5, 'customer2', '123', 'Customer', 2);

-- RentalOrders và Details
INSERT OR IGNORE INTO RentalOrders (OrderID, OrderCode, CustomerID, RentDate, ExpectedReturnDate, ReturnDate, OrderStatus)
VALUES (1, 'O001', 2, '2026-04-09 01:37:15', '2026-04-16 01:37:15', NULL, 'Renting');

INSERT OR IGNORE INTO RentalOrderDetails (OrderDetailID, OrderID, BookID)
VALUES (1, 1, 3);

-- Reset auto_increment - SQLite dùng sqlite_sequence
DELETE FROM sqlite_sequence WHERE name='Books';
INSERT INTO sqlite_sequence (name, seq) VALUES ('Books', 3);
DELETE FROM sqlite_sequence WHERE name='Customers';
INSERT INTO sqlite_sequence (name, seq) VALUES ('Customers', 4);
DELETE FROM sqlite_sequence WHERE name='Users';
INSERT INTO sqlite_sequence (name, seq) VALUES ('Users', 5);
DELETE FROM sqlite_sequence WHERE name='RentalOrders';
INSERT INTO sqlite_sequence (name, seq) VALUES ('RentalOrders', 1);
DELETE FROM sqlite_sequence WHERE name='RentalOrderDetails';
INSERT INTO sqlite_sequence (name, seq) VALUES ('RentalOrderDetails', 1);

-- ==================== Thêm 30 đầu sách mới ====================
INSERT OR IGNORE INTO Books (BookCode, Title, Author, Category, Publisher, PublishYear, BookStatus) VALUES
('B004', 'Số Đỏ', 'Vũ Trọng Phụng', 'Văn học', 'Văn Học', 2017, 'Available'),
('B005', 'Chí Phèo', 'Nam Cao', 'Văn học', 'Giáo Dục', 2018, 'Available'),
('B006', 'Truyện Kiều', 'Nguyễn Du', 'Thơ ca', 'Văn Học', 2016, 'Available'),
('B007', 'Vợ Nhặt', 'Kim Lân', 'Văn học', 'Giáo Dục', 2019, 'Available'),
('B008', 'Vợ Chồng A Phủ', 'Tô Hoài', 'Văn học', 'Kim Đồng', 2020, 'Available'),
('B009', 'Rừng Xà Nu', 'Nguyễn Trung Thành', 'Văn học', 'Quân Đội', 2015, 'Available'),
('B010', 'Người Lái Đò Sông Đà', 'Nguyễn Tuân', 'Tùy bút', 'Văn Học', 2018, 'Available'),
('B011', 'Hai Đứa Trẻ', 'Thạch Lam', 'Văn học', 'Giáo Dục', 2017, 'Available'),
('B012', 'Gió Lạnh Đầu Mùa', 'Thạch Lam', 'Văn học', 'Kim Đồng', 2019, 'Available'),
('B013', 'Đất Rừng Phương Nam', 'Đoàn Giỏi', 'Thiếu nhi', 'Kim Đồng', 2021, 'Available'),
('B014', 'Tuổi Thơ Dữ Dội', 'Phùng Quán', 'Thiếu nhi', 'Kim Đồng', 2016, 'Available'),
('B015', 'Nhật Ký Trong Tù', 'Hồ Chí Minh', 'Thơ ca', 'Chính Trị', 2015, 'Available'),
('B016', 'Cho Tôi Xin Một Vé Đi Tuổi Thơ', 'Nguyễn Nhật Ánh', 'Thiếu nhi', 'Trẻ', 2022, 'Available'),
('B017', 'Mắt Biếc', 'Nguyễn Nhật Ánh', 'Văn học', 'Trẻ', 2019, 'Available'),
('B018', 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Nguyễn Nhật Ánh', 'Thiếu nhi', 'Trẻ', 2020, 'Available'),
('B019', 'Kính Vạn Hoa', 'Nguyễn Nhật Ánh', 'Thiếu nhi', 'Trẻ', 2018, 'Available'),
('B020', 'Cô Gái Đến Từ Hôm Qua', 'Nguyễn Nhật Ánh', 'Văn học', 'Trẻ', 2021, 'Available'),
('B021', 'Nỗi Buồn Chiến Tranh', 'Bảo Ninh', 'Văn học', 'Văn Học', 2017, 'Available'),
('B022', 'Cánh Đồng Bất Tận', 'Nguyễn Ngọc Tư', 'Văn học', 'Trẻ', 2020, 'Available'),
('B023', 'Sông Đông Êm Đềm', 'Sholokhov', 'Văn học nước ngoài', 'Văn Học', 2016, 'Available'),
('B024', 'Ông Già Và Biển Cả', 'Hemingway', 'Văn học nước ngoài', 'Văn Học', 2019, 'Available'),
('B025', 'Hoàng Tử Bé', 'Saint-Exupéry', 'Thiếu nhi', 'Kim Đồng', 2022, 'Available'),
('B026', 'Harry Potter Và Hòn Đá Phù Thủy', 'J.K. Rowling', 'Giả tưởng', 'Trẻ', 2021, 'Available'),
('B027', 'Đắc Nhân Tâm', 'Dale Carnegie', 'Kỹ năng sống', 'Tổng Hợp', 2020, 'Available'),
('B028', 'Nhà Giả Kim', 'Paulo Coelho', 'Văn học nước ngoài', 'Văn Học', 2018, 'Available'),
('B029', 'Sapiens: Lược Sử Loài Người', 'Yuval Noah Harari', 'Khoa học', 'Thế Giới', 2023, 'Available'),
('B030', 'Tư Duy Nhanh Và Chậm', 'Daniel Kahneman', 'Tâm lý học', 'Thế Giới', 2022, 'Available'),
('B031', 'Doraemon - Tập 1', 'Fujiko F. Fujio', 'Truyện tranh', 'Kim Đồng', 2019, 'Available'),
('B032', 'Conan - Tập 1', 'Aoyama Gosho', 'Truyện tranh', 'Kim Đồng', 2020, 'Available'),
('B033', 'Totto-chan Bên Cửa Sổ', 'Tetsuko Kuroyanagi', 'Thiếu nhi', 'Kim Đồng', 2021, 'Available');

-- ==================== Thêm khách hàng và đơn thuê mới ====================
INSERT OR IGNORE INTO Customers (CustomerCode, FullName, Phone, Address, Email) VALUES
('C004', 'Phạm Thị D', '0904444444', 'Huế', 'd@gmail.com'),
('C005', 'Vũ Văn E', '0905555555', 'Hải Phòng', 'e@gmail.com'),
('C006', 'Trần Thị F', '0906666666', 'Cần Thơ', 'f@gmail.com'),
('C007', 'Hoàng Văn G', '0907777777', 'Bình Dương', 'g@gmail.com'),
('C008', 'Lý Thị H', '0908888888', 'Nha Trang', 'h@gmail.com'),
('C009', 'Ngô Văn I', '0909999999', 'Vũng Tàu', 'i@gmail.com'),
('C010', 'Đinh Thị K', '0910000000', 'Huế', 'k@gmail.com'),
('C011', 'Lê Văn L', '0911111111', 'Đà Lạt', 'l@gmail.com'),
('C012', 'Bùi Thị M', '0912222222', 'Biên Hòa', 'm@gmail.com'),
('C013', 'Đỗ Văn N', '0913333333', 'Vinh', 'n@gmail.com');

-- Lấy ID để gán đơn (SQLite không hỗ trợ SET @var, dùng subquery)
INSERT OR IGNORE INTO RentalOrders (OrderCode, CustomerID, RentDate, ExpectedReturnDate, ReturnDate, OrderStatus, TotalDeposit, TotalRentalFee, LateFee, TotalAmount)
VALUES ('O101', (SELECT CustomerID FROM Customers WHERE CustomerCode = 'C004'), '2026-03-01', '2026-03-05', '2026-03-05', 'Returned', 50000, 50000, 0, 50000);
INSERT OR IGNORE INTO RentalOrderDetails (OrderID, BookID) 
SELECT (SELECT OrderID FROM RentalOrders WHERE OrderCode = 'O101'), (SELECT BookID FROM Books WHERE BookCode = 'B004');

INSERT OR IGNORE INTO RentalOrders (OrderCode, CustomerID, RentDate, ExpectedReturnDate, ReturnDate, OrderStatus, TotalDeposit, TotalRentalFee, LateFee, TotalAmount)
VALUES ('O102', (SELECT CustomerID FROM Customers WHERE CustomerCode = 'C005'), '2026-03-10', '2026-03-15', '2026-03-16', 'Returned', 100000, 120000, 40000, 160000);
INSERT OR IGNORE INTO RentalOrderDetails (OrderID, BookID) 
SELECT (SELECT OrderID FROM RentalOrders WHERE OrderCode = 'O102'), (SELECT BookID FROM Books WHERE BookCode = 'B005');
INSERT OR IGNORE INTO RentalOrderDetails (OrderID, BookID) 
SELECT (SELECT OrderID FROM RentalOrders WHERE OrderCode = 'O102'), (SELECT BookID FROM Books WHERE BookCode = 'B006');

INSERT OR IGNORE INTO RentalOrders (OrderCode, CustomerID, RentDate, ExpectedReturnDate, ReturnDate, OrderStatus, TotalDeposit, TotalRentalFee, LateFee, TotalAmount)
VALUES ('O103', (SELECT CustomerID FROM Customers WHERE CustomerCode = 'C006'), '2026-03-20', '2026-03-25', '2026-03-23', 'Returned', 150000, 90000, 0, 90000);
INSERT OR IGNORE INTO RentalOrderDetails (OrderID, BookID) 
SELECT (SELECT OrderID FROM RentalOrders WHERE OrderCode = 'O103'), (SELECT BookID FROM Books WHERE BookCode = 'B007');
INSERT OR IGNORE INTO RentalOrderDetails (OrderID, BookID) 
SELECT (SELECT OrderID FROM RentalOrders WHERE OrderCode = 'O103'), (SELECT BookID FROM Books WHERE BookCode = 'B008');
INSERT OR IGNORE INTO RentalOrderDetails (OrderID, BookID) 
SELECT (SELECT OrderID FROM RentalOrders WHERE OrderCode = 'O103'), (SELECT BookID FROM Books WHERE BookCode = 'B004');

-- Tháng 4
INSERT OR IGNORE INTO RentalOrders (OrderCode, CustomerID, RentDate, ExpectedReturnDate, ReturnDate, OrderStatus, TotalDeposit, TotalRentalFee, LateFee, TotalAmount)
VALUES ('O104', (SELECT CustomerID FROM Customers WHERE CustomerCode = 'C007'), '2026-04-05', '2026-04-12', '2026-04-12', 'Returned', 50000, 70000, 0, 70000);
INSERT OR IGNORE INTO RentalOrderDetails (OrderID, BookID) 
SELECT (SELECT OrderID FROM RentalOrders WHERE OrderCode = 'O104'), (SELECT BookID FROM Books WHERE BookCode = 'B005');

INSERT OR IGNORE INTO RentalOrders (OrderCode, CustomerID, RentDate, ExpectedReturnDate, ReturnDate, OrderStatus, TotalDeposit, TotalRentalFee, LateFee, TotalAmount)
VALUES ('O105', (SELECT CustomerID FROM Customers WHERE CustomerCode = 'C004'), '2026-04-10', '2026-04-15', '2026-04-17', 'Returned', 100000, 100000, 40000, 140000);
INSERT OR IGNORE INTO RentalOrderDetails (OrderID, BookID) 
SELECT (SELECT OrderID FROM RentalOrders WHERE OrderCode = 'O105'), (SELECT BookID FROM Books WHERE BookCode = 'B004');
INSERT OR IGNORE INTO RentalOrderDetails (OrderID, BookID) 
SELECT (SELECT OrderID FROM RentalOrders WHERE OrderCode = 'O105'), (SELECT BookID FROM Books WHERE BookCode = 'B006');

INSERT OR IGNORE INTO RentalOrders (OrderCode, CustomerID, RentDate, ExpectedReturnDate, ReturnDate, OrderStatus, TotalDeposit, TotalRentalFee, LateFee, TotalAmount)
VALUES ('O106', (SELECT CustomerID FROM Customers WHERE CustomerCode = 'C005'), '2026-04-18', '2026-04-24', '2026-04-24', 'Returned', 50000, 60000, 0, 60000);
INSERT OR IGNORE INTO RentalOrderDetails (OrderID, BookID) 
SELECT (SELECT OrderID FROM RentalOrders WHERE OrderCode = 'O106'), (SELECT BookID FROM Books WHERE BookCode = 'B007');

INSERT OR IGNORE INTO RentalOrders (OrderCode, CustomerID, RentDate, ExpectedReturnDate, ReturnDate, OrderStatus, TotalDeposit, TotalRentalFee, LateFee, TotalAmount)
VALUES ('O107', (SELECT CustomerID FROM Customers WHERE CustomerCode = 'C006'), '2026-04-20', '2026-04-25', '2026-04-26', 'Returned', 100000, 100000, 40000, 140000);
INSERT OR IGNORE INTO RentalOrderDetails (OrderID, BookID) 
SELECT (SELECT OrderID FROM RentalOrders WHERE OrderCode = 'O107'), (SELECT BookID FROM Books WHERE BookCode = 'B008');
INSERT OR IGNORE INTO RentalOrderDetails (OrderID, BookID) 
SELECT (SELECT OrderID FROM RentalOrders WHERE OrderCode = 'O107'), (SELECT BookID FROM Books WHERE BookCode = 'B005');

SELECT 'Database Libris đã được khởi tạo thành công!' AS Message;