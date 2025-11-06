package com.hoianweb.controller;

import java.io.IOException;
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
}