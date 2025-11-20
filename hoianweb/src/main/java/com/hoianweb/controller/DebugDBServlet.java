package com.hoianweb.controller;

import com.google.gson.Gson;
import com.hoianweb.model.bean.Category;
import com.hoianweb.model.bean.Image;
import com.hoianweb.model.bean.Location;
import com.hoianweb.model.dao.CategoryDAO;
import com.hoianweb.model.dao.ImageDAO;
import com.hoianweb.model.dao.LocationDAO;
import com.hoianweb.util.DBContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Temporary debug servlet.
 *
 * Purpose:
 * - Query the database via existing DAOs and print diagnostics to the server console.
 * - Return a compact JSON payload to the HTTP client containing counts and a few sample rows.
 *
 * Usage (temporary): GET /debug/db
 *
 * Remove or disable this servlet before deploying to production.
 */
@WebServlet("/debug/db")
public class DebugDBServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private LocationDAO locationDAO;
    private CategoryDAO categoryDAO;
    private ImageDAO imageDAO;
    private final Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        super.init();
        this.locationDAO = new LocationDAO();
        this.categoryDAO = new CategoryDAO();
        this.imageDAO = new ImageDAO();
        System.out.println("[DebugDBServlet] initialized");

        // DB diagnostics: attempt to open a raw connection and print metadata + raw counts
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBContext.getConnection();
            if (conn != null) {
                System.out.println(
                    "[DebugDBServlet] DB catalog: " + conn.getCatalog()
                );
                DatabaseMetaData md = conn.getMetaData();
                System.out.println(
                    "[DebugDBServlet] DB product: " +
                        md.getDatabaseProductName() +
                        " " +
                        md.getDatabaseProductVersion()
                );
                System.out.println("[DebugDBServlet] DB URL: " + md.getURL());
                System.out.println(
                    "[DebugDBServlet] DB user: " + md.getUserName()
                );

                stmt = conn.createStatement();

                try {
                    rs = stmt.executeQuery("SELECT COUNT(*) FROM category");
                    if (rs.next()) System.out.println(
                        "[DebugDBServlet] RAW category COUNT = " + rs.getInt(1)
                    );
                    rs.close();
                } catch (SQLException e) {
                    System.err.println(
                        "[DebugDBServlet] Error counting category: " +
                            e.getMessage()
                    );
                }

                try {
                    rs = stmt.executeQuery("SELECT COUNT(*) FROM location");
                    if (rs.next()) System.out.println(
                        "[DebugDBServlet] RAW location COUNT = " + rs.getInt(1)
                    );
                    rs.close();
                } catch (SQLException e) {
                    System.err.println(
                        "[DebugDBServlet] Error counting location: " +
                            e.getMessage()
                    );
                }

                try {
                    rs = stmt.executeQuery("SELECT COUNT(*) FROM image");
                    if (rs.next()) System.out.println(
                        "[DebugDBServlet] RAW image COUNT = " + rs.getInt(1)
                    );
                    rs.close();
                } catch (SQLException e) {
                    System.err.println(
                        "[DebugDBServlet] Error counting image: " +
                            e.getMessage()
                    );
                }
            } else {
                System.err.println(
                    "[DebugDBServlet] DB connection returned null"
                );
            }
        } catch (Exception e) {
            System.err.println(
                "[DebugDBServlet] Error during DB diagnostics: " +
                    e.getMessage()
            );
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception ex) {}
            try {
                if (stmt != null) stmt.close();
            } catch (Exception ex) {}
            try {
                if (conn != null) conn.close();
            } catch (Exception ex) {}
        }
    }

    @Override
    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        Map<String, Object> payload = new HashMap<>();
        PrintWriter out = response.getWriter();

        try {
            // Fetch categories
            List<Category> categories = categoryDAO.getAll();
            int catCount = categories == null ? 0 : categories.size();
            System.out.println(
                "[DebugDBServlet] categories count = " + catCount
            );
            // Log first few categories
            if (categories != null) {
                for (int i = 0; i < Math.min(10, categories.size()); i++) {
                    Category c = categories.get(i);
                    System.out.println(
                        String.format(
                            "[DebugDBServlet] cat[%d] id=%d name=%s",
                            i,
                            c.getId(),
                            c.getName()
                        )
                    );
                }
            }

            // Fetch locations
            List<Location> locations = locationDAO.getAll();
            int locCount = locations == null ? 0 : locations.size();
            System.out.println(
                "[DebugDBServlet] locations count = " + locCount
            );
            // Log first few locations
            if (locations != null) {
                for (int i = 0; i < Math.min(10, locations.size()); i++) {
                    Location l = locations.get(i);
                    System.out.println(
                        String.format(
                            "[DebugDBServlet] loc[%d] id=%d name=%s slug=%s categoryId=%d lat=%s lon=%s",
                            i,
                            l.getId(),
                            l.getName(),
                            l.getSlug(),
                            l.getCategoryId(),
                            String.valueOf(l.getLatitude()),
                            String.valueOf(l.getLongitude())
                        )
                    );
                }
            }

            // Fetch first-image map (if available)
            Map<Integer, String> firstImageMap = null;
            try {
                firstImageMap = imageDAO.getFirstImageMap();
                System.out.println(
                    "[DebugDBServlet] firstImageMap size = " +
                        (firstImageMap == null ? 0 : firstImageMap.size())
                );
            } catch (Throwable t) {
                System.err.println(
                    "[DebugDBServlet] error while fetching firstImageMap: " +
                        t.getMessage()
                );
                t.printStackTrace();
            }

            // Also try to fetch some images as sample for a location (if exist)
            List<Image> sampleGallery = null;
            if (locations != null && !locations.isEmpty()) {
                try {
                    int sampleLocationId = locations.get(0).getId();
                    sampleGallery = imageDAO.getByLocationId(sampleLocationId);
                    System.out.println(
                        "[DebugDBServlet] sampleGallery for locationId=" +
                            sampleLocationId +
                            " size=" +
                            (sampleGallery == null ? 0 : sampleGallery.size())
                    );
                    if (sampleGallery != null) {
                        for (
                            int i = 0;
                            i < Math.min(5, sampleGallery.size());
                            i++
                        ) {
                            Image im = sampleGallery.get(i);
                            System.out.println(
                                String.format(
                                    "[DebugDBServlet] img[%d] id=%d url=%s locationId=%d",
                                    i,
                                    im.getId(),
                                    im.getImageUrl(),
                                    im.getLocationId()
                                )
                            );
                        }
                    }
                } catch (Throwable t) {
                    System.err.println(
                        "[DebugDBServlet] error while fetching sample gallery: " +
                            t.getMessage()
                    );
                    t.printStackTrace();
                }
            }

            // Prepare response payload (keep it small)
            payload.put("categoriesCount", catCount);
            payload.put("locationsCount", locCount);
            payload.put(
                "sampleCategories",
                categories == null
                    ? null
                    : categories.subList(0, Math.min(10, categories.size()))
            );
            payload.put(
                "sampleLocations",
                locations == null
                    ? null
                    : locations.subList(0, Math.min(10, locations.size()))
            );
            payload.put("firstImageMapSample", firstImageMap);
            payload.put(
                "sampleGallery",
                sampleGallery == null
                    ? null
                    : sampleGallery.subList(
                          0,
                          Math.min(5, sampleGallery.size())
                      )
            );

            String json = gson.toJson(payload);
            out.print(json);
            out.flush();

            System.out.println("[DebugDBServlet] response sent successfully");
        } catch (Exception e) {
            System.err.println(
                "[DebugDBServlet] Unexpected error: " + e.getMessage()
            );
            e.printStackTrace();
            payload.clear();
            payload.put("error", "Unexpected server error: " + e.getMessage());
            out.print(gson.toJson(payload));
            out.flush();
        } finally {
            out.close();
        }
    }

    @Override
    public void destroy() {
        System.out.println("[DebugDBServlet] destroyed");
        super.destroy();
    }
}
