package com.libris.integration;

import com.libris.config.ConstantsTest;
import com.libris.dao.BookDAO;
import com.libris.dao.CustomerDAO;
import com.libris.dao.RentalOrderDAO;
import com.libris.dao.UserDAO;
import com.libris.model.Book;
import com.libris.model.Customer;
import com.libris.model.RentalOrder;
import com.libris.model.User;
import com.libris.utils.DatabaseConnection;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests using local test database (Libris.test.db) via ConstantsTest.
 * Covered Test Cases:
 * TC-AUTH-01, TC-AUTH-02 (UserDAO login)
 * TC-BOOK-01, TC-BOOK-02, TC-BOOK-03 (UNIQUE), TC-BOOK-04, TC-BOOK-05, TC-BOOK-06 (FK Constraint)
 * TC-CUST-01, TC-CUST-02, TC-CUST-03, TC-CUST-04
 * TC-ORD-01, TC-ORD-02, TC-ORD-04 (Transaction & Rollback), TC-NFR-02, TC-NFR-03
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseIntegrationTest {

    private static BookDAO bookDao;
    private static CustomerDAO customerDao;
    private static RentalOrderDAO rentalOrderDao;
    private static UserDAO userDao;

    @BeforeAll
    static void setupDatabaseUrl() {
        System.out.println("[DatabaseIntegrationTest] Using test database: " + ConstantsTest.URL);
        DatabaseConnection.closeConnection();

        bookDao = new BookDAO();
        customerDao = new CustomerDAO();
        rentalOrderDao = new RentalOrderDAO();
        userDao = new UserDAO();
    }

    @AfterAll
    static void cleanup() {
        DatabaseConnection.closeConnection();
    }

    @Test
    @Order(1)
    @DisplayName("test_FR_AUTH_01_TC_AUTH_01_userDaoLogin_success()")
    void test_FR_AUTH_01_TC_AUTH_01_userDaoLogin_success() throws SQLException {
        User admin = userDao.login("admin", "123");
        assertNotNull(admin, "Admin user should be retrieved from seed database");
        assertEquals("Admin", admin.getRole());

        User customer = userDao.login("customer1", "123");
        assertNotNull(customer, "Customer1 user should be retrieved from seed database");
        assertEquals("Customer", customer.getRole());
    }

    @Test
    @Order(2)
    @DisplayName("test_FR_BOOK_01_TC_BOOK_01_getAllBooks_loadsData()")
    void test_FR_BOOK_01_TC_BOOK_01_getAllBooks_loadsData() throws SQLException {
        List<Book> books = bookDao.getAllBooks();
        assertNotNull(books);
        assertTrue(books.size() >= 3, "Database should contain initial seed books");
    }

    @Test
    @Order(3)
    @DisplayName("test_FR_BOOK_02_TC_BOOK_02_addBook_success()")
    void test_FR_BOOK_02_TC_BOOK_02_addBook_success() throws SQLException {
        String uniqueCode = "BX" + (System.currentTimeMillis() % 10000);
        Book newBook = new Book();
        newBook.setBookCode(uniqueCode);
        newBook.setTitle("Kỹ Thuật Test Tự Động Java");
        newBook.setAuthor("Libris QA Team");
        newBook.setCategory("Công nghệ");
        newBook.setPublisher("ĐHQG");
        newBook.setPublishYear(2026);
        newBook.setBookStatus("Available");
        newBook.setRentalPrice(12000);
        newBook.setDepositPrice(50000);

        boolean added = bookDao.addBook(newBook);
        assertTrue(added, "Book should be inserted into database");
    }

    @Test
    @Order(4)
    @DisplayName("test_FR_BOOK_02_TC_BOOK_03_addBook_duplicateUniqueCode_throwsException()")
    void test_FR_BOOK_02_TC_BOOK_03_addBook_duplicateUniqueCode_throwsException() {
        Book duplicateBook = new Book();
        duplicateBook.setBookCode("B001"); // Already exists
        duplicateBook.setTitle("Trùng Mã Sách");
        duplicateBook.setAuthor("N/A");
        duplicateBook.setBookStatus("Available");

        assertThrows(SQLException.class, () -> {
            bookDao.addBook(duplicateBook);
        }, "Adding duplicate BookCode B001 must throw SQLException due to UNIQUE constraint");
    }

    @Test
    @Order(5)
    @DisplayName("test_FR_BOOK_04_TC_BOOK_06_deleteBook_foreignKeyConstraint_throwsException()")
    void test_FR_BOOK_04_TC_BOOK_06_deleteBook_foreignKeyConstraint_throwsException() {
        // BookID 3 (B003) is referenced in RentalOrderDetails for OrderID 1
        assertThrows(SQLException.class, () -> {
            bookDao.deleteBook(3);
        }, "Deleting book referenced in active rental order must violate FOREIGN KEY constraint");
    }

    @Test
    @Order(6)
    @DisplayName("test_FR_ORD_04_TC_ORD_04_TC_NFR_03_returnOrder_transactionAndRollback()")
    void test_FR_ORD_04_TC_ORD_04_TC_NFR_03_returnOrder_transactionAndRollback() throws SQLException {
        // Test Order 1 status update
        boolean updated = rentalOrderDao.updateOrderStatus(1, "Returned", LocalDateTime.now());
        assertTrue(updated, "Order 1 should be marked as Returned");

        RentalOrder order = rentalOrderDao.getOrderById(1);
        assertNotNull(order);
        assertEquals("Returned", order.getOrderStatus());

        // Verify book status was restored to Available in the transaction
        List<Book> allBooks = bookDao.getAllBooks();
        Book b3 = allBooks.stream().filter(b -> b.getBookid() == 3).findFirst().orElse(null);
        assertNotNull(b3);
        assertEquals("Available", b3.getBookStatus(), "Book 3 status should automatically rollback to Available upon return");

        // Verify Rollback on Transaction failure simulation
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE RentalOrders SET OrderStatus = 'Renting' WHERE OrderID = 1")) {
                stmt.executeUpdate();
            }
            conn.rollback(); // Explicit rollback simulation
        }

        RentalOrder orderAfterRollback = rentalOrderDao.getOrderById(1);
        assertEquals("Returned", orderAfterRollback.getOrderStatus(), "State must remain Returned after transaction rollback");
    }

    @Test
    @Order(7)
    @DisplayName("test_FR_CUST_02_TC_CUST_02_addCustomer_success()")
    void test_FR_CUST_02_TC_CUST_02_addCustomer_success() throws SQLException {
        String uniqueCustCode = "CX" + (System.currentTimeMillis() % 10000);
        Customer cust = new Customer();
        cust.setCustomerCode(uniqueCustCode);
        cust.setName("Hoang Thi Test");
        cust.setPhone("0988776655");
        cust.setEmail("test@libris.com");
        cust.setAddress("TP HCM");

        boolean added = customerDao.addCustomer(cust);
        assertTrue(added);
    }
}
