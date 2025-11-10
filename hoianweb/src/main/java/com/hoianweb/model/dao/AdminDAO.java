package com.hoianweb.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.mindrot.jbcrypt.BCrypt; 
import com.hoianweb.model.bean.Admin;
import com.hoianweb.util.DBContext;

public class AdminDAO {
    public Admin checkLogin(String username, String password) {
        String sql = "SELECT * FROM admin WHERE username = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()){
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    // Dùng BCrypt để kiểm tra mật khẩu
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        return new Admin(rs.getInt("id"), username);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();    
        }
        return null;
    }
}