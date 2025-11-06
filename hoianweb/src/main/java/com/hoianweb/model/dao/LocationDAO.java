package com.hoianweb.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hoianweb.model.bean.Location; // Import Bean Location (tiếng Anh)
import com.hoianweb.util.DBContext;       // Import DBContext

/**
 * Lớp DAO (Data Access Object) cho bảng 'location'.
 * Đây là Task 11.
 */
public class LocationDAO {

    /**
     * Hàm 1: Lấy tất cả các địa điểm.
     * @return Một List các đối tượng Location.
     */
    public List<Location> getAll() {
        List<Location> locationList = new ArrayList<>();
        String sql = "SELECT * FROM location ORDER BY name";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // Tạo đối tượng Location từ dữ liệu CSDL
                Location location = mapResultSetToLocation(rs);
                locationList.add(location);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi LocationDAO - hàm getAll(): " + e.getMessage());
            e.printStackTrace();
        }
        return locationList;
    }

    /**
     * Hàm 2: Lấy một địa điểm cụ thể bằng 'slug'.
     * 'slug' là duy nhất (unique) nên hàm này chỉ trả về 1 đối tượng.
     * @param slug Chuỗi slug (ví dụ: "chua-cau")
     * @return Một đối tượng Location hoặc null nếu không tìm thấy.
     */
    public Location getBySlug(String slug) {
        String sql = "SELECT * FROM location WHERE slug = ?";
        Location location = null;

        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, slug); // Gán tham số slug vào câu lệnh SQL

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { // Nếu tìm thấy
                    location = mapResultSetToLocation(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi LocationDAO - hàm getBySlug(): " + e.getMessage());
            e.printStackTrace();
        }
        return location;
    }

    /**
     * Hàm 3: Lấy tất cả địa điểm thuộc về một thể loại.
     * @param categoryId ID của thể loại (ví dụ: 1 cho 'Historical Site')
     * @return Một List các đối tượng Location.
     */
    public List<Location> getByCategoryId(int categoryId) {
        List<Location> locationList = new ArrayList<>();
        String sql = "SELECT * FROM location WHERE category_id = ?";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId); // Gán tham số category_id

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Location location = mapResultSetToLocation(rs);
                    locationList.add(location);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi LocationDAO - hàm getByCategoryId(): " + e.getMessage());
            e.printStackTrace();
        }
        return locationList;
    }

    /**
     * (Hàm tiện ích private)
     * Chuyển đổi dữ liệu từ ResultSet sang đối tượng Location.
     * Dùng để tránh lặp lại code ở cả 3 hàm trên.
     */
    private Location mapResultSetToLocation(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String slug = rs.getString("slug");
        double longitude = rs.getDouble("longitude");
        double latitude = rs.getDouble("latitude");
        String description = rs.getString("description");
        int categoryId = rs.getInt("category_id");

        return new Location(id, name, slug, longitude, latitude, description, categoryId);
    }
    /*
    // Dán đoạn code này vào cuối file LocationDAO.java (trước dấu } cuối)
    // Sau khi test xong có thể xóa đi
    */
    public static void main(String[] args) {
        LocationDAO dao = new LocationDAO();

        System.out.println("--- 1. Đang test hàm getAll() ---");
        List<Location> allLocations = dao.getAll();
        if (allLocations.isEmpty()) {
            System.out.println("LỖI: getAll() trả về rỗng!");
        } else {
            System.out.println("Thành công! Lấy được " + allLocations.size() + " địa điểm.");
            // In ra 1 địa điểm để kiểm tra
            System.out.println("Địa điểm đầu tiên: " + allLocations.get(0).getName()); 
        }

        System.out.println("\n--- 2. Đang test hàm getBySlug(\"chua-cau\") ---");
        Location location = dao.getBySlug("chua-cau");
        if (location == null) {
            System.out.println("LỖI: không tìm thấy slug 'chua-cau'!");
        } else {
            System.out.println("Thành công! Tìm thấy: " + location.getName());
         // Sửa dòng này
            String moTaNgan = location.getDescription();
            if (moTaNgan.length() > 20) {
                moTaNgan = moTaNgan.substring(0, 20) + "...";
            }
            System.out.println("Mô tả: " + moTaNgan);
        }

        System.out.println("\n--- 3. Đang test hàm getByCategoryId(1) ---");
        List<Location> categoryLocations = dao.getByCategoryId(1); // 1 = Historical Site
        if (categoryLocations.isEmpty()) {
            System.out.println("LỖI: không tìm thấy địa điểm nào cho Category 1!");
        } else {
            System.out.println("Thành công! Tìm thấy " + categoryLocations.size() + " địa điểm (Di Tích):");
            for (Location loc : categoryLocations) {
                System.out.println("- " + loc.getName());
            }
        }
    }
}