package com.hoianweb.model.dao;

import com.hoianweb.model.bean.Image;
import com.hoianweb.util.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageDAO {

    public List<Image> getByLocationId(int locationId) {
        List<Image> imageList = new ArrayList<>();
        String sql = "SELECT * FROM image WHERE location_id = ?";
        try (
            Connection conn = DBContext.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, locationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    imageList.add(
                        new Image(
                            rs.getInt("id"),
                            rs.getString("image_url"),
                            locationId
                        )
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return imageList;
    }

    public List<String> getUrlsByLocationId(int locationId) {
        List<String> urlList = new ArrayList<>();
        String sql = "SELECT image_url FROM image WHERE location_id = ?";
        try (
            Connection conn = DBContext.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, locationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    urlList.add(rs.getString("image_url"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return urlList;
    }

    // HÀM TỐI ƯU (Dùng cho LocationDAO.getAll)
    public Map<Integer, String> getFirstImageMap() {
        Map<Integer, String> imageMap = new HashMap<>();
        String sql =
            " SELECT location_id, image_url FROM (" +
            "    SELECT location_id, image_url," +
            "           ROW_NUMBER() OVER(PARTITION BY location_id ORDER BY id ASC) as rn" +
            "    FROM image" +
            " ) AS RankedImages" +
            " WHERE rn = 1";

        try (
            Connection conn = DBContext.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                imageMap.put(
                    rs.getInt("location_id"),
                    rs.getString("image_url")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return imageMap;
    }

    //Hàm create trả về int
    public int create(Image image) {
        String sql = "INSERT INTO image (image_url, location_id) VALUES (?, ?)";
        try (
            Connection conn = DBContext.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
            )
        ) {
            pstmt.setString(1, image.getImageUrl());
            pstmt.setInt(2, image.getLocationId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM image WHERE id = ?";
        try (
            Connection conn = DBContext.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete a single image record by its location id and image_url.
     * Returns true when a row was deleted.
     */
    public boolean deleteByLocationIdAndUrl(int locationId, String imageUrl) {
        String sql =
            "DELETE FROM image WHERE location_id = ? AND image_url = ?";
        try (
            Connection conn = DBContext.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, locationId);
            pstmt.setString(2, imageUrl);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
