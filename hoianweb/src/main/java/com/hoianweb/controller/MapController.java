package com.hoianweb.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * MapController
 *
 * - Handles GET /map
 * - Reads optional query params: lat, lng (also accepts latitude, longitude)
 * - Reads Google Maps API key from classpath config.properties or environment variable (if configured)
 * - Sets request attributes:
 *      - "lat" -> String latitude (if provided)
 *      - "lng" -> String longitude (if provided)
 *      - "googleMapsApiKey" -> API key string (may be empty)
 * - Forwards to: /WEB-INF/views/map.jsp
 *
 * Usage:
 *  - /map?lat=15.88&lng=108.34
 *  - The JSP will fall back to default coords if lat/lng aren't provided.
 */
@WebServlet("/map")
public class MapController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // No external API key required when using Leaflet/OpenStreetMap
    private static final String CONFIG_RESOURCE = "";
    private static final String PROP_KEY_API = "";
    private static final String PROP_KEY_API_ENV_VAR = "";

    @Override
    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        // 1. Read params (supporting multiple possible names)
        String lat = firstNonEmpty(
            request.getParameter("lat"),
            request.getParameter("latitude")
        );

        String lng = firstNonEmpty(
            request.getParameter("lng"),
            request.getParameter("longitude")
        );

        // read place name (optional) - accept ?name=... or ?placeName=...
        String name = firstNonEmpty(
            request.getParameter("name"),
            request.getParameter("placeName")
        );

        // 2. No API key required for Leaflet/OpenStreetMap
        // 3. Set attributes for the JSP to use (JSP expects request attributes named 'lat', 'lng', 'placeName')
        if (lat != null && !lat.isEmpty()) {
            request.setAttribute("lat", lat);
        }
        if (lng != null && !lng.isEmpty()) {
            request.setAttribute("lng", lng);
        }
        if (name != null && !name.isEmpty()) {
            request.setAttribute("placeName", name);
        }

        // 4. Forward to JSP view (MVC)
        request
            .getRequestDispatcher("/WEB-INF/views/map.jsp")
            .forward(request, response);
    }

    /* No API key loading required for Leaflet/OpenStreetMap.
       This method was removed because Leaflet does not need a provider API key. */

    private static String firstNonEmpty(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (v != null && !v.trim().isEmpty()) return v.trim();
        }
        return null;
    }
}
