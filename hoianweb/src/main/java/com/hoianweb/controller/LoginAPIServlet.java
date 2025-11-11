package com.hoianweb.controller;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; // <-- Import Session

import com.google.gson.Gson;
import com.hoianweb.model.bean.Admin;
import com.hoianweb.model.dao.AdminDAO;
import com.hoianweb.controller.JsonUtil;
@WebServlet("/api/admin/login")
public class LoginAPIServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
    private AdminDAO adminDAO;
    private Gson gson = new Gson();
    @Override
    public void init() {
        this.adminDAO = new AdminDAO();
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String user = request.getParameter("username");
        String pass = request.getParameter("password");

        Admin admin = adminDAO.checkLogin(user, pass);

        if (admin != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("admin", admin); 
            JsonUtil.sendJson(response, admin);
            
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Username hoặc mật khẩu không đúng");
        }
    }

}
