package com.hoianweb.controller;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;

public class JsonUtil {
    
    private static final Gson gson = new Gson();

    // Hàm này sẽ chuyển bất kỳ object Java nào thành JSON và gửi đi
    public static void sendJson(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(data));
    }
    public static <T> T readJson(HttpServletRequest request, Class<T> classOfT) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        
        // Dùng Gson để chuyển chuỗi JSON thành đối tượng Java
        return gson.fromJson(sb.toString(), classOfT);
    }
}