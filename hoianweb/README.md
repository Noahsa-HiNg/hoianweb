# Hướng dẫn deploy `hoianweb` trong Eclipse

Tài liệu này mô tả các bước cơ bản và chi tiết để triển khai (deploy) project `hoianweb` bằng Eclipse (Maven + Tomcat) trên máy Windows. Nội dung bao gồm: import CSDL, import project vào Eclipse, cập nhật Maven, publish server, chạy script thiết lập uploads, và start server.

---

## Yêu cầu trước khi bắt đầu
- MySQL (có quyền tạo database / import .sql)
- Eclipse IDE for Enterprise Java Developers (có WTP / Servers support)
- Apache Tomcat (phiên bản tương thích; ví dụ Tomcat 8/9)
- Maven (Eclipse sẽ dùng Maven integration)
- Quyền chạy PowerShell (để chạy `deploy-setup-uploads.ps1`)

---

## 1. Import file CSDL vào MySQL
1. Import file `csdl.sql` vào database mysql:
- Dùng MySQL Workbench / phpMyAdmin: mở SQL import → chọn file `csdl.sql` → chạy.

Lưu ý: nếu file `csdl.sql` không nằm trong thư mục dự án, hãy tìm file tương ứng và sử dụng đường dẫn chính xác.

---

## 2. Import folder `hoianweb` vào Eclipse (Maven project)
1. Mở Eclipse.
2. File > Import... > Maven > Existing Maven Projects.
3. Chọn root directory: đường dẫn đến folder `hoianweb` (ví dụ `D:\Code\MyWorkspace\UNI\CNW_HoianProject\hoianweb`).
4. Eclipse sẽ tìm `pom.xml` và liệt kê project; tick chọn project rồi Finish.

Ghi chú:
- Nếu project có mô-đun con thì Eclipse sẽ import các mô-đun đó.
- Nếu gặp lỗi về JDK, kiểm tra Project > Properties > Java Build Path / Java Compiler.

---

## 3. Update Maven Project trong Eclipse
1. Trong `Package Explorer` hoặc `Project Explorer`, chuột phải vào project `hoianweb`.
2. Maven > Update Project...
3. Chọn project, check:
   - "Force Update of Snapshots/Releases" nếu muốn tải lại dependencies.
   - "Update project configuration from pom.xml" (mặc định).
4. Click OK và chờ Eclipse tải/biên dịch.

Mục đích: đảm bảo tất cả dependencies được tải, cấu hình Maven được áp dụng.

---

## 4. Cấu hình Server trong Eclipse và Publish
1. Mở view `Servers` (Window > Show View > Other... > Server > Servers).
2. Nếu chưa có Tomcat server:
   - Chuột phải > New > Server.
   - Chọn phiên bản Tomcat tương ứng, chỉ ra đường dẫn cài Tomcat trên máy.
   - Thêm project `hoianweb` vào server khi được hỏi (Add and Remove).
3. Sau khi server đã có, chuột phải vào server > Publish (nếu cần).
   - Publish sẽ deploy webapp vào folder mà Eclipse cấu hình.

Quan trọng: Eclipse có hai chế độ "Server Locations":
- "Use workspace metadata (doesn't modify Tomcat installation)" — khi dùng WTP, webapp thường được copy vào `workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmpX/wtpwebapps/hoianweb`.
- "Use Tomcat installation (takes control of Tomcat installation)" — khi dùng chế độ này, webapp được xuất thẳng vào `TOMCAT_INSTALLATION/wtpwebapps/hoianweb`.

Nếu bạn cần script `deploy-setup-uploads.ps1` hoạt động với đường dẫn `wtpwebapps\hoianweb`, hãy kiểm tra chế độ publish của server để biết đường dẫn thực tế (bên dưới có hướng dẫn tìm path).

---

## 5. Chạy script `deploy-setup-uploads.ps1` và tìm `TomcatWebappsHoian`
Script `deploy-setup-uploads.ps1` nằm trong thư mục project `hoianweb`. Script này tạo folder uploads và tạo junction (symlink) từ webapp `uploads` đến một folder uploads ngoài Tomcat.

Phần đầu của file định nghĩa các tham số (để bạn biết thay đổi nếu cần):
```CNW_HoianProject/hoianweb/deploy-setup-uploads.ps1#L1-4
param(
    [string]$ExternalUploads = "D:\hoian_uploads",
    [string]$TomcatWebappsHoian = "D:\apache-tomcat-9.0.111\wtpwebapps\hoianweb",
    [switch]$RestartTomcat = $false
)
```

Hướng dẫn chạy script:
1. Mở PowerShell (chạy với quyền Administrator nếu lỗi).
2. Chuyển thư mục đến `hoianweb` hoặc chỉ định đường dẫn đầy đủ tới script.
3. Ví dụ chạy script với tham số (thay đường dẫn cho phù hợp):
```/dev/null/usage.ps1#L1-2
cd "D:\Code\MyWorkspace\UNI\CNW_HoianProject\hoianweb"
.\deploy-setup-uploads.ps1 -ExternalUploads "D:\hoian_uploads" -TomcatWebappsHoian "D:\apache-tomcat-9.0.111\wtpwebapps\hoianweb"
```
- Nếu Tomcat đang chạy trong Eclipse (không phải service), script sẽ bỏ qua restart (theo nội dung script).
- Nếu đường dẫn `TomcatWebappsHoian` trong script không khớp với nơi Eclipse publish, bạn có thể truyền tham số -TomcatWebappsHoian để override.

Cách tìm chính xác `TomcatWebappsHoian` (đường dẫn webapp của Eclipse):
- Trong Eclipse, mở view `Servers`.
- Double-click server để mở Server Overview.
- Xem phần "Server Locations" (nếu không thể chỉnh sửa vì server đã được tạo, bạn có thể xóa server và tạo lại để chọn dạng "Use Tomcat installation" nếu cần).
- Hoặc kiểm tra thư mục publish thực tế:
  - Nếu server dùng Tomcat installation, webapp sẽ nằm ở: `TOMCAT_HOME/wtpwebapps/hoianweb`.
  - Nếu dùng workspace metadata: tìm trong workspace metadata:
    - Workspace của Eclipse: `.../.metadata/.plugins/org.eclipse.wst.server.core/`
    - Trong đó có các tmp folder như `tmp0/wtpwebapps/hoianweb`. Lấy đường dẫn đầy đủ và dùng làm `TomcatWebappsHoian`.

Ví dụ đường dẫn mặc định trong script:
- `D:\apache-tomcat-9.0.111\wtpwebapps\hoianweb` — nếu Tomcat của bạn nằm ở `D:\apache-tomcat-9.0.111` và Eclipse publish vào `wtpwebapps`.

Sau khi chạy script, kiểm tra kết quả:
- Mở trình duyệt: `http://localhost:8080/hoianweb/uploads/` (nếu server đang chạy trên 8080) để xác nhận folder uploads được mapping.

---

## 6. Start server
1. Trong Eclipse > Servers view: chuột phải server > Start.
2. Theo dõi Console view để xem log start của Tomcat.
3. Nếu cần redeploy sau thay đổi code: chuột phải project > Run As > Maven install (nếu cần) hoặc chuột phải server > Publish / Clean.
4. Mở ứng dụng:
   - URL: `http://localhost:8080/hoianweb/` (thay port nếu Tomcat chạy port khác).

---

## Kiểm tra và xử lý sự cố (Troubleshooting)
- Nếu webapp không chạy / lỗi 404:
  - Kiểm tra Console của Tomcat trong Eclipse để xem exception khi deploy.
  - Kiểm tra file `web.xml`, các logs của ứng dụng.
- Nếu kết nối DB lỗi:
  - Kiểm tra thông tin cấu hình kết nối DB trong project (các file cấu hình có thể là `application.properties`, `context.xml`, `web.xml` hoặc file cấu hình riêng tùy project). Cập nhật username/password/host/port/database cho đúng.
- Nếu `deploy-setup-uploads.ps1` báo `ERROR: Webapp folder không tồn tại`:
  - Kiểm tra lại tham số `-TomcatWebappsHoian` khi chạy script.
  - Kiểm tra nơi Eclipse publish webapp (xem phần 5).
- Nếu không thể tạo junction (mklink), cần quyền admin trên Windows; chạy PowerShell as Administrator.
- Nếu port 8080 bị chiếm, đổi port Tomcat hoặc tắt ứng dụng đang dùng port đó.
