<%@ page isELIgnored="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Provide context path via scriptlet to avoid EL parsing inside JS/template literals
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Quản lý địa điểm</title>

    <!-- Local Bootstrap CSS (project already contains this) -->
    <link rel="stylesheet" href="<%= ctx %>/css/bootstrap.css">

    <style>
        /* Floating panel customizations (modal already provides floating behavior) */
        .gallery-preview img {
            max-height: 60px;
            margin-right: 8px;
            border-radius: 4px;
            display: block;
        }
        .gallery-preview .img-wrap {
            position: relative;
            display: inline-block;
            margin-right: 8px;
            margin-bottom: 6px;
        }
        .gallery-preview .img-wrap img {
            max-height: 60px;
            border-radius: 4px;
            display: block;
        }
        .gallery-preview .img-remove {
            position: absolute;
            top: 2px;
            right: 2px;
            background: rgba(0,0,0,0.6);
            color: #fff;
            border: none;
            border-radius: 50%;
            width: 20px;
            height: 20px;
            line-height: 18px;
            text-align: center;
            padding: 0;
            cursor: pointer;
            font-weight: 700;
        }
        .table-actions button {
            margin-right: 4px;
        }
        .required::after {
            content: " *";
            color: #d00;
        }
    </style>
</head>
<body>
<nav class="navbar navbar-dark bg-dark">
    <div class="container">
        <span class="navbar-brand mb-0 h1">Admin Dashboard</span>
        <div>
            <button type="button" id="refresh-button" class="btn btn-outline-light btn-sm me-2">Làm mới</button>
            <button type="button" id="logout-button" class="btn btn-outline-light btn-sm">Đăng xuất</button>
        </div>
    </div>
</nav>

<div class="container mt-4">

    <div id="alert-placeholder"></div>

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3>Danh sách địa điểm</h3>
        <div>
            <button type="button" id="add-button" class="btn btn-primary">Thêm địa điểm mới</button>
        </div>
    </div>

    <div class="table-responsive">
        <table id="places-table" class="table table-striped table-bordered align-middle">
            <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Tên</th>
                    <th>Slug</th>
                    <th>Category</th>
                    <th>Latitude</th>
                    <th>Longitude</th>
                    <th>Mô tả</th>
                    <th>Gallery</th>
                    <th style="width:150px">Hành động</th>
                </tr>
            </thead>
            <tbody>
                <!-- Rows populated dynamically -->
            </tbody>
        </table>
    </div>
</div>

<!-- Floating edit/add panel: Bootstrap modal -->
<div class="modal fade" id="editModal" tabindex="-1" aria-labelledby="editModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content">
      <form id="place-form">
        <div class="modal-header">
          <h5 class="modal-title" id="editModalLabel">Chỉnh sửa địa điểm</h5>
          <!--<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>-->
        </div>
        <div class="modal-body">
            <input type="hidden" id="place-id" name="id" value="">

            <div class="mb-3">
                <label for="place-name" class="form-label required">Tên</label>
                <input type="text" class="form-control" id="place-name" name="name" required>
            </div>

            <div class="mb-3">
                <label for="place-slug" class="form-label">Slug</label>
                <input type="text" class="form-control" id="place-slug" name="slug">
                <div class="form-text">Nếu để trống, server có thể tự sinh slug</div>
            </div>

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="place-lat" class="form-label required">Latitude</label>
                    <input type="number" step="any" class="form-control" id="place-lat" name="latitude" required>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="place-lng" class="form-label required">Longitude</label>
                    <input type="number" step="any" class="form-control" id="place-lng" name="longitude" required>
                </div>
            </div>

            <div class="mb-3">
                <label for="place-category" class="form-label required">Category</label>
                <select class="form-select" id="place-category" name="categoryId" required>
                    <option value="">-- Chọn category --</option>
                </select>
            </div>

            <div class="mb-3">
                <label for="place-desc" class="form-label">Mô tả</label>
                <textarea class="form-control" id="place-desc" name="description" rows="3"></textarea>
            </div>

            <div class="mb-3">
                <label for="place-image" class="form-label">Tải ảnh (thêm vào gallery)</label>
                <input class="form-control" type="file" id="place-image" name="image" accept="image/*" multiple>
                <div class="form-text">Chọn 1 hoặc nhiều ảnh để upload vào gallery sau khi lưu bản ghi.</div>
            </div>

            <div class="mb-3">
                <label class="form-label">Xem trước gallery</label>
                <div id="gallery-preview" class="gallery-preview d-flex flex-row flex-wrap"></div>
            </div>

        </div>
        <div class="modal-footer">
          <button type="button" id="delete-inside-button" class="btn btn-danger me-auto d-none">Xóa</button>
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
          <button type="submit" class="btn btn-primary" id="save-button">Lưu</button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Bootstrap JS (CDN with local fallback) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
<script>
    // If CDN fails or bootstrap is not available, load local copy as a fallback
    (function(){
        if (typeof bootstrap === 'undefined') {
            var s = document.createElement('script');
            s.src = '<%= ctx %>/js/bootstrap.bundle.min.js';
            s.defer = false;
            document.head.appendChild(s);
        }
    })();
</script>

<script>
    (function () {
        // Use scriptlet-provided context path to avoid any ${...} parsing issues
        const ctx = '<%= ctx %>';
        // admin API base used for create/update/delete/upload
        const adminApi = ctx + '/api/admin/diadiem';
        // public API used for listing and fetching by slug (shared with index.jsp)
        const listApi = ctx + '/api/diadiem';
        // categories API for fetching all categories (theloai)
        const categoriesApi = ctx + '/api/theloai';
        // keep `apiBase` name for existing code that performs admin actions
        const apiBase = adminApi;
        // cache the list returned by the public API to avoid unnecessary extra calls
        let locationsCache = [];
        // cache for categories (array of {id,name})
        let categoriesCache = [];

        const tableBody = document.querySelector("#places-table tbody");
        const editModalEl = document.getElementById('editModal');
        const editModal = new bootstrap.Modal(editModalEl);
        const placeForm = document.getElementById('place-form');
        const alertPlaceholder = document.getElementById('alert-placeholder');
        const galleryPreview = document.getElementById('gallery-preview');
        const deleteInsideBtn = document.getElementById('delete-inside-button');

        // Insert helper text under latitude / longitude inputs (so users know expected range/format)
        (function insertLatLngHelp(){
            try {
                var latEl = document.getElementById('place-lat');
                var lngEl = document.getElementById('place-lng');
                function addHelp(el, id, text){
                    if (!el) return;
                    var p = document.createElement('div');
                    p.className = 'form-text text-muted';
                    p.id = id;
                    p.textContent = text;
                    // insert after the input's parent if exists
                    var parent = el.parentNode;
                    if (parent) {
                        // avoid duplicate
                        var existing = parent.querySelector('#' + id);
                        if (!existing) parent.appendChild(p);
                    }
                }
                addHelp(latEl, 'place-lat-help', 'Latitude phải là số thập phân (ví dụ: 15.877232). Phạm vi hợp lệ: -90 tới 90.');
                addHelp(lngEl, 'place-lng-help', 'Longitude phải là số thập phân (ví dụ: 108.337041). Phạm vi hợp lệ: -180 tới 180.');
            } catch (e) { console.warn('Cannot insert lat/lng help text', e); }
        })();

        // Show temporary alert (append to alertPlaceholder)
        function showAlert(message, type = 'success', timeout = 4000) {
            const wrapper = document.createElement('div');
            wrapper.innerHTML = '<div class="alert alert-' + type + ' alert-dismissible" role="alert">'
                + '<div>' + message + '</div>'
                // + '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>'
                + '</div>';
            alertPlaceholder.append(wrapper);

            // auto-close after timeout if provided
            if (timeout) setTimeout(function(){ var a = wrapper.querySelector('.alert'); if (a) bootstrap.Alert.getOrCreateInstance(a).close(); }, timeout);
        }

        // Show validation/error message inside the edit modal (scroll modal body to top smoothly and focus the alert)
        function showModalError(message, type = 'danger') {
            try {
                var modalBody = editModalEl.querySelector('.modal-body');
                if (!modalBody) { showAlert(message, type); return; }

                // ensure a placeholder exists at the very top of modal-body
                var ph = editModalEl.querySelector('#modal-error-placeholder');
                if (!ph) {
                    ph = document.createElement('div');
                    ph.id = 'modal-error-placeholder';
                    modalBody.insertBefore(ph, modalBody.firstChild);
                }

                // put an alert with tabindex so we can programmatically focus it
                ph.innerHTML = '<div class="alert alert-' + type + ' alert-dismissible" role="alert" tabindex="-1">'
                    + '<div>' + message + '</div>'
                    // + '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>'
                    + '</div>';

                // allow layout to settle, then smoothly scroll modalBody to top and focus the alert
                setTimeout(function() {
                    try {
                        // Smooth scroll to top of modal-body; fallback to instant if not supported
                        if (modalBody.scrollTo) {
                            modalBody.scrollTo({ top: 0, behavior: 'smooth' });
                        } else {
                            modalBody.scrollTop = 0;
                        }
                        // focus the alert so keyboard/screen-reader users get immediate context
                        var alertEl = ph.querySelector('.alert');
                        if (alertEl) {
                            try { alertEl.focus(); } catch (e) { /* ignore focus errors */ }
                        }
                    } catch (e) {
                        console.warn('Error while scrolling/focusing modal alert', e);
                    }
                }, 50);

            } catch (e) {
                console.error('showModalError failed', e);
                showAlert(message, type);
            }
        }

        function clearModalError() {
            try {
                var ph = editModalEl.querySelector('#modal-error-placeholder');
                if (ph) ph.innerHTML = '';
            } catch (e) {}
        }

        // Fetch list of places (use public list API so admin and index share the same source)
        async function fetchList() {
            tableBody.innerHTML = '<tr><td colspan="9" class="text-center">Đang tải...</td></tr>';
            try {
                const res = await fetch(listApi, { method: 'GET', credentials: 'same-origin' });
                if (!res.ok) {
                    throw new Error('Không thể lấy danh sách: ' + res.status);
                }
                const data = await res.json();
                locationsCache = Array.isArray(data) ? data : [];
                await populateCategorySelect();
                renderTable(locationsCache);
            } catch (err) {
                tableBody.innerHTML = '<tr><td colspan="9" class="text-danger">Lỗi khi tải: ' + err.message + '</td></tr>';
                console.error(err);
            }
        }

        function truncateText(text, length = 80) {
            if (!text) return '';
            return text.length > length ? text.slice(0, length) + '…' : text;
        }

        // Render table rows (tolerant to both public API shape and admin API shape)
        function renderTable(items) {
            tableBody.innerHTML = '';
            if (!Array.isArray(items) || items.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="9" class="text-center">Không có địa điểm nào.</td></tr>';
                return;
            }
            for (var i = 0; i < items.length; i++) {
                var item = items[i];
                var tr = document.createElement('tr');

                var galleryHtml = '';
                if (item && item.gallery && item.gallery.length) {
                    for (var j = 0; j < item.gallery.length; j++) {
                        var u = item.gallery[j];
                        var src = ctx + (u && u.startsWith && u.startsWith('/') ? '' : '/') + u;
                        galleryHtml += '<img src="' + src + '" alt="" style="height:40px;margin-right:6px;border-radius:3px">';
                    }
                }

                // Flexible field access: public API may not include some admin-only fields
                        var idCell = (item && (item.id !== undefined && item.id !== null)) ? item.id : '';
                        var nameCell = (item && item.name) ? escapeHtml(item.name) : '';
                        var slugVal = (item && item.slug) ? item.slug : (item && item.Slug) ? item.Slug : '';
                        var slugCell = slugVal ? escapeHtml(slugVal) : '';
                        // Resolve category: prefer categoryId mapped to category name via categoriesCache, fall back to categoryName or raw id
                        var categoryCell = '';
                        if (item && (item.categoryId !== undefined && item.categoryId !== null)) {
                            var foundCat = (Array.isArray(categoriesCache) ? categoriesCache : []).find(function(c){ return c && (c.id == item.categoryId || String(c.id) === String(item.categoryId)); });
                            if (foundCat && foundCat.name) {
                                categoryCell = escapeHtml(foundCat.name);
                            } else {
                                categoryCell = String(item.categoryId);
                            }
                        } else if (item && item.categoryName) {
                            categoryCell = escapeHtml(item.categoryName);
                        } else if (item && item.category && item.category.name) {
                            categoryCell = escapeHtml(item.category.name);
                        }
                        var latCell = (item && (item.latitude !== undefined && item.latitude !== null)) ? item.latitude : (item && item.Latitude !== undefined) ? item.Latitude : '';
                        var lngCell = (item && (item.longitude !== undefined && item.longitude !== null)) ? item.longitude : (item && item.Longitude !== undefined) ? item.Longitude : '';
                        var descTitle = (item && item.description) ? escapeHtml(item.description) : '';
                        var descCell = escapeHtml(truncateText(item && item.description ? item.description : '', 80));

                var html = '';
                // show id when available, otherwise show slug for convenience
                html += '<td>' + (idCell || slugCell) + '</td>';
                html += '<td>' + nameCell + '</td>';
                html += '<td>' + slugCell + '</td>';
                html += '<td>' + categoryCell + '</td>';
                html += '<td>' + latCell + '</td>';
                html += '<td>' + lngCell + '</td>';
                // Description view button - opens modal containing the full description
                html += '<td><button class="btn btn-sm btn-outline-secondary btn-view-desc" data-slug="' + slugVal + '" data-id="' + idCell + '">Xem</button></td>';
                // Gallery view button - opens gallery modal
                html += '<td><button class="btn btn-sm btn-outline-secondary btn-view-gallery" data-slug="' + slugVal + '" data-id="' + idCell + '">Xem</button></td>';
                html += '<td class="table-actions">';
                // use data-slug for edit (we fetch full details by slug), keep data-id if present for direct delete
                html += '<button class="btn btn-sm btn-outline-primary btn-edit" data-slug="' + slugVal + '">Sửa</button>';
                html += '<button class="btn btn-sm btn-outline-danger btn-delete" data-id="' + idCell + '" data-slug="' + slugVal + '">Xóa</button>';
                html += '</td>';

                tr.innerHTML = html;
                if (slugVal) tr.dataset.slug = slugVal;
                tableBody.appendChild(tr);
            }

            // Attach row action handlers
            var editBtns = tableBody.querySelectorAll('.btn-edit');
            for (var k = 0; k < editBtns.length; k++) {
                (function(b){
                    b.addEventListener('click', function(){ openEditPanel(b.dataset.slug); });
                })(editBtns[k]);
            }
            var delBtns = tableBody.querySelectorAll('.btn-delete');
            for (var k2 = 0; k2 < delBtns.length; k2++) {
                (function(b){
                    b.addEventListener('click', function(){
                        var id = b.dataset.id;
                        var slug = b.dataset.slug;
                        if (id) {
                            confirmDelete(id);
                        } else if (slug) {
                            // try to resolve id from cached list
                            var found = locationsCache.find(function(x){ return x && x.slug === slug; });
                            if (found && found.id) {
                                confirmDelete(found.id);
                            } else {
                                // fetch by slug to obtain id then delete
                                fetch(listApi + '/' + encodeURIComponent(slug), { method: 'GET', credentials: 'same-origin' })
                                    .then(function(r){ if (!r.ok) throw new Error('Không tìm thấy'); return r.json(); })
                                    .then(function(obj){ if (obj && obj.id) confirmDelete(obj.id); else showAlert('Không thể xác định ID để xóa', 'warning'); })
                                    .catch(function(err){ showAlert('Không thể xóa: ' + err.message, 'danger'); });
                            }
                        } else {
                            showAlert('Không có thông tin để xóa', 'warning');
                        }
                    });
                })(delBtns[k2]);
            }
            // Attach View buttons handlers (description and gallery)
            var viewDescBtns = tableBody.querySelectorAll('.btn-view-desc');
            for (var v = 0; v < viewDescBtns.length; v++) {
                (function(b){
                    b.addEventListener('click', function(){
                        openViewModal('desc', b.dataset.slug, b.dataset.id);
                    });
                })(viewDescBtns[v]);
            }
            var viewGalleryBtns = tableBody.querySelectorAll('.btn-view-gallery');
            for (var v2 = 0; v2 < viewGalleryBtns.length; v2++) {
                (function(b){
                    b.addEventListener('click', function(){
                        openViewModal('gallery', b.dataset.slug, b.dataset.id);
                    });
                })(viewGalleryBtns[v2]);
            }
        }

        // Populate category select options from categories API (/api/theloai)
        async function populateCategorySelect() {
            var sel = document.getElementById('place-category');
            if (!sel) return;
            try {
                if (!Array.isArray(categoriesCache) || categoriesCache.length === 0) {
                    var res = await fetch(categoriesApi, { method: 'GET', credentials: 'same-origin' });
                    if (res.ok) {
                        var cats = await res.json();
                        categoriesCache = Array.isArray(cats) ? cats : [];
                    } else {
                        console.warn('Không thể tải categories:', res.status);
                        categoriesCache = [];
                    }
                }
            } catch (err) {
                console.error('Lỗi khi tải categories:', err);
                categoriesCache = [];
            }
            // rebuild select options
            sel.innerHTML = '';
            var placeholder = document.createElement('option');
            placeholder.value = '';
            placeholder.textContent = '-- Chọn category --';
            sel.appendChild(placeholder);
            // sort by id numeric if possible, otherwise by name
            var arr = Array.isArray(categoriesCache) ? categoriesCache.slice() : [];
            arr.sort(function(a,b){
                var ai = (a && a.id) ? Number(a.id) : 0;
                var bi = (b && b.id) ? Number(b.id) : 0;
                if (!isNaN(ai) && !isNaN(bi)) return ai - bi;
                var an = a && a.name ? a.name : '';
                var bn = b && b.name ? b.name : '';
                return an.localeCompare(bn);
            });
            arr.forEach(function(c){
                if (!c) return;
                var opt = document.createElement('option');
                opt.value = c.id != null ? String(c.id) : '';
                opt.textContent = c.name != null ? c.name : opt.value;
                sel.appendChild(opt);
            });
        }

        function escapeHtml(unsafe) {
            if (unsafe == null) return '';
            return String(unsafe)
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;")
                .replace(/'/g, "&#039;");
        }

        // Open panel for new record
        function openAddPanel() {
            document.getElementById('editModalLabel').textContent = 'Thêm địa điểm mới';
            document.getElementById('place-id').value = '';
            placeForm.reset();
            galleryPreview.innerHTML = '';
            deleteInsideBtn.classList.add('d-none');
            clearModalError();
            // focus name for convenience
            setTimeout(function(){ var n = document.getElementById('place-name'); if (n) n.focus(); }, 200);
            editModal.show();
        }

        // Open panel for editing an existing record (we fetch full details via public API by slug)
        async function openEditPanel(slug) {
            try {
                clearModalError();
                if (!slug) throw new Error('Slug không hợp lệ');
                // Use public API to get full object by slug (this endpoint returns detailed object)
                var res = await fetch(listApi + '/' + encodeURIComponent(slug), { method: 'GET', credentials: 'same-origin' });
                if (!res.ok) {
                    throw new Error('Không tìm thấy địa điểm');
                }
                var item = await res.json();
                document.getElementById('editModalLabel').textContent = 'Chỉnh sửa địa điểm #' + (item.id || item.slug || '');
                document.getElementById('place-id').value = item.id || '';
                document.getElementById('place-name').value = item.name || '';
                document.getElementById('place-slug').value = item.slug || '';
                document.getElementById('place-lat').value = item.latitude || item.Latitude || '';
                document.getElementById('place-lng').value = item.longitude || item.Longitude || '';
                document.getElementById('place-category').value = item.categoryId || '';
                document.getElementById('place-desc').value = item.description || '';
                document.getElementById('place-image').value = '';
                renderGalleryPreview(item.gallery || []);
                deleteInsideBtn.classList.remove('d-none');
                deleteInsideBtn.onclick = function() { if (item.id) confirmDelete(item.id, true); else showAlert('Không có ID để xóa', 'warning'); };
                editModal.show();
            } catch (err) {
                showModalError('Không thể mở bản ghi để sửa: ' + err.message, 'danger');
                console.error(err);
            }
        }

        function renderGalleryPreview(galleryArray) {
            galleryPreview.innerHTML = '';
            if (!Array.isArray(galleryArray) || galleryArray.length === 0) return;
            for (var i = 0; i < galleryArray.length; i++) {
                var url = galleryArray[i];
                var wrap = document.createElement('div');
                wrap.className = 'img-wrap';
                var img = document.createElement('img');
                img.src = ctx + (url && url.startsWith && url.startsWith('/') ? '' : '/') + url;
                img.alt = '';
                img.style.maxHeight = '60px';
                img.style.display = 'block';
                wrap.appendChild(img);
                var rem = document.createElement('button');
                rem.type = 'button';
                rem.className = 'img-remove';
                rem.title = 'Xóa ảnh';
                rem.textContent = '×';
                rem.dataset.url = url;
                wrap.appendChild(rem);
                galleryPreview.appendChild(wrap);
            }
        }
        // Ensure view modal exists in DOM (create on demand)
        function ensureViewModal() {
            if (document.getElementById('viewModal')) return;
            var div = document.createElement('div');
            div.innerHTML = '\
<div class="modal fade" id="viewModal" tabindex="-1" aria-hidden="true">\
  <div class="modal-dialog modal-lg modal-dialog-centered">\
    <div class="modal-content">\
      <div class="modal-header">\
        <h5 class="modal-title"></h5>\
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>\
      </div>\
      <div class="modal-body" style="max-height:70vh; overflow:auto;">\
        <!-- content injected dynamically -->\
      </div>\
    </div>\
  </div>\
</div>';
            document.body.appendChild(div.firstElementChild);
        }
        // Open view modal for description or gallery
        function openViewModal(mode, slug, id) {
            ensureViewModal();
            var modalEl = document.getElementById('viewModal');
            var modal = new bootstrap.Modal(modalEl);
            var titleEl = modalEl.querySelector('.modal-title');
            var bodyEl = modalEl.querySelector('.modal-body');

            // helper: normalize gallery item into URL string supporting different shapes
            function resolveImageUrl(g) {
                if (!g) return null;
                if (typeof g === 'string') return g;
                // common property names
                if (g.image_url) return g.image_url;
                if (g.imageUrl) return g.imageUrl;
                if (g.url) return g.url;
                if (g.path) return g.path;
                // as last resort, try JSON stringify fallback (not useful as URL)
                return null;
            }

            // find item by id or slug in cache
            // find item by id or slug (use cache as fallback)
            var item = null;
            if (id) {
                item = locationsCache.find(function(x){ return x && String(x.id) === String(id); });
            }
            var cachedItem = null;
            if (slug) {
                cachedItem = locationsCache.find(function(x){ return x && x.slug === slug; });
            }
            // Always try to fetch full item by slug when available; fallback to cache if fetch fails
            var promise;
            if (slug) {
                promise = fetch(listApi + '/' + encodeURIComponent(slug), { method: 'GET', credentials: 'same-origin' })
                    .then(function(r){ if (!r.ok) throw new Error('Không tìm thấy'); return r.json(); })
                    .catch(function(){ return cachedItem || item || null; });
            } else {
                promise = Promise.resolve(item);
            }

            promise.then(function(it){
                if (!it) {
                    titleEl.textContent = 'Không tìm thấy';
                    bodyEl.innerHTML = '<div class="text-muted">Không tìm thấy nội dung để hiển thị.</div>';
                    modal.show();
                    return;
                }

                if (mode === 'desc') {
                    titleEl.textContent = 'Mô tả: ' + (it.name || '');
                    var text = it.description || it.Description || it.desc || '';
                    bodyEl.innerHTML = '<div style="white-space:pre-wrap;">' + escapeHtml(text) + '</div>';
                    // scroll modal body top for visibility
                    setTimeout(function(){ if (bodyEl.scrollTo) bodyEl.scrollTo({ top: 0, behavior: 'smooth' }); else bodyEl.scrollTop = 0; }, 50);
                    modal.show();
                    return;
                }

                if (mode === 'gallery') {
                    titleEl.textContent = 'Gallery: ' + (it.name || '');

                    // build normalized array of image URLs (strings)
                    var gallery = [];
                    if (Array.isArray(it.gallery)) {
                        for (var gi = 0; gi < it.gallery.length; gi++) {
                            var g = it.gallery[gi];
                            var u = resolveImageUrl(g);
                            if (u) gallery.push(u);
                        }
                    }
                    // fallbacks: 'avata' (typo in model), or direct image fields
                    if ((!gallery || gallery.length === 0) && it.avata) gallery = [it.avata];
                    if ((!gallery || gallery.length === 0) && it.image) {
                        var u2 = resolveImageUrl(it.image);
                        if (u2) gallery = [u2];
                    }

                    // if still empty, show message
                    if (!gallery || gallery.length === 0) {
                        bodyEl.innerHTML = '<div class="text-muted">Không có ảnh để hiển thị.</div>';
                        modal.show();
                        return;
                    }

                    // build gallery layout: main image + thumbnails row
                    var mainId = 'view-main-img';
                    var thumbsId = 'view-thumbs';
                    var mainHeight = 400; // fixed height in px for main image, width scales automatically
                    var html = '<div class="d-flex flex-column align-items-center">\
<div style="width:100%;text-align:center;margin-bottom:8px">\
  <img id="' + mainId + '" src="' + (gallery[0] ? (ctx + (gallery[0].startsWith('/') ? '' : '/') + gallery[0]) : '') + '" style="height:' + mainHeight + 'px;width:auto;object-fit:contain;border-radius:4px" alt="">\
</div>\
<div id="' + thumbsId + '" class="d-flex flex-row flex-wrap justify-content-center" style="gap:8px">\
</div>\
</div>';
                    bodyEl.innerHTML = html;

                    var thumbsEl = bodyEl.querySelector('#' + thumbsId);
                    var mainImg = bodyEl.querySelector('#' + mainId);

                    // function to set active thumbnail styling
                    function markActiveThumb(selEl) {
                        var imgs = thumbsEl.querySelectorAll('img');
                        imgs.forEach(function(im){ im.style.border = '2px solid transparent'; im.style.opacity = '0.9'; });
                        if (selEl) { selEl.style.border = '2px solid #0d6efd'; selEl.style.opacity = '1'; }
                    }

                    // create thumbnails and attach handlers
                    for (var i = 0; i < gallery.length; i++) {
                        (function(idx){
                            var u = gallery[idx];
                            var src = ctx + (u && u.startsWith && u.startsWith('/') ? '' : '/') + u;
                            var t = document.createElement('img');
                            t.src = src;
                            t.alt = '';
                            t.style.height = '80px'; // fixed thumbnail height
                            t.style.width = 'auto';
                            t.style.cursor = 'pointer';
                            t.style.borderRadius = '3px';
                            t.style.boxShadow = '0 1px 3px rgba(0,0,0,0.2)';
                            t.style.transition = 'border .15s, opacity .15s';
                            t.addEventListener('click', function(){
                                if (mainImg) {
                                    mainImg.src = src;
                                }
                                markActiveThumb(t);
                            });
                            thumbsEl.appendChild(t);
                        })(i);
                    }

                    // mark first thumb active
                    setTimeout(function(){
                        var firstThumb = thumbsEl.querySelector('img');
                        if (firstThumb) markActiveThumb(firstThumb);
                        // scroll modal body to top and focus main image for accessibility
                        try {
                            if (bodyEl.scrollTo) bodyEl.scrollTo({ top: 0, behavior: 'smooth' }); else bodyEl.scrollTop = 0;
                            if (mainImg && mainImg.focus) mainImg.setAttribute('tabindex', '-1'); try { mainImg.focus(); } catch (e) {}
                        } catch (e) { /* ignore */ }
                    }, 50);

                    modal.show();
                    return;
                }

                // unknown mode -> fallback
                titleEl.textContent = 'Xem';
                bodyEl.innerHTML = '<div class="text-muted">Không có nội dung để hiển thị.</div>';
                modal.show();
            });
        }

        // Confirm then delete
        async function confirmDelete(id, closeModalAfter) {
            if (!confirm('Bạn có chắc muốn xóa địa điểm #' + id + ' ?')) return;
            closeModalAfter = !!closeModalAfter;
            try {
                var res = await fetch(apiBase + '/' + encodeURIComponent(id), { method: 'DELETE', credentials: 'same-origin' });
                if (res.ok) {
                    var json = await res.json().catch(function(){ return {message:'Xóa thành công'}; });
                    showAlert(json.message || 'Xóa thành công');
                    if (closeModalAfter) editModal.hide();
                    fetchList();
                } else if (res.status === 404) {
                    var json2 = await res.json().catch(function(){ return {message:'Không tìm thấy'}; });
                    showAlert(json2.message || 'Không tìm thấy', 'warning');
                } else if (res.status === 401) {
                    showAlert('Cần đăng nhập', 'warning');
                } else {
                    var text = await res.text();
                    showAlert('Lỗi khi xóa: ' + text, 'danger');
                }
            } catch (err) {
                console.error(err);
                showAlert('Lỗi khi xóa: ' + err.message, 'danger');
            }
        }

        // Save (create or update)
        placeForm.addEventListener('submit', async function (ev) {
            ev.preventDefault();
            var id = document.getElementById('place-id').value;
            var payload = {
                name: document.getElementById('place-name').value.trim(),
                slug: document.getElementById('place-slug').value.trim(),
                latitude: parseFloat(document.getElementById('place-lat').value) || null,
                longitude: parseFloat(document.getElementById('place-lng').value) || null,
                description: document.getElementById('place-desc').value.trim(),
                categoryId: parseInt(document.getElementById('place-category').value, 10) || null
            };

            // Generate slug from name if missing
            var slugInputEl = document.getElementById('place-slug');
            if ((!slugInputEl.value || slugInputEl.value.trim() === '') && payload.name) {
                // Logic sửa đổi:
                var genSlug = payload.name.toLowerCase().trim()
                    .normalize('NFD').replace(/[\u0300-\u036f]/g, '') 
                    .replace(/đ/g, 'd')                               
                    .replace(/[^a-z0-9\s-]/g, '')                     
                    .replace(/\s+/g, '-')                              
                    .replace(/-+/g, '-')                               
                    .replace(/^-+|-+$/g, '');                          
                    
                slugInputEl.value = genSlug;
                payload.slug = genSlug;
            }

            // Field-specific validation and show inside modal
            var missing = [];
            if (!payload.name) missing.push('Tên (name)');
            if (payload.latitude == null) missing.push('Latitude');
            if (payload.longitude == null) missing.push('Longitude');
            if (!payload.categoryId) missing.push('Category');
            if (missing.length) {
                showModalError('Vui lòng điền: ' + missing.join(', '), 'warning');
                return;
            }

            // Client-side check for slug uniqueness (for create). For update allow same id.
            if (payload.slug) {
                var existing = locationsCache.find(function(x){ return x && x.slug === payload.slug; });
                var currentId = document.getElementById('place-id').value;
                if (existing && (!currentId || String(existing.id) !== String(currentId))) {
                    showModalError('Slug đã tồn tại. Vui lòng sửa slug hoặc đổi tên.', 'warning');
                    return;
                }
            }

            try {
                var res;
                if (!id) {
                    res = await fetch(apiBase, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        credentials: 'same-origin',
                        body: JSON.stringify(payload)
                    });
                } else {
                    res = await fetch(apiBase + '/' + encodeURIComponent(id), {
                        method: 'PUT',
                        headers: { 'Content-Type': 'application/json' },
                        credentials: 'same-origin',
                        body: JSON.stringify(payload)
                    });
                }

                // Improved error handling: attempt to parse JSON error body (if any) to show server message.
                if (!res.ok) {
                    var errMsg = '';
                    try {
                        var ct = res.headers.get('Content-Type') || '';
                        if (ct.indexOf('application/json') !== -1) {
                            var errJson = await res.json();
                            if (errJson && errJson.message) {
                                errMsg = errJson.message;
                            } else {
                                // fallback to stringify the JSON
                                errMsg = JSON.stringify(errJson);
                            }
                        } else {
                            // not JSON, try plain text
                            errMsg = await res.text();
                        }
                    } catch (parseErr) {
                        // reading/parsing body failed - leave errMsg empty
                        console.warn('Failed to parse error body:', parseErr);
                    }

                    var userMsg = 'Server trả lỗi: ' + res.status + (errMsg ? ' - ' + errMsg : '');
                    throw new Error(userMsg);
                }

                var record = await res.json();

                var filesInput = document.getElementById('place-image');
                if (filesInput && filesInput.files && filesInput.files.length > 0) {
                    var form = new FormData();
                    for (var i = 0; i < filesInput.files.length; i++) {
                        form.append('files', filesInput.files[i]);
                    }

                    try {
                        var uploadRes = await fetch(apiBase + '/' + encodeURIComponent(record.id) + '/gallery', {
                            method: 'POST',
                            credentials: 'same-origin',
                            body: form
                        });
                        if (!uploadRes.ok) {
                            // try to extract meaningful error from upload response as well
                            var uploadMsg = '';
                            try {
                                var uct = uploadRes.headers.get('Content-Type') || '';
                                if (uct.indexOf('application/json') !== -1) {
                                    var uj = await uploadRes.json();
                                    uploadMsg = uj && uj.message ? uj.message : JSON.stringify(uj);
                                } else {
                                    uploadMsg = await uploadRes.text();
                                }
                            } catch (ue) { console.warn('Cannot parse upload error body', ue); }
                            console.warn('Upload gallery trả lỗi', uploadRes.status, uploadMsg);
                            showAlert('Lưu bản ghi thành công nhưng upload ảnh thất bại' + (uploadMsg ? (': ' + uploadMsg) : ' (kiểm tra endpoint upload).'), 'warning');
                        } else {
                            showAlert('Lưu và upload ảnh thành công.');
                        }
                    } catch (uploadErr) {
                        console.error('Lỗi upload:', uploadErr);
                        showAlert('Lưu bản ghi nhưng có lỗi upload ảnh: ' + uploadErr.message, 'warning');
                    }
                } else {
                    showAlert(id ? 'Cập nhật thành công' : 'Tạo mới thành công');
                }

                editModal.hide();
                fetchList();
            } catch (err) {
                console.error(err);
                var messageToShow = (err && err.message) ? err.message : String(err);
                // Display server/client error inside the modal so user can correct issues that modal covers
                showModalError('Lỗi khi lưu: ' + messageToShow, 'danger');
                // keep modal open for user to correct
            }
        });

        // Event wiring
        document.getElementById('add-button').addEventListener('click', openAddPanel);
        document.getElementById('refresh-button').addEventListener('click', fetchList);

        // Auto-generate slug from name when empty, and check slug uniqueness on blur
        (function attachSlugHelpers(){
            var nameEl = document.getElementById('place-name');
            var slugEl = document.getElementById('place-slug');
            
            if (nameEl && slugEl) {
                nameEl.addEventListener('blur', function(){
                    if (!slugEl.value || slugEl.value.trim() === '') {
                        var val = nameEl.value.toLowerCase().trim();
                        val = val.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
                        val = val.replace(/đ/g, 'd');
                        val = val.replace(/[^a-z0-9\s-]/g, '');
                        val = val.replace(/\s+/g, '-');
                        val = val.replace(/-+/g, '-');

                        val = val.replace(/^-+|-+$/g, '');

                        slugEl.value = val;
                    }
                });
                slugEl.addEventListener('blur', function(){
                    var s = slugEl.value && slugEl.value.trim();
                    if (!s) return;
                    if (typeof locationsCache !== 'undefined') {
                        var existing = locationsCache.find(function(x){ return x && x.slug === s; });
                        var currentId = document.getElementById('place-id').value;
                        if (existing && (!currentId || String(existing.id) !== String(currentId))) {
                            showModalError('Slug đã tồn tại. Vui lòng sửa slug hoặc đổi tên.', 'warning');
                        } else {
                            clearModalError();
                        }
                    }
                });
            }
        })();

        // Logout button
        document.getElementById('logout-button').addEventListener('click', function () {
            fetch(ctx + '/api/admin/logout', { method: 'POST', credentials: 'same-origin' })
                .then(function(r){ return r.json().catch(function(){ return {}; }); })
                .then(function(data){ window.location.href = ctx + '/index'; })
                .catch(function(err){ window.location.href = ctx + '/index'; });
        });

        // Preview selected images before upload
        document.getElementById('place-image').addEventListener('change', function (ev) {
            var files = ev.target.files;
            if (!files || files.length === 0) return;
            // NOTE: do NOT clear existing galleryPreview here; we append previews so existing images remain visible
            Array.prototype.forEach.call(files, function(file){
                if (!file.type || !file.type.startsWith('image/')) return;
                var wrap = document.createElement('div');
                wrap.className = 'img-wrap';
                var img = document.createElement('img');
                img.style.maxHeight = '60px';
                img.style.display = 'block';
                wrap.appendChild(img);
                var rem = document.createElement('button');
                rem.type = 'button';
                rem.className = 'img-remove';
                rem.title = 'Xóa ảnh (chỉ preview)';
                rem.textContent = '×';
                // mark as temp preview (no server url yet)
                rem.dataset.temp = '1';
                wrap.appendChild(rem);
                galleryPreview.appendChild(wrap);
                var reader = new FileReader();
                reader.onload = function (e) { img.src = e.target.result; };
                reader.readAsDataURL(file);
            });
        });


        // Handle click on remove (X) overlay inside gallery preview in edit panel
        galleryPreview.addEventListener('click', function (ev) {
            var btn = ev.target.closest('.img-remove');
            if (!btn) return;
            var url = btn.dataset.url;
            var wrap = btn.closest('.img-wrap');
            if (!wrap) return;
            var id = document.getElementById('place-id').value;
            if (!url) {
                // Remove preview of newly selected image (does not affect file input)
                wrap.parentNode.removeChild(wrap);
                return;
            }
            if (!id) {
                // Not editing an existing record - just remove from UI
                wrap.parentNode.removeChild(wrap);
                showAlert('Đã xóa ảnh khỏi preview (bản ghi chưa lưu).', 'info');
                return;
            }
            if (!confirm('Bạn có chắc muốn xóa ảnh này?')) return;

            // Try query-param DELETE first
            fetch(apiBase + '/gallery?id=' + encodeURIComponent(id) + '&url=' + encodeURIComponent(url), {
                method: 'DELETE',
                credentials: 'same-origin'
            }).then(function (r) {
                if (r.ok) {
                    showAlert('Ảnh đã được xóa.');
                    wrap.parentNode.removeChild(wrap);
                } else {
                    // fallback: try DELETE to /{id}/gallery with JSON body {url}
                    r.text().then(function (t) {
                        fetch(apiBase + '/' + encodeURIComponent(id) + '/gallery', {
                            method: 'DELETE',
                            credentials: 'same-origin',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify({ url: url })
                        }).then(function (r2) {
                            if (r2.ok) {
                                showAlert('Ảnh đã được xóa.');
                                wrap.parentNode.removeChild(wrap);
                            } else {
                                r2.text().then(function (t2) {
                                    showAlert('Xóa ảnh thất bại: ' + (t2 || t || 'Unknown'), 'danger');
                                });
                            }
                        }).catch(function (err2) {
                            console.error(err2);
                            showAlert('Lỗi khi xóa ảnh: ' + err2.message, 'danger');
                        });
                    }).catch(function(){
                        showAlert('Xóa ảnh thất bại.', 'danger');
                    });
                }
            }).catch(function (err) {
                console.error(err);
                showAlert('Lỗi khi xóa ảnh: ' + err.message, 'danger');
            });
        });

        // Row click convenience removed — now edit panel opens only via the Edit button
        // (Previously clicking a row opened the edit modal; that behavior is disabled.)

        // Initial load
        fetchList();

    })();
</script>
</body>
</html>
