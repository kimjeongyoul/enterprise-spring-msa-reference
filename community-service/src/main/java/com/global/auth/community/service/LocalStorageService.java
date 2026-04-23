package com.global.auth.community.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@Profile("!prod") // 운영(prod) 환경이 아닐 때 사용
public class LocalStorageService implements StorageService {

    @Override
    public String upload(MultipartFile file) {
        String fileId = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        log.info("[Local Storage] Uploading file: {} to local disk", fileId);
        return "/uploads/" + fileId; // 로컬 가상 경로 반환
    }

    @Override
    public void delete(String fileId) {
        log.info("[Local Storage] Deleting file: {} from local disk", fileId);
    }

    @Override
    public String getUrl(String fileId) {
        return "http://localhost:8084" + fileId;
    }
}
