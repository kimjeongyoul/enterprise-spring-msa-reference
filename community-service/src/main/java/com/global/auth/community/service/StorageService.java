package com.global.auth.community.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 저장소 추상화 인터페이스 (ADR 013)
 */
public interface StorageService {
    String upload(MultipartFile file);
    void delete(String fileId);
    String getUrl(String fileId);
}
