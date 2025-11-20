package com.hoianweb.controller;

import com.hoianweb.controller.JsonUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
 * AdminAuthFilter protects:
 *  - admin UI: /admin and /admin/*
 *  - admin APIs: /api/admin/*
 *
 * Behavior:
 *  - If an unauthenticated browser request targets the admin UI, redirect to /login.
 *  - If an unauthenticated request targets admin APIs (/api/admin/*), return 401 JSON.
 *  - Allow /api/admin/login through so login can succeed.
 */
@WebFilter(urlPatterns = { "/admin", "/admin/*", "/api/admin/*" })
public class AdminAuthFilter implements Filter {

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        // no-op
    }

    @Override
    public void doFilter(
        ServletRequest req,
        ServletResponse res,
        FilterChain chain
    ) throws IOException, ServletException {
        if (
            !(req instanceof HttpServletRequest) ||
            !(res instanceof HttpServletResponse)
        ) {
            chain.doFilter(req, res);
            return;
        }

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI(); // full URI including context
        String path = requestURI.substring(contextPath.length()); // path starts with '/'

        // Allow login endpoint to pass through
        if (path.equals("/api/admin/login")) {
            chain.doFilter(request, response);
            return;
        }

        // If session exists and contains admin attribute, continue
        HttpSession session = request.getSession(false);
        boolean authenticated = (session != null &&
            session.getAttribute("admin") != null);

        // Requests to admin UI (/admin or /admin/...) should redirect to login if not authenticated
        if (path.equals("/admin") || path.startsWith("/admin/")) {
            if (!authenticated) {
                // Browser redirect to login page
                response.sendRedirect(contextPath + "/login");
                return;
            } else {
                chain.doFilter(request, response);
                return;
            }
        }

        // Requests to admin APIs should return 401 JSON if not authenticated
        if (path.startsWith("/api/admin")) {
            if (!authenticated) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                Map<String, String> m = new HashMap<>();
                m.put("message", "Cần đăng nhập");
                JsonUtil.sendJson(response, m);
                return;
            } else {
                chain.doFilter(request, response);
                return;
            }
        }

        // For safety, allow other requests to pass
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // no-op
    }
}
