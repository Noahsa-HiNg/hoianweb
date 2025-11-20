package com.hoianweb.controller;

import com.hoianweb.model.bean.Image;
import com.hoianweb.model.dao.ImageDAO;
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

//import com.hoianweb.util.JsonUtil;

@WebServlet("/api/admin/image/upload")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 10, // 10MB
    maxRequestSize = 1024 * 1024 * 50 // 50MB
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
    protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        // Filter (AdminAuthFilter) đã kiểm tra đăng nhập cho chúng ta
        request.setCharacterEncoding("UTF-8"); // Đảm bảo đọc tên file UTF-8

        try {
            // --- 1. LẤY DỮ LIỆU ---

            // Lấy ID của địa điểm (dưới dạng text)
            String locationIdStr = request.getParameter("location_id");
            if (locationIdStr == null) {
                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Thiếu 'location_id'"
                );
                return;
            }

            int locationId = Integer.parseInt(locationIdStr);

            // Lấy file nhị phân (binary)
            Part filePart = request.getPart("image_file");
            if (filePart == null) {
                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Thiếu 'image_file'"
                );
                return;
            }

            // --- 2. XỬ LÝ TÊN FILE (Để tránh trùng lặp) ---
            String originalFileName = filePart.getSubmittedFileName();
            // an toàn khi không có extension hoặc tên null
            String fileExtension = "";
            if (originalFileName != null) {
                int dot = originalFileName.lastIndexOf('.');
                if (dot >= 0 && dot < originalFileName.length() - 1) {
                    fileExtension = originalFileName.substring(dot);
                }
            }
            String uniqueFileName =
                UUID.randomUUID().toString() + fileExtension;

            // --- 3. LƯU FILE VÀO SERVER ---
            String uploadPath = UPLOAD_DIRECTORY;
            File uploadDir = new File(uploadPath);
            // tạo nhiều cấp nếu cần, kiểm tra quyền ghi
            if (!uploadDir.exists()) {
                if (!uploadDir.mkdirs()) {
                    response.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Không thể tạo thư mục lưu file"
                    );
                    return;
                }
            }
            if (!uploadDir.canWrite()) {
                response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Không có quyền ghi vào thư mục uploads"
                );
                return;
            }

            // Ghi file (dạng nhị phân) vào ổ đĩa server - ghi vào file đích và bảo đảm xóa khi lỗi xảy ra sau đó
            File destFile = new File(uploadDir, uniqueFileName);
            try {
                filePart.write(destFile.getAbsolutePath());
            } catch (IOException ioe) {
                // Nếu có file partial, cố gắng xóa
                try {
                    if (destFile.exists()) destFile.delete();
                } catch (Exception ignore) {}
                throw ioe;
            }

            // --- 4. LƯU ĐƯỜNG DẪN VÀO CSDL ---
            // Store a context-independent public path so DB contains '/uploads/{file}'
            // (the serving side will either use a junction or the UploadsServlet).
            String dbPath = "/uploads/" + uniqueFileName;

            Image newImage = new Image(dbPath, locationId);

            // Dùng hàm create() trả về int (ID mới). Nếu lưu DB thất bại, xóa file đã ghi để tránh rác trên ổ đĩa.
            int newId = imageDAO.create(newImage);
            if (newId == -1) {
                // cố gắng xóa file đã ghi
                try {
                    if (destFile.exists()) destFile.delete();
                } catch (Exception ignore) {}
                response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Không thể lưu thông tin ảnh vào CSDL"
                );
                return;
            }
            newImage.setId(newId);

            // --- 5. TRẢ VỀ JSON ---
            response.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtil.sendJson(response, newImage);
        } catch (NumberFormatException e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "'location_id' phải là một con số"
            );
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Lỗi khi tải file lên"
            );
        }
    }
}
