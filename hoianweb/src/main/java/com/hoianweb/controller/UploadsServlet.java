package com.hoianweb.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to serve uploaded files from a dedicated uploads directory on disk.
 *
 * Maps to /uploads/* and serves files from D:/hoian_uploads.
 *
 * Security and behavior:
 * - Prevents path traversal by resolving canonical paths and ensuring the requested
 *   file is inside the uploads directory.
 * - Sets a sensible Content-Type using the servlet context's mime type mapping.
 * - Sends Last-Modified and Cache-Control headers to allow client caching.
 * - Supports GET and HEAD; returns 404 if file missing, 403 if access denied.
 */
@WebServlet(name = "UploadsServlet", urlPatterns = {"/uploads/*"}, loadOnStartup = 1)
public class UploadsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIRECTORY = "D:/hoian_uploads";
    private static final int BUFFER_SIZE = 8192;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        serveFile(request, response, true);
    }

    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        serveFile(request, response, false);
    }

    private void serveFile(HttpServletRequest request, HttpServletResponse response, boolean writeBody)
            throws IOException {

        String pathInfo = request.getPathInfo(); // expected like "/filename.jpg"
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.trim().length() == 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing file name in URL");
            return;
        }
        String decoded;
        try {
            decoded = URLDecoder.decode(pathInfo, "UTF-8");
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malformed file path");
            return;
        }
        if (decoded.startsWith("/")) decoded = decoded.substring(1);

        File uploadsDir = new File(UPLOAD_DIRECTORY);
        if (!uploadsDir.exists() || !uploadsDir.isDirectory()) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Uploads directory not available");
            return;
        }

        File requestedFile = new File(uploadsDir, decoded);

        String uploadsCanonical = uploadsDir.getCanonicalPath();
        String requestedCanonical;
        try {
            requestedCanonical = requestedFile.getCanonicalPath();
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!requestedCanonical.startsWith(uploadsCanonical + File.separator) && !requestedCanonical.equals(uploadsCanonical)) {
            // Access outside the uploads directory is forbidden
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        if (!requestedFile.exists() || !requestedFile.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }

        // Set content type
        ServletContext ctx = getServletContext();
        String mime = ctx.getMimeType(requestedFile.getName());
        if (mime == null) mime = "application/octet-stream";
        response.setContentType(mime);

        // Set content length if possible
        long length = requestedFile.length();
        if (length >= 0 && length <= Integer.MAX_VALUE) {
            response.setContentLength((int) length);
        } else if (length > Integer.MAX_VALUE) {
            // For very large files, set header rather than content length int cast
            response.setHeader("Content-Length", Long.toString(length));
        }

        // Last-Modified header
        long lastModified = requestedFile.lastModified();
        if (lastModified > 0) {
            response.setDateHeader("Last-Modified", lastModified);
        }

        // Cache headers - allow clients to cache for a short time (adjust as needed)
        response.setHeader("Cache-Control", "public, max-age=3600"); // 1 hour
        response.setHeader("Pragma", ""); // disable legacy header

        // Prefer inline display for images/media
        response.setHeader("Content-Disposition", "inline; filename=\"" + requestedFile.getName() + "\"");

        if (!writeBody) {
            // HEAD request: headers only
            return;
        }

        // Stream file contents
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(requestedFile));
             OutputStream out = response.getOutputStream()) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (IOException e) {
            // If client disconnects while streaming, nothing more to do; log if desired
            // Do not reveal internal details to the client
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error reading file");
            }
        }
    }
}
