package com.hoianweb.controller; // Hoặc package .util

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Filter này sẽ "chặn" tất cả các URL bắt đầu bằng /api/admin/
 * TRỪ URL login
 */
@WebFilter("/api/admin/*")
public class AdminAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Lấy URI (ví dụ: /hoianweb/api/admin/login)
        String requestURI = request.getRequestURI();

        // 1. CHO PHÉP ĐI QUA nếu là API Đăng nhập
        if (requestURI.endsWith("/api/admin/login")) {
            chain.doFilter(request, response); 
            return;
        }
        HttpSession session = request.getSession(false); 

        if (session != null && session.getAttribute("admin") != null) {
            // HỢP LỆ: Đã đăng nhập
            chain.doFilter(request, response); // Cho đi tiếp
        } else {
            // KHÔNG HỢP LỆ: Chưa đăng nhập
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bạn cần đăng nhập để thực hiện hành động này");
        }
    }
    @Override public void init(FilterConfig fConfig) throws ServletException {}
    @Override public void destroy() {}
}