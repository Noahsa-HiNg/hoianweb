package com.hoianweb.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID; // Dùng để tạo tên file duy nhất
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.hoianweb.model.bean.Image;
import com.hoianweb.model.dao.ImageDAO;
//import com.hoianweb.util.JsonUtil;

@WebServlet("/api/admin/image/upload")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,    // 1MB
    maxFileSize = 1024 * 1024 * 10,   // 10MB
    maxRequestSize = 1024 * 1024 * 50  // 50MB
)
public class ImageUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIRECTORY = "D:/hoian_uploads";
    private ImageDAO imageDAO;

    @Override
    public void init() {
        this.imageDAO = new ImageDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Filter (AdminAuthFilter) đã kiểm tra đăng nhập cho chúng ta
        request.setCharacterEncoding("UTF-8"); // Đảm bảo đọc tên file UTF-8
        
        try {
            // --- 1. LẤY DỮ LIỆU ---
            
            // Lấy ID của địa điểm (dưới dạng text)
            String locationIdStr = request.getParameter("location_id");
            if (locationIdStr == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu 'location_id'");
                return;
            }
            
            int locationId = Integer.parseInt(locationIdStr);
            
            // Lấy file nhị phân (binary)
            Part filePart = request.getPart("image_file");
            if (filePart == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu 'image_file'");
                return;
            }

            // --- 2. XỬ LÝ TÊN FILE (Để tránh trùng lặp) ---
            String originalFileName = filePart.getSubmittedFileName();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // --- 3. LƯU FILE VÀO SERVER ---
            String uploadPath = UPLOAD_DIRECTORY; 
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdir();

            // Ghi file (dạng nhị phân) vào ổ đĩa server
            filePart.write(uploadPath + File.separator + uniqueFileName);

            // --- 4. LƯU ĐƯỜNG DẪN VÀO CSDL ---
            String dbPath = "/hoianweb/uploads/" + uniqueFileName;
            
            Image newImage = new Image(dbPath, locationId);
            
            // Dùng hàm create() trả về int (ID mới)
            int newId = imageDAO.create(newImage); 
            newImage.setId(newId);

            // --- 5. TRẢ VỀ JSON ---
            response.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtil.sendJson(response, newImage);

        } catch (NumberFormatException e) {
             response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'location_id' phải là một con số");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi tải file lên");
        }
    }
}