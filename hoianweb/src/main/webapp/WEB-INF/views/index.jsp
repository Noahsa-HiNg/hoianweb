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
	<title>Du lịch Hội An</title>
    <link rel="icon" href="${pageContext.request.contextPath}/img/j.jpg" type="image/jpg">

	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap&subset=vietnamese" rel="stylesheet">

	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/linearicons.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/font-awesome.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/magnific-popup.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/nice-select.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/animate.min.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/owl.carousel.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">

	<style>
		body, h1, h2, h3, h4, h5, h6, p, a, li, .primary-btn {
			font-family: 'Roboto', sans-serif;
		}
		.filters-content::after {
			content: "";
			display: table;
			clear: both;
		}
		.banner-area,
		.portfolio-area,
		section {
		    padding-top: 120px;
		}

		@media (max-width: 991.98px) {
		    .banner-area,
		    .portfolio-area,
		    section {
		        padding-top: 80px;
		    }
		}

		@media (max-width: 767px) {
		    .banner-area,
		    .portfolio-area,
		    section {
		        padding-top: 70px;
		    }
		}
		.protfolio-wrap {
		    /*padding-bottom: 100px;*/
		    min-height: 80vh;
		}
		.footer-area {
		    margin-top: 50px;
		    width: 100%;
		}

		/* ========== SPINNER + IMAGE LOADER ========== */
		.image-wrapper {
		    position: relative;
		    height: 250px;
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
		    transition: opacity 0.5s ease;
		    position: relative;
		    z-index: 1;
		}
		.image-wrapper.loaded img {
			opacity: 1;
		}
		.spinner {
		    position: absolute;
		    top: 50%;
		    left: 50%;
		    transform: translate(-50%, -50%);
		    width: 50px;
		    height: 50px;
		    border: 5px solid #e9ecef;
		    border-top: 5px solid #8ab92d;
		    border-radius: 50%;
		    animation: spin 1s linear infinite;
		    z-index: 2;
		}
		.image-wrapper.loaded .spinner {
		    opacity: 0;
		    transform: translate(-50%, -50%) scale(0.8);
		    transition: all 0.4s ease;
		}
		@keyframes spin {
		    to { transform: translate(-50%, -50%) rotate(360deg); }
		}
		/* Keep spinner if no image */
		.image-wrapper:not(.loaded) .spinner {
		    opacity: 1 !important;
		}
		.image-wrapper:not(.loaded) {
		    background: #f0f0f0 !important;
		}
		/* ACTIVE FILTER = GREEN */
		#category-filters li {
		    color: #222;
		    transition: color 0.3s ease;
		}
		#category-filters li.active {
		    color: #8ab92d !important;
		}
		#category-filters li:hover {
		    color: #8ab92d;
		}
		/* FILTER ACTIVE = GREEN */
		#category-filters li {
		    color: #222;
		    cursor: pointer;
		    transition: color 0.3s ease;
		    font-weight: 500;
		    display: inline-block;
		    margin: 0 15px;
		    padding: 8px 0;
		    text-transform: uppercase;
		    font-size: 14px;
		    position: relative;
		}
		#category-filters li.active {
		    color: #8ab92d !important;
		}
		#category-filters li:hover:not(.active) {
		    color: #8ab92d;
		}

		/* GRID ITEMS HIDE/SHOW */
		.single-portfolio {
		    transition: all 0.3s ease;
		}
		/* =========================================== */
	</style>
</head>
<body>
	<div class="protfolio-wrap">

		<header class="default-header">
			<nav class="navbar navbar-expand-lg navbar-light">
				<div class="container">
					<a class="navbar-brand" href="index">
						<img src="${pageContext.request.contextPath}/img/logo.png" alt="">
					</a>
					<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
						<span class="text-white lnr lnr-menu"></span>
					</button>

					<div class="collapse navbar-collapse justify-content-end align-items-center" id="navbarSupportedContent">
						<ul class="navbar-nav">
							<li><a href="index">Home</a></li>
							<li><a href="map">Map</a></li>
							<li><a href="login">Login</a></li>
						</ul>
					</div>
				</div>
			</nav>
		</header>

		<section class="banner-area relative" id="home">
			<div class="overlay overlay-bg"></div>
			<div class="container">
				<div class="row fullscreen d-flex align-items-center justify-content-center">
					<div class="banner-content col-lg-10">
						<h5 class="text-uppercase">Hãy là một phần của hành trình tuyệt vời này</h5>
						<h1>Thành phố Hội An</h1>
						<a href="#portfolio" class="primary-btn text-uppercase">Khám phá thêm!</a>
					</div>
				</div>
			</div>
		</section>

		<section class="portfolio-area section-gap" id="portfolio">
			<div class="container">
				<div class="row d-flex justify-content-center">
					<div class="menu-content col-lg-10">
						<div class="title text-center">
							<h1 class="mb-10">Bạn sẽ yêu mến Hội An</h1>
							<p>Khám phá di sản văn hóa thế giới</p>
						</div>
					</div>
				</div>

				<div class="filters">
					<ul id="category-filters">
						<li class="active" data-filter="*">Tất cả</li>
					</ul>
				</div>

				<div class="filters-content">
					<div class="row grid" id="portfolio-grid">
						<!-- JS fills this -->
					</div>
				</div>
			</div>
		</section>

		<!--Footer section is commented out for now -->
		<!--<footer class="footer-area section-gap">
			<div class="container">
				<div class="row">
					<div class="col-lg-5 col-md-6 col-sm-6">
						<div class="single-footer-widget">
							<h6>Về chúng tôi</h6>
							<p>
								Chúng tôi cung cấp các kế hoạch du lịch cho chuyến đi thoải mái nhất của bạn.
								Hãy để bản thân đắm mình vào Vẻ đẹp, Văn hóa &amp; lễ hội của Hội An. Chúc bạn có thời gian vui vẻ tại Hội An.
							</p>
							<p class="footer-text">
								Copyright <script>document.write(new Date().getFullYear());</script> All rights reserved | This Website is created with <i class="fa fa-heart-o" aria-hidden="true"></i> by <a href="https://github.com/mrjatinchauhan" target="_blank">Jatin Chauhan</a>
							</p>
						</div>
					</div>
					<div class="col-lg-5 col-md-6 col-sm-6">
						<div class="single-footer-widget">
							<h6>Bản tin</h6>
							<p>Cập nhật thông tin mới nhất</p>
							<div class="" id="mc_embed_signup">
								<form target="_blank" action="" method="get" class="form-inline">
									<input class="form-control" name="EMAIL" placeholder="Nhập Email" onfocus="this.placeholder = ''" onblur="this.placeholder = 'Nhập Email của bạn '" required="" type="email">
									<button class="click-btn btn btn-default"><i class="fa fa-long-arrow-right" aria-hidden="true"></i></button>
									<div class="info"></div>
								</form>
							</div>
						</div>
					</div>
					<div class="col-lg-2 col-md-6 col-sm-6 social-widget">
						<div class="single-footer-widget">
							<h6>Theo dõi chúng tôi</h6>
							<p>Hãy kết nối</p>
							<div class="footer-social d-flex align-items-center">
								<a href="#"><i class="fa fa-facebook"></i></a>
								<a href="#"><i class="fa fa-twitter"></i></a>
								<a href="#"><i class="fa fa-snapchat"></i></a>
								<a href="#"><i class="fa fa-instagram"></i></a>
							</div>
						</div>
					</div>
				</div>
			</div>
		</footer>-->
	</div>

	<script src="${pageContext.request.contextPath}/js/vendor/jquery-2.2.4.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/vendor/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/easing.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/jquery.ajaxchimp.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/jquery.magnific-popup.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/owl.carousel.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/jquery.sticky.js"></script>
	<script src="${pageContext.request.contextPath}/js/jquery.nice-select.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/parallax.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/mail-script.js"></script>
    <!--<script src="${pageContext.request.contextPath}/js/isotope.pkgd.min.js"></script>-->
	<script src="https://unpkg.com/imagesloaded@4/imagesloaded.pkgd.min.js"></script>
	<script src="${pageContext.request.contextPath}/js/main.js"></script>

	<script>
	document.addEventListener("DOMContentLoaded", function() {
	    const contextPath = "${pageContext.request.contextPath}";
	    console.log("[DEBUG] contextPath = " + contextPath);
	    const portfolioGrid = document.getElementById('portfolio-grid');
	    const categoryFilters = document.getElementById('category-filters');

	    Promise.all([
	        fetch(contextPath + "/api/theloai"),
	        fetch(contextPath + "/api/diadiem/")
	    ])
	    .then(responses => Promise.all(responses.map(res => res.json())))
	    .then(([categories, locations]) => {
	        renderCategories(categories);
	        renderLocations(locations, categories);
	        bindFilterClicks();  // Bind immediately
	    })
	    .catch(error => {
	        console.error("Lỗi khi tải dữ liệu:", error);
	        portfolioGrid.innerHTML = "<p class='text-center'>Không thể tải dữ liệu địa điểm.</p>";
	    });

	    function renderCategories(categories) {
	        let filterHtml = '<li class="active" data-filter="*">Tất cả</li>';
	        if (categories && Array.isArray(categories)) {
	            categories.forEach(function(cat) {
	                const filterClass = "category-" + cat.id;
	                filterHtml += '<li data-filter=".' + filterClass + '">' + cat.name + '</li>';
	            });
	        }
	        categoryFilters.innerHTML = filterHtml;
	    }

	    function renderLocations(locations, categories) {
	        const categoryMap = new Map();
	        if (categories && Array.isArray(categories)) {
	            categories.forEach(function(cat) {
	                categoryMap.set(cat.id, cat.name);
	            });
	        }

	        let locationsHtml = '';
	        if (locations && Array.isArray(locations)) {
	            locations.forEach(function(loc) {
	                const filterClass = "category-" + loc.categoryId;
	                const imageUrl = loc.avata && loc.avata.startsWith('http')
	                    ? loc.avata
	                    : (loc.avata ? contextPath + loc.avata : '');
	                const detailUrl = loc.slug ? contextPath + '/' + loc.slug : '#';
	                const tenDiaDiem = loc.name || 'Địa điểm không tên';
	                const tenTheLoai = categoryMap.get(loc.categoryId) || 'Không rõ';

	                const imgTag = imageUrl
	                    ? '<img src="' + imageUrl + '" alt="' + tenDiaDiem + '" ' +
	                      'onload="this.parentNode.classList.add(\'loaded\')" ' +
	                      'onerror="this.onerror=null; this.src=\'' + contextPath + '/img/no-image.png\'; this.parentNode.classList.remove(\'loaded\')">'
	                    : '';

	                locationsHtml +=
	                    '<div class="single-portfolio col-sm-4 all ' + filterClass + '">' +
	                        '<div class="item">' +
	                            '<div class="image-wrapper">' +
	                                '<div class="spinner"></div>' +
	                                imgTag +
	                            '</div>' +
	                            '<div class="p-inner">' +
	                                '<h4><a href="' + detailUrl + '">' + tenDiaDiem + '</a></h4>' +
	                                '<div class="cat">' + tenTheLoai + '</div>' +
	                            '</div>' +
	                        '</div>' +
	                    '</div>';
	            });
	        }
	        portfolioGrid.innerHTML = locationsHtml;
	    }

	    function bindFilterClicks() {
	        $(categoryFilters).off('click').on('click', 'li', function() {
	            const $this = $(this);
	            const filterValue = $this.attr('data-filter');

	            // Update active class (GREEN)
	            $(categoryFilters).find('.active').removeClass('active');
	            $this.addClass('active');

	            // FILTER WITH PURE JS
	            const items = portfolioGrid.querySelectorAll('.single-portfolio');

	            items.forEach(item => {
	                if (filterValue === '*' || item.classList.contains(filterValue.substring(1))) {
	                    item.style.display = 'block';
	                } else {
	                    item.style.display = 'none';
	                }
	            });
	        });
	    }
	});
	</script>
</body>
</html>
