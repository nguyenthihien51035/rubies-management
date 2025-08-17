package com.example.rubiesmanagement.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, String subFolder);

    void deleteFileByUrl(String url);

    boolean isValidImage(MultipartFile file);

}
