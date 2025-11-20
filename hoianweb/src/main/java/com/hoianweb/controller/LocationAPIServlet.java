package com.hoianweb.controller;

import com.hoianweb.controller.JsonUtil; // Import file tiện ích của bạn
import com.hoianweb.model.bean.Category;
import com.hoianweb.model.bean.Image;
// Import Mock DAO (chúng ta sẽ thay bằng DAO thật sau)
import com.hoianweb.model.bean.Location;
import com.hoianweb.model.dao.CategoryDAO;
import com.hoianweb.model.dao.ImageDAO;
import com.hoianweb.model.dao.LocationDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/diadiem/*")
public class LocationAPIServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private LocationDAO locationDAO;
    private CategoryDAO categoryDAO;
    private ImageDAO imageDAO;

    @Override
    public void init() throws ServletException {
        this.locationDAO = new LocationDAO();
        this.categoryDAO = new CategoryDAO();
        this.imageDAO = new ImageDAO();
    }

    @Override
    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            String cateId = request.getParameter("theloai");
            System.out.println(
                "[LocationAPIServlet] GET list - theloai param: " + cateId
            );
            List<Location> fullList;
            if (cateId != null) {
                try {
                    int categoryId = Integer.parseInt(cateId);
                    fullList = this.locationDAO.getByCategoryId(categoryId);
                } catch (NumberFormatException e) {
                    fullList = new ArrayList<>();
                }
            } else {
                fullList = this.locationDAO.getAll();
            }

            // Debug: log number of records and some basic info to server console
            try {
                if (fullList != null) {
                    System.out.println(
                        "[LocationAPIServlet] fetched fullList size = " +
                            fullList.size()
                    );
                    for (Location debugLoc : fullList) {
                        System.out.println(
                            String.format(
                                "[LocationAPIServlet] loc id=%d name=%s slug=%s categoryId=%d",
                                debugLoc.getId(),
                                debugLoc.getName(),
                                debugLoc.getSlug(),
                                debugLoc.getCategoryId()
                            )
                        );
                    }
                } else {
                    System.out.println(
                        "[LocationAPIServlet] fetched fullList is null"
                    );
                }
            } catch (Exception e) {
                System.err.println(
                    "[LocationAPIServlet] Error while logging fullList: " +
                        e.getMessage()
                );
                e.printStackTrace();
            }

            java.util.Map<Integer, String> firstImageMap =
                this.imageDAO.getFirstImageMap();
            List<Location> mapList = new ArrayList<>();
            for (Location Loc : fullList) {
                Location mapLoc = new Location();
                // include id so client can reference records if needed
                mapLoc.setId(Loc.getId());
                mapLoc.setSlug(Loc.getSlug());
                mapLoc.setName(Loc.getName());
                mapLoc.setLongitude(Loc.getLongitude());
                mapLoc.setLatitude(Loc.getLatitude());
                // include category id and try to resolve human-readable category name
                mapLoc.setCategoryId(Loc.getCategoryId());
                try {
                    com.hoianweb.model.bean.Category cat =
                        this.categoryDAO.getById(Loc.getCategoryId());
                    if (cat != null) {
                        mapLoc.setCategoryName(cat.getName());
                    }
                } catch (Exception ignore) {
                    // If category lookup fails, we still send the id (client can handle missing name)
                }
                String firstImage = firstImageMap.get(Loc.getId());
                if (firstImage != null) {
                    mapLoc.setAvata(firstImage);
                }
                mapList.add(mapLoc);
            }

            // Debug: log final mapped list
            try {
                if (mapList != null) {
                    System.out.println(
                        "[LocationAPIServlet] sending mapList size = " +
                            mapList.size()
                    );
                    for (Location dbg : mapList) {
                        System.out.println(
                            String.format(
                                "[LocationAPIServlet] map loc slug=%s name=%s lat=%s lon=%s avata=%s",
                                dbg.getSlug(),
                                dbg.getName(),
                                dbg.getLatitude(),
                                dbg.getLongitude(),
                                dbg.getAvata()
                            )
                        );
                    }
                }
            } catch (Exception e) {
                System.err.println(
                    "[LocationAPIServlet] Error while logging mapList: " +
                        e.getMessage()
                );
                e.printStackTrace();
            }

            JsonUtil.sendJson(response, mapList);
        } else {
            try {
                String slug = pathInfo.substring(1);
                System.out.println(
                    "[LocationAPIServlet] GET detail - slug: " + slug
                );
                Location loca = locationDAO.getBySlug(slug);
                if (loca != null) {
                    // Fetch raw Image objects from DAO
                    List<Image> gallery = this.imageDAO.getByLocationId(
                        loca.getId()
                    );

                    // Normalize the gallery into a list of URL strings that start with '/uploads/...'
                    List<String> galleryUrls = new ArrayList<>();
                    if (gallery != null) {
                        for (Image im : gallery) {
                            if (
                                im == null || im.getImageUrl() == null
                            ) continue;
                            String u = im.getImageUrl().trim();
                            if (u.length() == 0) continue;

                            // If absolute URL, keep as-is
                            if (
                                u.startsWith("http://") ||
                                u.startsWith("https://")
                            ) {
                                galleryUrls.add(u);
                                continue;
                            }

                            // Prefer to return the path starting at '/uploads/...'
                            int idx = u.indexOf("/uploads/");
                            if (idx >= 0) {
                                galleryUrls.add(u.substring(idx));
                                continue;
                            }

                            // If stored as 'uploads/...' (no leading slash)
                            if (u.startsWith("uploads/")) {
                                galleryUrls.add("/" + u);
                                continue;
                            }

                            // If begins with '/' but doesn't include '/uploads/', return as-is (best-effort)
                            if (u.startsWith("/")) {
                                galleryUrls.add(u);
                                continue;
                            }

                            // Fallback: treat value as filename and prefix '/uploads/'
                            galleryUrls.add(
                                "/uploads/" + u.replaceAll("^/+", "")
                            );
                        }
                    }

                    // Ensure categoryName is resolved for convenience
                    List<Category> allCategories = this.categoryDAO.getAll();
                    for (Category cat : allCategories) {
                        if (cat.getId() == loca.getCategoryId()) {
                            loca.setCategoryName(cat.getName());
                            break;
                        }
                    }

                    // Debug: log detail info (size based on normalized gallery)
                    try {
                        System.out.println(
                            String.format(
                                "[LocationAPIServlet] detail loc id=%d name=%s slug=%s category=%s gallerySize=%d",
                                loca.getId(),
                                loca.getName(),
                                loca.getSlug(),
                                loca.getCategoryName(),
                                (galleryUrls != null ? galleryUrls.size() : 0)
                            )
                        );
                    } catch (Exception de) {
                        System.err.println(
                            "[LocationAPIServlet] Error while logging location detail: " +
                                de.getMessage()
                        );
                        de.printStackTrace();
                    }

                    // Build a response map (avoid returning Image objects; return primitive fields + gallery URL strings)
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", loca.getId());
                    dto.put("name", loca.getName());
                    dto.put("slug", loca.getSlug());
                    dto.put("longitude", loca.getLongitude());
                    dto.put("latitude", loca.getLatitude());
                    dto.put("description", loca.getDescription());
                    dto.put("categoryId", loca.getCategoryId());
                    dto.put("categoryName", loca.getCategoryName());
                    dto.put("gallery", galleryUrls);

                    JsonUtil.sendJson(response, dto);
                } else {
                    System.out.println(
                        "[LocationAPIServlet] Location not found for slug: " +
                            slug
                    );
                    response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Location not found"
                    );
                }
            } catch (Exception e) {
                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid URL format"
                );
                e.printStackTrace();
            }
        }
    }
}
