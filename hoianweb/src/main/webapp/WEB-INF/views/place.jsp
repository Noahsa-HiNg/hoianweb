<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zxx" class="no-js">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/img/fav.png">
    <meta name="author" content="">
    <meta name="description" content="">
    <meta name="keywords" content="">
    <meta charset="UTF-8">
    <title>Place - Tour Hoi An</title>
    <link rel="icon" href="${pageContext.request.contextPath}/img/j.jpg" type="image/jpg">
    <link href="https://fonts.googleapis.com/css?family=Poppins:100,200,400,300,500,600,700" rel="stylesheet">

    <!-- Header / main styles (copied from login.jsp) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/linearicons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/font-awesome.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/magnific-popup.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nice-select.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/animate.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/owl.carousel.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">

    <!-- Place-specific styles -->
    <link href="${pageContext.request.contextPath}/css/place.css" rel="stylesheet">

    <style>
        /* Ensure header sticks to top like login.jsp's visual */
        .default-header { background-color: rgba(4, 9, 30, 0.8) }
        .protfolio-wrap { margin-top: 0 !important; }
        body { margin: 0; }

        /* Main image & spinner */
        .image-wrapper {
            position: relative;
            height: 400px;
            background: #f8f9fa;
            border-radius: 10px;
            overflow: hidden;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .image-wrapper img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            opacity: 0;
            transition: opacity 0.4s ease;
        }
        .image-wrapper.loaded img { opacity: 1; }
        .spinner {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            width: 44px;
            height: 44px;
            border: 5px solid #e9ecef;
            border-top: 5px solid #8ab92d;
            border-radius: 50%;
            animation: spin 1s linear infinite;
            z-index: 2;
        }
        @keyframes spin { to { transform: translate(-50%, -50%) rotate(360deg); } }
        .image-wrapper.loaded .spinner { opacity: 0; transform: translate(-50%, -50%) scale(0.85); transition: all 0.3s ease; }

        /* Thumbnail strip with arrows */
        .thumb-strip-wrapper {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-top: 12px;
        }
        .thumb-arrow {
            background: rgba(0,0,0,0.55);
            color: #fff;
            border: none;
            width: 42px;
            height: 42px;
            border-radius: 50%;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            flex: 0 0 auto;
        }
        .thumb-arrow:disabled { opacity: 0.35; cursor: default; }
        .thumb-viewport {
            overflow: hidden;
            flex: 1 1 auto;
        }
        .thumb-track {
            display: flex;
            gap: 8px;
            align-items: center;
            transition: transform 0.2s ease;
        }
        .thumb-item {
            flex: 0 0 auto;
            height: 200px; /* fixed height */
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 6px;
            background: #f8f9fa;
            overflow: hidden;
        }
        .thumb-item img {
            height: 100%;
            width: auto;
            display: block;
            cursor: pointer;
            transition: transform .15s ease;
        }
        .thumb-item img:hover { transform: scale(1.03); }

        /* Overlay viewer */
        .overlay-viewer {
            position: fixed;
            inset: 0;
            background: rgba(0,0,0,0.85);
            display: none;
            align-items: center;
            justify-content: center;
            z-index: 2000;
            cursor: pointer;
        }
        .overlay-viewer img {
            max-width: 92%;
            max-height: 92%;
            box-shadow: 0 8px 30px rgba(0,0,0,0.6);
            border-radius: 6px;
        }

        /* small screens */
        @media (max-width: 576px) {
            .image-wrapper { height: 250px; }
            .thumb-item { height: 120px; }
            .thumb-arrow { width: 36px; height: 36px; }
            body { padding-top: 72px; }
        }
    </style>
</head>
<body>
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
            <div class="container mt-3" id="place-content">
                <!-- Dynamic content will be injected here -->
                <div class="text-center py-5 text-muted">Đang tải...</div>
            </div>
        </section>

        <!--Footer, change if needed-->
        <!--<footer class="py-3 bg-dark text-white mt-5">
            <div class="container">
                <p class="m-0 text-center">Copyright © Tour Hoi An</p>
            </div>
        </footer>-->
    </div>

    <!-- Overlay for viewer -->
    <div id="overlayViewer" class="overlay-viewer" aria-hidden="true"><img id="overlayImage" src="" alt="preview"></div>

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

    <script>
        (function() {
            const contextPath = "${pageContext.request.contextPath}";
            const slug = "${slug}";

            function showError(msg) {
                document.getElementById('place-content').innerHTML = '<div class="alert alert-danger">Lỗi: ' + msg + '</div>';
            }

            if (!slug || slug === 'null') {
                showError('Không tìm thấy địa điểm.');
                return;
            }

            fetch(contextPath + "/api/diadiem/" + slug)
                .then(r => r.ok ? r.json() : Promise.reject('Not found'))
                .then(data => {
                    const gallery = Array.isArray(data.gallery) ? data.gallery : [];
                    const mainImage = gallery.length ? (contextPath + gallery[0]) : '';

                    // Build content
                    const container = document.createElement('div');

                    // Title + breadcrumb
                    const titleHtml =
                        '<h1 class="mt-4 mb-3">' + (data.name || 'Không có tên') + ' <small>' + (data.categoryName || '') + '</small></h1>' +
                        '<ol class="breadcrumb">' +
                            '<li class="breadcrumb-item"><a href="' + contextPath + '/index">Home</a></li>' +
                            '<li class="breadcrumb-item active">' + (data.name || 'Địa điểm') + '</li>' +
                        '</ol>';
                    container.insertAdjacentHTML('beforeend', titleHtml);

                    // Main row
                    const leftStart = '<div class="row"><div class="col-md-8">';
                    const leftHtml =
                        '<div class="image-wrapper" id="mainImageWrapper">' +
                            '<div class="spinner"></div>' +
                            (mainImage ? ('<img id="mainImage" src="' + mainImage + '" alt="' + (data.name || '') + '">') : '<div class="p-5 text-center text-muted">Không có ảnh chính</div>') +
                        '</div>';
                    const rightHtml =
                        '</div><div class="col-md-4">' +
                            '<h3 class="my-3">Mô tả</h3>' +
                            '<p>' + (data.description || 'Không có mô tả.') + '</p>' +
                            '<h3 class="my-3">Thông tin</h3>' +
                            '<ul>' +
                                '<li><strong>Danh mục:</strong> ' + (data.categoryName || 'Không rõ') + '</li>' +
                                '<li><strong>Tọa độ:</strong> ' + (data.latitude || '') + ', ' + (data.longitude || '') + '</li>' +
                            '</ul>' +
                        '</div></div>';
                    container.insertAdjacentHTML('beforeend', leftStart + leftHtml + rightHtml);

                    // Thumbnails
                    const thumbs = gallery.length > 1 ? gallery.slice(1) : [];

                    if (thumbs.length) {
                        let thumbsOuter =
                            '<h3 class="my-4">Ảnh liên quan</h3>' +
                            '<div class="thumb-strip-wrapper" id="thumbStripWrapper">' +
                                '<button class="thumb-arrow" id="thumbPrev" aria-label="Prev">&lt;</button>' +
                                '<div class="thumb-viewport"><div class="thumb-track" id="thumbTrack">';
                        thumbs.forEach(function(img) {
                            thumbsOuter +=
                                '<div class="thumb-item">' +
                                    '<img src="' + (contextPath + img) + '" data-src="' + (contextPath + img) + '" alt="thumb">' +
                                '</div>';
                        });
                        thumbsOuter += '</div></div><button class="thumb-arrow" id="thumbNext" aria-label="Next">&gt;</button></div>';
                        container.insertAdjacentHTML('beforeend', thumbsOuter);
                    } else {
                        container.insertAdjacentHTML('beforeend', '<h3 class="my-4">Ảnh liên quan</h3><p class="text-muted">Không có ảnh phụ.</p>');
                    }

                    const placeContent = document.getElementById('place-content');
                    placeContent.innerHTML = '';
                    placeContent.appendChild(container);

                    // Bind main image spinner behavior
                    const mainWrapper = document.getElementById('mainImageWrapper');
                    const mainImg = document.getElementById('mainImage');
                    if (mainImg) {
                        mainImg.addEventListener('load', function() {
                            mainWrapper.classList.add('loaded');
                        });
                        mainImg.addEventListener('error', function() {
                            mainWrapper.classList.add('loaded');
                        });
                    } else {
                        const sp = mainWrapper.querySelector('.spinner');
                        if (sp) sp.style.display = 'none';
                    }

                    // Thumbnail scrolling
                    const thumbTrack = document.getElementById('thumbTrack');
                    const thumbViewport = document.querySelector('#thumbStripWrapper .thumb-viewport');
                    const btnPrev = document.getElementById('thumbPrev');
                    const btnNext = document.getElementById('thumbNext');

                    function updateThumbArrows() {
                        if (!thumbTrack || !thumbViewport || !btnPrev || !btnNext) return;
                        const maxScroll = thumbTrack.scrollWidth - thumbViewport.clientWidth;
                        btnPrev.disabled = (thumbViewport.scrollLeft <= 0);
                        btnNext.disabled = (thumbViewport.scrollLeft >= maxScroll - 1);
                        if (thumbTrack.scrollWidth <= thumbViewport.clientWidth) {
                            btnPrev.style.display = 'none';
                            btnNext.style.display = 'none';
                        } else {
                            btnPrev.style.display = '';
                            btnNext.style.display = '';
                        }
                    }

                    if (btnPrev && btnNext && thumbViewport) {
                        const scrollAmount = Math.max(200, Math.floor(thumbViewport.clientWidth * 0.5));
                        btnPrev.addEventListener('click', function() {
                            thumbViewport.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
                        });
                        btnNext.addEventListener('click', function() {
                            thumbViewport.scrollBy({ left: scrollAmount, behavior: 'smooth' });
                        });
                        thumbViewport.addEventListener('scroll', updateThumbArrows);
                        window.addEventListener('resize', updateThumbArrows);
                        setTimeout(updateThumbArrows, 80);
                    }

                    // Overlay viewer
                    const overlay = document.getElementById('overlayViewer');
                    const overlayImg = document.getElementById('overlayImage');
                    function showOverlay(src) {
                        overlayImg.src = src;
                        overlay.style.display = 'flex';
                        overlay.setAttribute('aria-hidden', 'false');
                    }
                    function hideOverlay() {
                        overlay.style.display = 'none';
                        overlayImg.src = '';
                        overlay.setAttribute('aria-hidden', 'true');
                    }
                    overlay.addEventListener('click', hideOverlay);
                    document.addEventListener('keydown', function(ev) {
                        if (ev.key === 'Escape' && overlay.style.display === 'flex') hideOverlay();
                    });

                    // Clicks on thumbnails
                    const thumbImgs = document.querySelectorAll('.thumb-item img');
                    thumbImgs.forEach(function(ti) {
                        ti.addEventListener('click', function(evt) {
                            evt.preventDefault();
                            showOverlay(ti.getAttribute('data-src') || ti.src);
                        });
                    });

                    if (mainImg) {
                        mainImg.style.cursor = 'pointer';
                        mainImg.addEventListener('click', function() {
                            showOverlay(mainImg.src);
                        });
                    }
                })
                .catch(err => {
                    console.error('Load place error:', err);
                    showError(err);
                });
        })();
    </script>
</body>
</html>
