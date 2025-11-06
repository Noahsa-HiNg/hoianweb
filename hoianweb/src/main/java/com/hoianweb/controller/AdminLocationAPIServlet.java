package com.hoianweb.controller;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Import Bean, Util và DAO
import com.hoianweb.model.bean.Location;
import com.hoianweb.controller.JsonUtil;
import com.hoianweb.model.dao.LocationDAO;
@WebServlet("/api/admin/location/*")
public class AdminLocationAPIServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

    private LocationDAO locationDAO;

    @Override
    public void init() {
        this.locationDAO = new LocationDAO();
    }
    
}
