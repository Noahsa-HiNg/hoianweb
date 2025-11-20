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
    <title>Admin Login</title>
    <link rel="icon" href="${pageContext.request.contextPath}/img/j.jpg" type="image/jpg">
    <link href="https://fonts.googleapis.com/css?family=Poppins:100,200,400,300,500,600,700" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/linearicons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/font-awesome.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/magnific-popup.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nice-select.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/animate.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/owl.carousel.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">

    <style>
        body { margin: 0; }
        .protfolio-wrap { margin-top: 0 !important; }
        .default-header { background-color: rgba(4, 9, 30, 0.8); }
        .login-form-section { padding: 120px 0 60px; }
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
                    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent">
                        <span class="text-white lnr lnr-menu"></span>
                    </button>
                    <div class="collapse navbar-collapse justify-content-end align-items-center" id="navbarSupportedContent">
                        <ul class="navbar-nav">
                            <li><a href="index"><strong>Home</strong></a></li>
                            <li><a href="map"><strong>Map</strong></a></li>
                            <li><a href="login"><strong>Login</strong></a></li>
                        </ul>
                    </div>
                </div>
            </nav>
        </header>

        <section class="login-form-section">
            <div class="container">
                <div class="row justify-content-center">
                    <div class="col-lg-6">
                        <div class="card shadow-sm">
                            <div class="card-body p-4 p-md-5">
                                <h3 class="card-title text-center mb-4">Admin Login</h3>

                                <form id="login-form">
                                    <div class="form-group mb-3">
                                        <label for="username" class="form-label">Username</label>
                                        <input type="text" class="form-control" id="username" required>
                                    </div>
                                    <div class="form-group mb-4">
                                        <label for="password" class="form-label">Password</label>
                                        <input type="password" class="form-control" id="password" required>
                                    </div>

                                    <div id="error-message" class="alert alert-danger d-none" role="alert"></div>

                                    <div class="d-grid">
                                        <button type="submit" class="btn btn-primary btn-lg w-100">Login</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>

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
	    document.getElementById('login-form').addEventListener('submit', function(event) {
	        event.preventDefault();

	        const username = document.getElementById('username').value.trim();
	        const password = document.getElementById('password').value;
	        const errorMessage = document.getElementById('error-message');
	        const contextPath = "${pageContext.request.contextPath}";

	        errorMessage.classList.add('d-none');

	        if (!username || !password) {
	            errorMessage.textContent = 'Vui lòng nhập đầy đủ thông tin.';
	            errorMessage.classList.remove('d-none');
	            return;
	        }

	        // SEND FORM DATA (NOT JSON)
	        const formData = new URLSearchParams();
	        formData.append('username', username);
	        formData.append('password', password);

	        fetch(contextPath + "/api/admin/login", {
	            method: 'POST',
	            headers: {
	                'Content-Type': 'application/x-www-form-urlencoded'
	            },
	            body: formData
	        })
	        .then(response => {
	            if (response.ok) {
	                return response.json().then(data => {
	                    window.location.href = contextPath + "/admin";
	                });
	            } else if (response.status === 401) {
	                return response.text().then(text => {
	                    // Servlet sends plain text error
	                    throw new Error(text.includes("Username") ? "Username hoặc mật khẩu không đúng" : "Lỗi xác thực");
	                });
	            } else {
	                throw new Error("Lỗi server");
	            }
	        })
	        .then(() => {
	            // Success redirect already done
	        })
	        .catch(error => {
	            console.error('Login error:', error);
	            errorMessage.textContent = error.message || 'Lỗi kết nối. Vui lòng thử lại.';
	            errorMessage.classList.remove('d-none');
	        });
	    });
	</script>
</body>
</html>
