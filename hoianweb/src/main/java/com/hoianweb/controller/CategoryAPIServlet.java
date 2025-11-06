package com.hoianweb.controller;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Import Bean và Util
import com.hoianweb.model.bean.Category;
import com.hoianweb.controller.JsonUtil;

// Import DAO THẬT
import com.hoianweb.model.dao.CategoryDAO;

@WebServlet("/api/category")
public class CategoryAPIServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private CategoryDAO categoryDAO;
	@Override
	public void init() throws ServletException{
		this.categoryDAO = new CategoryDAO();
	}
	@Override
	public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		List<Category> listCat = this.categoryDAO.getAll();
		JsonUtil.sendJson(response, listCat);
	}
	
}
