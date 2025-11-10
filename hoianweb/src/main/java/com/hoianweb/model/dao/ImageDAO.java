package com.hoianweb.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.hoianweb.model.bean.Image;
import com.hoianweb.util.DBContext;

public class ImageDAO {

    public List<Image> getByLocationId(int locationId) {
        List<Image> imageList = new ArrayList<>();
        String sql = "SELECT * FROM image WHERE location_id = ?"; 
        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, locationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    imageList.add(new Image(rs.getInt("id"), rs.getString("image_url"), locationId));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return imageList;
    }

    // BỔ SUNG HÀM NÀY ĐỂ HOÀN THIỆN CHỨC NĂNG UP ẢNH
    public boolean create(Image image) {
        String sql = "INSERT INTO image (image_url, location_id) VALUES (?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, image.getImageUrl());
            pstmt.setInt(2, image.getLocationId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
    
     public boolean delete(int id) {
        String sql = "DELETE FROM image WHERE id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}