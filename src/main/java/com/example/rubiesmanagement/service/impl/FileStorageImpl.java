package com.example.rubiesmanagement.service.impl;

import com.example.rubiesmanagement.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageImpl implements FileStorageService {
    private static final String ROOT_UPLOAD_DIR = "uploads";
    private final List<String> VALID_IMAGE_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/bmp",
            "image/webp"
    );

    @Override
    public String storeFile(MultipartFile file, String subFolder) {
        if (file == null || file.isEmpty()) {
            log.warn("File ảnh rỗng hoặc null");
            return null;
        }

        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(ROOT_UPLOAD_DIR, subFolder);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String protocol = "http://";
            String host = "localhost";
            String port = "8080";
            String baseUrl = protocol + host + ":" + port;
            String avatarUrl = baseUrl + "/" + ROOT_UPLOAD_DIR + "/" + subFolder + "/" + fileName;

            if (!Files.exists(filePath)) {
                log.error("File ảnh không được lưu: {}", filePath);
                throw new RuntimeException("Không thể lưu file ảnh: " + fileName);
            }

            log.info("Đã lưu ảnh mới tại: {}", avatarUrl);
            return avatarUrl;

        } catch (IOException e) {
            log.error("Lỗi khi lưu file ảnh: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi lưu file ảnh!", e);
        }
    }

    @Override
    public void deleteFileByUrl(String url) {
        try {
            if (url == null || url.isBlank()) {
                log.warn("URL ảnh không hợp lệ: {}", url);
                return;
            }
            if (!url.startsWith("/uploads/")) {
                log.warn("URL ảnh không đúng định dạng: {}", url);
                return;
            }
            String relativePath = url.replaceFirst("^/uploads/", "");
            Path filePath = Paths.get("uploads").resolve(relativePath);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Đã xóa file ảnh: {}", url);
            } else {
                log.warn("File ảnh không tồn tại: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Lỗi khi xóa file ảnh: {}. Lý do: {}", url, e.getMessage(), e);
        }
    }

    @Override
    public boolean isValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        String contentType = file.getContentType();
        return VALID_IMAGE_TYPES.contains(contentType);
    }
}
