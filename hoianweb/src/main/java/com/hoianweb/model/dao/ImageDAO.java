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
        // Cập nhật tên bảng và cột
        String sql = "SELECT * FROM image WHERE location_id = ?"; 

        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, locationId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String url = rs.getString("image_url"); // Cập nhật tên cột
                    
                    Image img = new Image(id, url, locationId);
                    imageList.add(img);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return imageList;
    }
}