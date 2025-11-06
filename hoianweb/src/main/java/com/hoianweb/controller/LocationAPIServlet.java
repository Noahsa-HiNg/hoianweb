package com.hoianweb.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Import Mock DAO (chúng ta sẽ thay bằng DAO thật sau)
import com.hoianweb.model.bean.Location;
import com.hoianweb.model.dao.CategoryDAO;
import com.hoianweb.model.dao.ImageDAO;
import com.hoianweb.model.dao.LocationDAO;
import com.hoianweb.controller.JsonUtil; // Import file tiện ích của bạn
import com.hoianweb.model.bean.Category;
import com.hoianweb.model.bean.Image;

@WebServlet("/api/diadiem/*")
public class LocationAPIServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private LocationDAO locationDAO;
    private CategoryDAO categoryDAO;
    private ImageDAO imageDAO;
    @Override
    public void init() throws ServletException {
        this.locationDAO = new LocationDAO();
        this.categoryDAO = new CategoryDAO();
        this.imageDAO = new ImageDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	String pathInfo = request.getPathInfo();
    	if (pathInfo == null || pathInfo == "/") {
    		String cateId = request.getParameter("theloai");
    		List<Location> fullList;
    		if (cateId != null) {
    			try {
    				int categoryId = Integer.parseInt(cateId);
        			fullList = this.locationDAO.getByCategoryId(categoryId);
    			}catch (NumberFormatException e) {
                    fullList = new ArrayList<>(); 
                } 			
    		} 
    		else {
    			fullList = this.locationDAO.getAll();
    		}
    		List<Location> mapList = new ArrayList<>();
    		for (Location Loc:fullList) {
    			Location mapLoc = new Location();
    			mapLoc.setSlug(Loc.getSlug());
    			mapLoc.setName(Loc.getName());
    			mapLoc.setLongitude(Loc.getLongitude());
    			mapLoc.setLatitude(Loc.getLatitude());
                mapList.add(mapLoc);
    		}
    		JsonUtil.sendJson(response, mapList);
    		
    	}else {
    		try {
    		String slug = pathInfo.substring(1);
    		Location loca = locationDAO.getBySlug(slug);
    		if (loca!=null) {
    			List<Image> gallery = this.imageDAO.getByLocationId(loca.getId());
    			loca.setGallery(gallery);
    			List<Category> allCategories = this.categoryDAO.getAll();
                for (Category cat : allCategories) {
                    if (cat.getId() == loca.getCategoryId()) {
                        loca.setCategoryName(cat.getName());
                        break;
                    }
                }
                JsonUtil.sendJson(response, loca);
    		}else
    		{response.sendError(HttpServletResponse.SC_NOT_FOUND, "Location not found");}
    		}catch (Exception e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL format");
                e.printStackTrace();
            }
    		
    	}
    }
}