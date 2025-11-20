package com.hoianweb.controller;

import com.hoianweb.controller.JsonUtil;
// Import Bean và Util
import com.hoianweb.model.bean.Category;
// Import DAO THẬT
import com.hoianweb.model.dao.CategoryDAO;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/theloai")
public class CategoryAPIServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        this.categoryDAO = new CategoryDAO();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // Debug logging to server console to verify DB access and returned data
        System.out.println("[CategoryAPIServlet] GET /api/theloai called");
        List<Category> listCat = this.categoryDAO.getAll();
        System.out.println(
            "[CategoryAPIServlet] categories fetched: count=" +
                (listCat == null ? 0 : listCat.size())
        );
        if (listCat != null) {
            for (int i = 0; i < Math.min(10, listCat.size()); i++) {
                Category c = listCat.get(i);
                try {
                    System.out.println(
                        "[CategoryAPIServlet] cat[" +
                            i +
                            "] id=" +
                            c.getId() +
                            ", name=" +
                            c.getName()
                    );
                } catch (Exception e) {
                    System.out.println(
                        "[CategoryAPIServlet] Error logging category at index " +
                            i +
                            ": " +
                            e.getMessage()
                    );
                }
            }
        }
        JsonUtil.sendJson(response, listCat);
    }
}
