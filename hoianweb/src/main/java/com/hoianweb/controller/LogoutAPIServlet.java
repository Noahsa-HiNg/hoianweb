package com.hoianweb.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;     

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 



@WebServlet("/api/admin/logout")
public class LogoutAPIServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {

            HttpSession session = request.getSession(false); 


            if (session != null) {

                session.invalidate();
            }


            Map<String, String> message = new HashMap<>();
            message.put("message", "Đã đăng xuất thành công");
            
            response.setStatus(HttpServletResponse.SC_OK);
            JsonUtil.sendJson(response, message);
            
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi đăng xuất");
        }
    }
}