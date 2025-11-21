<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Read latitude/longitude and place name from request attributes (set by controller) or query params.
    // Controller should set request attributes:
    //   request.setAttribute("lat", someDoubleOrString);
    //   request.setAttribute("lng", someDoubleOrString);
    //   request.setAttribute("placeName", "Some Place Name");
    // Additionally we accept query param 'name' when linking from place.jsp.
    Object latAttr = request.getAttribute("lat");
    Object lngAttr = request.getAttribute("lng");
    Object nameAttr = request.getAttribute("placeName");
    if (nameAttr == null) nameAttr = request.getParameter("name");

    // Fallback coordinates (Hoi An approximate center) if none provided.
    double lat = 15.880058;
    double lng = 108.338046;
    String placeName = (nameAttr != null) ? nameAttr.toString().replace("'", "\\'") : "";

    try {
        if (latAttr != null) {
            lat = Double.parseDouble(latAttr.toString());
        }
    } catch (NumberFormatException ignored) { }

    try {
        if (lngAttr != null) {
            lng = Double.parseDouble(lngAttr.toString());
        }
    } catch (NumberFormatException ignored) { }

    String ctx = request.getContextPath();
%>
<!doctype html>
<html lang="vi">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Bản đồ - Hoi An</title>

    <!-- Bootstrap CSS (optional - kept minimal to match place.jsp style) -->
    <link rel="stylesheet" href="<%= ctx %>/css/bootstrap.min.css" />
    <!-- Leaflet CSS -->
    <link rel="stylesheet" href="https://unpkg.com/leaflet/dist/leaflet.css" />
    <!-- Header / main styles (copied from login.jsp) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/linearicons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/font-awesome.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/magnific-popup.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nice-select.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/animate.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/owl.carousel.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">

    <style>
        /* Ensure header sticks to top like login.jsp's visual */
        .default-header { background-color: rgba(4, 9, 30, 0.8) }
        .protfolio-wrap { margin-top: 0 !important; }
        body { margin: 0; }

        /* Simple styles to make the map full-width and responsive */
        .map-container {
            width: 100%;
            height: 520px;
            margin: 1rem 0;
            border: 1px solid #ddd;
        }
        .map-page .map-info {
            margin-top: 0.5rem;
        }
        .default-header .navbar-brand img {
            max-height: 40px;
        }
    </style>
</head>
<body class="map-page">

    <!-- Header copied from place.jsp -->
    <div class="protfolio-wrap">
        <header class="default-header">
            <nav class="navbar navbar-expand-lg navbar-light">
                <div class="container">
                    <a class="navbar-brand" href="${pageContext.request.contextPath}/index">
                        <img src="${pageContext.request.contextPath}/img/logo.png" alt="logo">
                    </a>
                    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent">
                        <span class="text-white lnr lnr-menu"></span>
                    </button>
                    <div class="collapse navbar-collapse justify-content-end align-items-center" id="navbarSupportedContent">
                        <ul class="navbar-nav">
                            <li><a href="${pageContext.request.contextPath}/index"><strong>Home</strong></a></li>
                            <li><a href="${pageContext.request.contextPath}/map"><strong>Map</strong></a></li>
                            <li><a href="${pageContext.request.contextPath}/login"><strong>Login</strong></a></li>
                        </ul>
                    </div>
                </div>
            </nav>
        </header>

        <section class="pt-4">
            <div class="container mt-3">
                <div class="place-content">
                    <!--<div class="text-center py-3">
                        <h2>Bản đồ Hội An</h2>
                    </div>-->

                    <!-- Map card -->
                    <div class="card">
                        <div class="card-body">
                            <div id="map" class="map-container" role="application" aria-label="Bản đồ"></div>

                            <div class="map-info d-flex justify-content-between align-items-center">
                                <div>
                                    <strong>Tọa độ:</strong>
                                    <span id="coords"><%= lat %>, <%= lng %></span>
                                </div>
                                <div>
                                    <!-- Open in Google Maps link -->
                                    <a id="openInGoogle" href="https://www.google.com/maps?q=<%= lat %>,<%= lng %>" target="_blank" rel="noopener" class="btn btn-sm btn-outline-primary">Mở bằng Google Maps</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>

    <!-- Scripts (copied / aligned with login.jsp includes) -->
    <script src="${pageContext.request.contextPath}/js/vendor/jquery-2.2.4.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/vendor/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/easing.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery.ajaxchimp.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery.magnific-popup.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/owl.carousel.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery.nice-select.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/parallax.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/mail-script.js"></script>
    <script src="${pageContext.request.contextPath}/js/main.js"></script>

    <!-- Pass server-side coordinates and place name to JavaScript -->
    <script>
        var initialLatitude = <%= lat %>;
        var initialLongitude = <%= lng %>;
        var placeName = '<%= placeName %>';
    </script>

    <!-- Leaflet JS -->
    <script src="https://unpkg.com/leaflet/dist/leaflet.js"></script>

    <script>
        // Initialize Leaflet map, marker and popup
        (function () {
            // Create map
            var map = L.map('map').setView([initialLatitude, initialLongitude], 15);

            // Tile layer (OpenStreetMap)
            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors'
            }).addTo(map);

            // Add marker and open popup with place name (if provided)
            var marker = L.marker([initialLatitude, initialLongitude]).addTo(map);
            if (placeName && placeName.trim().length) {
                marker.bindPopup(placeName).openPopup();
            } else {
                marker.bindPopup(initialLatitude + ', ' + initialLongitude).openPopup();
            }

            // Update coords display and Google Maps link for convenience
            var coordsEl = document.getElementById('coords');
            if (coordsEl) coordsEl.textContent = initialLatitude + ', ' + initialLongitude;
            var openInGoogle = document.getElementById('openInGoogle');
            if (openInGoogle) openInGoogle.href = 'https://www.google.com/maps?q=' + initialLatitude + ',' + initialLongitude;
        })();
    </script>

    <!-- Optional JS dependencies (Bootstrap/jQuery) - these paths assume they exist in the project -->
    <script src="<%= ctx %>/js/jquery.min.js"></script>
    <script src="<%= ctx %>/js/bootstrap.bundle.min.js"></script>
</body>
</html>
