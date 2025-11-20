param(
    [string]$ExternalUploads = "D:\hoian_uploads",
    [string]$TomcatWebappsHoian = "D:\apache-tomcat-9.0.111\wtpwebapps\hoianweb",
    [switch]$RestartTomcat = $false
)

function Write-Log {
    param([string]$msg)
    Write-Host "$(Get-Date -Format 'HH:mm:ss') - $msg"
}

function Ensure-Folder {
    param([string]$path)
    if (-Not (Test-Path $path)) {
        Write-Log "Tạo folder: $path"
        New-Item -Path $path -ItemType Directory -Force | Out-Null
    } else {
        Write-Log "Folder tồn tại: $path"
    }
}

function Ensure-Junction {
    param([string]$link, [string]$target)
    if (Test-Path $link) {
        $attr = Get-Item $link -Force
        if ($attr.Attributes -band [System.IO.FileAttributes]::ReparsePoint) {
            Write-Log "Junction tồn tại: $link"
        } else {
            Write-Log "Một file/folder khác đã tồn tại tại $link — hãy kiểm tra thủ công."
        }
    } else {
        Write-Log "Tạo junction: $link -> $target"
        $cmd = "mklink /J `"$link`" `"$target`""
        cmd /c $cmd | Out-Null
        Write-Log "Đã tạo junction."
    }
}

# ---- MAIN ----
Write-Log "=== Bắt đầu setup uploads mapping ==="

# 1) ensure external uploads folder
Ensure-Folder -path $ExternalUploads

# 2) check webapp folder
if (-not (Test-Path $TomcatWebappsHoian)) {
    Write-Log "ERROR: Webapp folder không tồn tại: $TomcatWebappsHoian"
    exit 1
} else {
    Write-Log "Webapp folder tồn tại: $TomcatWebappsHoian"
}

# 3) create uploads junction
$uploadsLink = Join-Path $TomcatWebappsHoian "uploads"
Ensure-Junction -link $uploadsLink -target $ExternalUploads

# 4) grant permissions (skipped vì không chạy service)
Write-Log "Bỏ qua cấp quyền vì Tomcat không chạy dưới Windows service"

# 5) restart Tomcat (skipped)
if ($RestartTomcat) {
    Write-Log "Bỏ qua restart Tomcat vì Tomcat chạy trong Eclipse, không phải Windows service"
}

Write-Log "=== Hoàn tất. Kiểm tra: http://localhost:8080/hoianweb/uploads/ ==="
