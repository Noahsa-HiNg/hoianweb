package com.hoianweb.controller;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File; // <-- Thêm import này
import java.util.List;
// Import Bean, Util và DAO
import com.hoianweb.model.bean.Location;
import com.hoianweb.controller.JsonUtil;
import com.hoianweb.model.dao.LocationDAO;
@WebServlet("/api/admin/diadiem/*")
public class AdminLocationAPIServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

    private LocationDAO locationDAO;

    @Override
    public void init() {
        this.locationDAO = new LocationDAO();
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // 1. Đọc JSON từ body ({"name":"...", "slug":"..."...})
            Location newLocation = JsonUtil.readJson(request, Location.class);
            int newId = this.locationDAO.create(newLocation);

            if (newId != -1) {
                newLocation.setId(newId);

                response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
                JsonUtil.sendJson(response, newLocation);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể tạo địa điểm");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dữ liệu JSON không hợp lệ");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 1. Lấy ID từ URL
            String pathInfo = request.getPathInfo(); // Sẽ là "/5"
            int id = Integer.parseInt(pathInfo.substring(1));

            // 2. Đọc JSON dữ liệu mới
            Location updatedLocation = JsonUtil.readJson(request, Location.class);
            updatedLocation.setId(id); // Gán ID vào object
            
            // 3. Gọi DAO (hàm update của bạn dùng ID)
            boolean success = this.locationDAO.update(updatedLocation); 

            if (success) {
                JsonUtil.sendJson(response, updatedLocation);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy địa điểm để cập nhật");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ hoặc dữ liệu JSON sai");
        }
    }
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        try {

            int id = Integer.parseInt(request.getPathInfo().substring(1));
            List<String> deletedImageUrls = this.locationDAO.delete(id); 
            
            if (deletedImageUrls != null) {
                for (String imageUrl : deletedImageUrls) {
                    deleteFileFromServer(imageUrl, request);
                }
                JsonUtil.sendJson(response, "{\"message\": \"Xóa địa điểm (ID: " + id + ") và các ảnh thành công\"}");
                
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy địa điểm để xóa");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteFileFromServer(String imageUrl, HttpServletRequest request) {
        try {
            String contextPath = request.getContextPath();
            String filePath = imageUrl.substring(contextPath.length());
            String realPath = getServletContext().getRealPath(filePath);
            
            if (realPath != null) {
                File fileToDelete = new File(realPath);
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                    System.out.println("Đã xóa file: " + realPath); // Ghi log
                } else {
                    System.out.println("Không tìm thấy file để xóa: " + realPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi xóa file: " + imageUrl);
        }
    }
}
