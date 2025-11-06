package com.hoianweb.controller;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig; // <-- BẮT BUỘC
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part; // <-- Dùng 'Part' để lấy file

import com.hoianweb.model.bean.Image;
import com.hoianweb.model.bean.Location;
import com.hoianweb.model.dao.ImageDAO;
import com.hoianweb.model.dao.LocationDAO;
import com.hoianweb.controller.JsonUtil;
@WebServlet("/api/admin/image/upload")
@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB (Kích thước tạm)
	    maxFileSize = 1024 * 1024 * 10, // 10MB (Kích thước file tối đa)
	    maxRequestSize = 1024 * 1024 * 50 // 50MB (Tổng kích thước request)
	)
public class ImageUploadServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

    private ImageDAO imageDAO;
    private LocationDAO locationDAO;

    @Override
    public void init() {
        this.imageDAO = new ImageDAO();
        this.locationDAO = new LocationDAO();
    }
}
