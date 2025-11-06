package com.hoianweb.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lớp tiện ích (Utility Class) để quản lý kết nối CSDL.
 * Nhiệm vụ: Cung cấp một phương thức tĩnh để lấy kết nối đến CSDL MySQL.
 */
public class DBContext {

    // Thông tin CSDL của bạn trên XAMPP
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hoian_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = ""; // Mật khẩu root của XAMPP (mặc định là rỗng)

    /**
     * Phương thức tĩnh (static) để lấy kết nối đến CSDL.
     * Các lớp DAO sẽ gọi hàm này.
     * * @return Một đối tượng Connection.
     * @throws SQLException Nếu không thể kết nối.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            // 1. Nạp driver MySQL
            // (Đảm bảo bạn đã thêm mysql-connector-java vào pom.xml)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 2. Tạo kết nối
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Lỗi DBContext: Không thể kết nối CSDL.");
            e.printStackTrace();
            // Ném lỗi ra ngoài để lớp gọi (DAO) có thể xử lý
            throw new SQLException("Không thể kết nối CSDL: " + e.getMessage());
        }
        return conn;
    }
}