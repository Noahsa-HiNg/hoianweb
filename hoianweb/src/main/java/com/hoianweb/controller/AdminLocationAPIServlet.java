package com.hoianweb.controller;

import com.hoianweb.controller.JsonUtil;
import com.hoianweb.model.bean.Category;
import com.hoianweb.model.bean.Image;
import com.hoianweb.model.bean.Location;
import com.hoianweb.model.dao.CategoryDAO;
import com.hoianweb.model.dao.ImageDAO;
import com.hoianweb.model.dao.LocationDAO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Admin API servlet for locations.
 *
 * Notes:
 * - Supports create (POST), update (PUT), delete (DELETE).
 * - DELETE supports:
 *   - Deleting an entire location: DELETE /api/admin/diadiem/{id}
 *   - Deleting a single gallery image via query params BEFORE path parsing:
 *       DELETE /api/admin/diadiem/gallery?id={id}&url={url}
 *     (this form is intentionally accepted first to be tolerant of various client forms)
 *   - Also supports path-style gallery delete if client calls:
 *       DELETE /api/admin/diadiem/{id}/gallery?url={url}
 */
@WebServlet("/api/admin/diadiem/*")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1MB
    maxFileSize = 1024 * 1024 * 20, // 20MB per file
    maxRequestSize = 1024 * 1024 * 100 // 100MB overall
)
public class AdminLocationAPIServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private LocationDAO locationDAO;
    private ImageDAO imageDAO;
    private CategoryDAO categoryDAO;
    private static final String UPLOAD_DIRECTORY = "D:/hoian_uploads";

    @Override
    public void init() {
        this.locationDAO = new LocationDAO();
        this.imageDAO = new ImageDAO();
        this.categoryDAO = new CategoryDAO();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // Support two forms of POST:
        // 1) Create new Location (JSON body) => existing behavior.
        // 2) Upload gallery files for an existing location:
        //    - POST /api/admin/diadiem/{id}/gallery (multipart/form-data)
        //    - or POST /api/admin/diadiem/gallery with parameter 'location_id'
        try {
        	request.setCharacterEncoding("UTF-8");
        	response.setCharacterEncoding("UTF-8");
            String pathInfo = request.getPathInfo(); // may be "/{id}/gallery" or similar

            // If multipart upload to gallery
            String contentType = request.getContentType();
            boolean isMultipart =
                contentType != null &&
                contentType.toLowerCase().startsWith("multipart/");
            boolean isGalleryPath =
                (pathInfo != null &&
                    pathInfo.toLowerCase().contains("gallery")) ||
                ("POST".equalsIgnoreCase(request.getMethod()) &&
                    request.getParameter("upload_gallery") != null);

            if (isMultipart && isGalleryPath) {
                // Resolve location id from path or param
                Integer locationId = null;
                if (request.getParameter("location_id") != null) {
                    try {
                        locationId = Integer.parseInt(
                            request.getParameter("location_id")
                        );
                    } catch (NumberFormatException ex) {
                        locationId = null;
                    }
                }
                if (locationId == null && pathInfo != null) {
                    // try to extract numeric id from pathInfo
                    String[] parts = pathInfo.split("/");
                    for (String p : parts) {
                        if (p == null || p.isEmpty()) continue;
                        if (p.matches("\\d+")) {
                            locationId = Integer.parseInt(p);
                            break;
                        }
                    }
                }
                if (locationId == null) {
                    response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Thiếu hoặc không hợp lệ 'location_id' cho upload"
                    );
                    return;
                }

                File uploadDir = new File(UPLOAD_DIRECTORY);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                List<String> createdUrls = new ArrayList<>();
                Collection<Part> parts = request.getParts();
                for (Part p : parts) {
                    String submitted = p.getSubmittedFileName();
                    if (
                        submitted == null || submitted.trim().isEmpty()
                    ) continue;
                    // basic content-type check
                    String partCt = p.getContentType();
                    if (
                        partCt == null ||
                        !partCt.toLowerCase().startsWith("image/")
                    ) {
                        // skip non-image parts
                        continue;
                    }
                    String original = submitted;
                    String ext = "";
                    int idx = original.lastIndexOf('.');
                    if (idx >= 0) ext = original.substring(idx);
                    String unique = UUID.randomUUID().toString() + ext;
                    File out = new File(uploadDir, unique);
                    p.write(out.getAbsolutePath());

                    String dbPath = "/uploads/" + unique;
                    Image img = new Image(dbPath, locationId);
                    int newImageId = imageDAO.create(img);
                    if (newImageId != -1) {
                        img.setId(newImageId);
                        createdUrls.add(dbPath);
                    }
                }

                response.setStatus(HttpServletResponse.SC_CREATED);
                JsonUtil.sendJson(
                    response,
                    Collections.singletonMap("uploaded", createdUrls)
                );
                return;
            }

            // Fallback: original JSON-based create location behavior
            Location newLocation = JsonUtil.readJson(request, Location.class);

            // Basic validation: ensure required fields are present
            if (
                newLocation == null ||
                newLocation.getName() == null ||
                newLocation.getName().trim().isEmpty() ||
                newLocation.getSlug() == null ||
                newLocation.getSlug().trim().isEmpty()
            ) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.sendJson(
                    response,
                    java.util.Collections.singletonMap(
                        "message",
                        "Dữ liệu không hợp lệ: 'name' và 'slug' là bắt buộc."
                    )
                );
                return;
            }

            // Require categoryId > 0 (category is mandatory for creation)
            if (newLocation.getCategoryId() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.sendJson(
                    response,
                    java.util.Collections.singletonMap(
                        "message",
                        "Dữ liệu không hợp lệ: 'categoryId' phải lớn hơn 0."
                    )
                );
                return;
            }

            // Validate latitude/longitude presence.
            // Note: Location model uses primitive double, default 0.0 may indicate missing in client JSON.
            // We treat 0.0 as missing here to avoid inserting invalid coordinates.
            try {
                double lat = newLocation.getLatitude();
                double lng = newLocation.getLongitude();
                if (
                    Double.isNaN(lat) ||
                    Double.isNaN(lng) ||
                    lat == 0.0 ||
                    lng == 0.0
                ) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.sendJson(
                        response,
                        java.util.Collections.singletonMap(
                            "message",
                            "Dữ liệu không hợp lệ: 'latitude' và 'longitude' là bắt buộc và phải khác 0."
                        )
                    );
                    return;
                }
            } catch (Exception ex) {
                // Defensive: if getters throw (shouldn't), return bad request
                ex.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.sendJson(
                    response,
                    java.util.Collections.singletonMap(
                        "message",
                        "Dữ liệu không hợp lệ: Vấn đề khi đọc 'latitude'/'longitude'."
                    )
                );
                return;
            }

            // Validate category exists
            try {
                Category existingCat = this.categoryDAO.getById(
                    newLocation.getCategoryId()
                );
                if (existingCat == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.sendJson(
                        response,
                        java.util.Collections.singletonMap(
                            "message",
                            "Category không tồn tại cho categoryId đã cung cấp."
                        )
                    );
                    return;
                }
            } catch (Exception ex) {
                // If DAO fails for some reason, return server error with JSON
                ex.printStackTrace();
                response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );
                JsonUtil.sendJson(
                    response,
                    java.util.Collections.singletonMap(
                        "message",
                        "Lỗi khi kiểm tra category: " + ex.getMessage()
                    )
                );
                return;
            }

            // Check slug uniqueness to avoid DB constraint violation that causes 500
            try {
                if (
                    newLocation.getSlug() != null &&
                    !newLocation.getSlug().trim().isEmpty()
                ) {
                    Location existing = this.locationDAO.getBySlug(
                        newLocation.getSlug()
                    );
                    if (existing != null) {
                        response.setStatus(HttpServletResponse.SC_CONFLICT); // 409
                        JsonUtil.sendJson(
                            response,
                            java.util.Collections.singletonMap(
                                "message",
                                "Slug đã tồn tại. Vui lòng chọn slug khác."
                            )
                        );
                        return;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );
                JsonUtil.sendJson(
                    response,
                    java.util.Collections.singletonMap(
                        "message",
                        "Lỗi khi kiểm tra slug: " + ex.getMessage()
                    )
                );
                return;
            }

            // Proceed to create record
            int newId = -1;
            try {
                newId = this.locationDAO.create(newLocation);
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );
                JsonUtil.sendJson(
                    response,
                    java.util.Collections.singletonMap(
                        "message",
                        "Lỗi khi tạo địa điểm: " + ex.getMessage()
                    )
                );
                return;
            }

            if (newId != -1) {
                newLocation.setId(newId);
                // Try to include categoryName for convenience in client responses
                try {
                    if (newLocation.getCategoryId() != 0) {
                        Category cat = this.categoryDAO.getById(
                            newLocation.getCategoryId()
                        );
                        if (cat != null) newLocation.setCategoryName(
                            cat.getName()
                        );
                    }
                } catch (Exception ignore) {}
                response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
                JsonUtil.sendJson(response, newLocation);
            } else {
                response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );
                JsonUtil.sendJson(
                    response,
                    java.util.Collections.singletonMap(
                        "message",
                        "Không thể tạo địa điểm."
                    )
                );
            }
        } catch (Exception e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Dữ liệu JSON không hợp lệ hoặc yêu cầu multipart không hợp lệ"
            );
        }
    }

    @Override
    protected void doPut(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        try {
        	request.setCharacterEncoding("UTF-8");
        	response.setCharacterEncoding("UTF-8");
            String pathInfo = request.getPathInfo(); // expected "/{id}"
            if (pathInfo == null || pathInfo.length() <= 1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.sendJson(
                    response,
                    java.util.Collections.singletonMap(
                        "message",
                        "ID không hợp lệ"
                    )
                );
                return;
            }
            String[] parts = pathInfo.split("/");
            String idPart = null;
            for (String p : parts) {
                if (p == null || p.isEmpty()) continue;
                if (p.matches("\\d+")) {
                    idPart = p;
                    break;
                }
            }
            if (idPart == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.sendJson(
                    response,
                    java.util.Collections.singletonMap(
                        "message",
                        "ID không hợp lệ"
                    )
                );
                return;
            }
            int id = Integer.parseInt(idPart);

            Location updatedLocation = JsonUtil.readJson(
                request,
                Location.class
            );
            updatedLocation.setId(id);

            boolean success = this.locationDAO.update(updatedLocation);

            if (success) {
                // Enrich with categoryName if possible so clients get human-readable info
                try {
                    if (updatedLocation.getCategoryId() != 0) {
                        Category cat = this.categoryDAO.getById(
                            updatedLocation.getCategoryId()
                        );
                        if (cat != null) updatedLocation.setCategoryName(
                            cat.getName()
                        );
                    }
                } catch (Exception ignore) {}
                JsonUtil.sendJson(response, updatedLocation);
            } else {
                response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Không tìm thấy địa điểm để cập nhật"
                );
            }
        } catch (Exception e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "ID không hợp lệ hoặc dữ liệu JSON sai"
            );
        }
    }

    /**
     * DELETE handling:
     * 1) If query params include id + url => treat as single-gallery-image delete (preferred).
     * 2) Otherwise parse pathInfo; supports path-style /{id}/gallery?url=... or /{id} whole-location delete.
     */
    @Override
    protected void doDelete(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        // ensure UTF-8 for response bodies
        response.setCharacterEncoding("UTF-8");

        try {
            // 1) Prefer query-param style for single-image deletion:
            //    DELETE /api/admin/diadiem/gallery?id={id}&url={url}
            // or  DELETE /api/admin/diadiem?id={id}&url={url}
            String idParam = request.getParameter("id");
            String urlParam = request.getParameter("url");
            if (
                idParam != null &&
                idParam.trim().length() > 0 &&
                urlParam != null &&
                urlParam.trim().length() > 0
            ) {
                // Be tolerant: extract digits from idParam
                String digits = idParam.trim().replaceAll("\\D+", "");
                if (digits.length() == 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.sendJson(
                        response,
                        Collections.singletonMap("message", "ID không hợp lệ")
                    );
                    return;
                }
                int id = Integer.parseInt(digits);

                try {
                    boolean deleted = this.imageDAO.deleteByLocationIdAndUrl(
                        id,
                        urlParam
                    );
                    if (deleted) {
                        // remove file from disk if exists
                        deleteFileFromServer(urlParam, request);
                        JsonUtil.sendJson(
                            response,
                            Collections.singletonMap(
                                "message",
                                "Xóa ảnh thành công"
                            )
                        );
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        JsonUtil.sendJson(
                            response,
                            Collections.singletonMap(
                                "message",
                                "Không tìm thấy ảnh để xóa"
                            )
                        );
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    response.setStatus(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                    );
                    JsonUtil.sendJson(
                        response,
                        Collections.singletonMap(
                            "message",
                            "Lỗi server: " + ex.getMessage()
                        )
                    );
                }
                return;
            }

            // 2) Fallback to path-based parsing: /{id} or /{id}/gallery?url=...
            String pathInfo = request.getPathInfo(); // e.g. "/5" or "/5/gallery"
            if (pathInfo == null) {
                String uri = request.getRequestURI();
                String prefix =
                    request.getContextPath() + request.getServletPath();
                if (uri != null && uri.startsWith(prefix)) {
                    pathInfo = uri.substring(prefix.length());
                } else {
                    pathInfo = "";
                }
            }

            if (pathInfo == null || pathInfo.length() <= 1) {
                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "ID không hợp lệ"
                );
                return;
            }

            String[] parts = pathInfo.split("/");
            String idPart = null;
            for (String p : parts) {
                if (p == null || p.isEmpty()) continue;
                if (p.matches("\\d+")) {
                    idPart = p;
                    break;
                }
            }
            if (idPart == null) {
                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "ID không hợp lệ"
                );
                return;
            }
            int id = Integer.parseInt(idPart);

            // check if path indicates a gallery operation
            boolean isGalleryPath = false;
            for (String p : parts) {
                if (p != null && p.equalsIgnoreCase("gallery")) {
                    isGalleryPath = true;
                    break;
                }
            }

            if (isGalleryPath) {
                String url = request.getParameter("url");
                if (url == null || url.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonUtil.sendJson(
                        response,
                        Collections.singletonMap("message", "Thiếu tham số url")
                    );
                    return;
                }

                try {
                    boolean deleted = this.imageDAO.deleteByLocationIdAndUrl(
                        id,
                        url
                    );
                    if (deleted) {
                        deleteFileFromServer(url, request);
                        JsonUtil.sendJson(
                            response,
                            Collections.singletonMap(
                                "message",
                                "Xóa ảnh thành công"
                            )
                        );
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        JsonUtil.sendJson(
                            response,
                            Collections.singletonMap(
                                "message",
                                "Không tìm thấy ảnh để xóa"
                            )
                        );
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    response.setStatus(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                    );
                    JsonUtil.sendJson(
                        response,
                        Collections.singletonMap(
                            "message",
                            "Lỗi server: " + ex.getMessage()
                        )
                    );
                }
                return;
            }

            // Otherwise, delete the whole location record and return any deleted image URLs
            List<String> deletedImageUrls = this.locationDAO.delete(id);
            if (deletedImageUrls != null) {
                for (String imageUrl : deletedImageUrls) {
                    deleteFileFromServer(imageUrl, request);
                }
                JsonUtil.sendJson(
                    response,
                    Collections.singletonMap(
                        "message",
                        "Xóa địa điểm (ID: " + id + ") và các ảnh thành công"
                    )
                );
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonUtil.sendJson(
                    response,
                    java.util.Collections.singletonMap(
                        "message",
                        "Không tìm thấy địa điểm để xóa"
                    )
                );
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.sendJson(
                response,
                java.util.Collections.singletonMap("message", "ID không hợp lệ")
            );
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Lỗi server: " + e.getMessage()
            );
        }
    }

    /**
     * Attempts to delete the file referenced by imageUrl from the filesystem.
     * This is tolerant to imageUrl values that include the context path or are absolute/relative paths.
     */
    private void deleteFileFromServer(
        String imageUrl,
        HttpServletRequest request
    ) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) return;

        try {
            String contextPath = request.getContextPath();
            String relPath = imageUrl;

            // If imageUrl contains the context path at start, remove it
            if (relPath.startsWith(contextPath)) {
                relPath = relPath.substring(contextPath.length());
            }

            // Ensure it begins with '/'
            if (!relPath.startsWith("/")) {
                relPath = "/" + relPath;
            }

            String realPath = getServletContext().getRealPath(relPath);
            if (realPath != null) {
                File fileToDelete = new File(realPath);
                if (fileToDelete.exists()) {
                    boolean ok = fileToDelete.delete();
                    if (ok) {
                        System.out.println("Đã xóa file: " + realPath);
                    } else {
                        System.out.println(
                            "Không thể xóa file (phần quyền?): " + realPath
                        );
                    }
                } else {
                    System.out.println(
                        "Không tìm thấy file để xóa: " + realPath
                    );
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Lỗi khi xóa file: " + imageUrl);
        }
    }
}
