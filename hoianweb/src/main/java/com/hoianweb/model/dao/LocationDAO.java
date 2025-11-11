package com.hoianweb.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hoianweb.model.bean.Image;
import com.hoianweb.model.bean.Location;
import com.hoianweb.util.DBContext;

public class LocationDAO {

	private ImageDAO imageDAO = new ImageDAO();
    private static final String SELECT_BASE = 
        "SELECT l.*, c.name AS category_name " +
        "FROM location l " +
        "LEFT JOIN category c ON l.category_id = c.id ";

    public List<Location> getAll() {
        List<Location> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY l.name";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // CHỈ DÙNG MAPPER CƠ BẢN (KHÔNG GÂY LỖI N+1)
                list.add(mapResultSetToLocation_Basic(rs)); 
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return list;
    }

    /**
     * Sửa lỗi: Gọi mapResultSetToLocation_Full
     */
    public Location getBySlug(String slug) {
        String sql = SELECT_BASE + "WHERE l.slug = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, slug);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // DÙNG MAPPER ĐẦY ĐỦ (CÓ GALLERY)
                    return mapResultSetToLocation_Full(rs); 
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * Sửa lỗi: Gọi mapResultSetToLocation_Basic
     */
    public List<Location> getByCategoryId(int categoryId) {
        List<Location> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE l.category_id = ? ORDER BY l.name";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLocation_Basic(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int create(Location loc) {
        String sql = "INSERT INTO location (name, slug, longitude, latitude, description, category_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, loc.getName());
            pstmt.setString(2, loc.getSlug());
            pstmt.setDouble(3, loc.getLongitude());
            pstmt.setDouble(4, loc.getLatitude());
            pstmt.setString(5, loc.getDescription());
            pstmt.setInt(6, loc.getCategoryId());
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
    
    public boolean update(Location loc) {
        String sql = "UPDATE location SET name=?, slug=?, longitude=?, latitude=?, description=?, category_id=? WHERE id=?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, loc.getName());
            pstmt.setString(2, loc.getSlug());
            pstmt.setDouble(3, loc.getLongitude());
            pstmt.setDouble(4, loc.getLatitude());
            pstmt.setString(5, loc.getDescription());
            pstmt.setInt(6, loc.getCategoryId());
            pstmt.setInt(7, loc.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<String> delete(int id) {
    	List<String> urlsToDelete = imageDAO.getUrlsByLocationId(id);
        String sql = "DELETE FROM location WHERE id = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            if( pstmt.executeUpdate() > 0) {
            	return urlsToDelete;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private Location mapResultSetToLocation_Basic(ResultSet rs) throws SQLException {
        Location loc = new Location(
            rs.getInt("id"), rs.getString("name"), rs.getString("slug"),
            rs.getDouble("longitude"), rs.getDouble("latitude"),
            rs.getString("description"), rs.getInt("category_id")
        );
        loc.setCategoryName(rs.getString("category_name"));
        return loc;
    }

    private Location mapResultSetToLocation_Full(ResultSet rs) throws SQLException {

        Location loc = mapResultSetToLocation_Basic(rs);
        List<Image> gallery = imageDAO.getByLocationId(loc.getId());
        loc.setGallery(gallery);
        
        return loc;
    }
}