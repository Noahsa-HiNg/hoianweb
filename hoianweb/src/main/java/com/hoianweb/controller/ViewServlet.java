package com.hoianweb.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/index", "/about", "/blog", "/goldentemple-info", "/recent-announcement", "/signup", "/login", "/admin"})
public class ViewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        // Check if the request is for a static resource
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/img/") || path.startsWith("/fonts/") || path.startsWith("/scss/") || path.startsWith("/vendor/") || path.startsWith("/bootstrap/") || path.startsWith("/Pages-inside/")) {
            // Forward to the default servlet for static resource handling
            getServletContext().getNamedDispatcher("default").forward(request, response);
            return;
        }

        // If not a static resource, handle as a page request
        String viewPath = "/WEB-INF/views/";
        switch (path) {
            case "/":
            case "/index":
                viewPath += "index.jsp";
                break;
            case "/about":
                viewPath += "Pages-inside/about.jsp";
                break;
            case "/blog":
                viewPath += "Pages-inside/blog.jsp";
                break;
            case "/goldentemple-info":
                viewPath += "Pages-inside/goldentemple-info.jsp";
                break;
            case "/recent-announcement":
                viewPath += "Pages-inside/recent-announcement.jsp";
                break;
            case "/signup":
                viewPath += "Pages-inside/signup.jsp";
                break;
            case "/login":
                viewPath += "login.jsp";
                break;
            case "/admin":
                viewPath += "admin.jsp";
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
        }
        request.getRequestDispatcher(viewPath).forward(request, response);
    }
}
