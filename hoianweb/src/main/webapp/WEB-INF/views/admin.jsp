<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.css">
</head>
<body>
    <nav class="navbar navbar-dark bg-dark">
        <div class="container">
            <span class="navbar-brand mb-0 h1">Admin Dashboard</span>
            <button id="logout-button" class="btn btn-outline-light">Đăng xuất</button>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="alert alert-success" role="alert">
            Đăng nhập thành công!
        </div>
        
        <h1>Chào mừng đến trang Quản trị</h1>
        <p>Nội dung trang admin sẽ được đặt ở đây.</p>
    </div>

    <script>
        document.getElementById('logout-button').addEventListener('click', function() {
            const contextPath = "${pageContext.request.contextPath}";

            // Gọi API Đăng xuất
            fetch(`${contextPath}/api/admin/logout`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => response.json())
            .then(data => {
                console.log(data.message); // In ra "Đã đăng xuất"
                // Sau khi server hủy session, chuyển hướng về trang chủ
                window.location.href = `${contextPath}/index`;
            })
            .catch(error => {
                console.error('Lỗi khi đăng xuất:', error);
                // Dù lỗi cũng nên chuyển về trang chủ
                window.location.href = `${contextPath}/index`;
            });
        });
    </script>
</body>
</html>