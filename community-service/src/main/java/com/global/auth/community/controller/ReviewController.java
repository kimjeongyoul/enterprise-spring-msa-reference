package com.global.auth.community.controller;

import com.global.auth.common.dto.ApiResponse;
import com.global.auth.community.domain.Review;
import com.global.auth.community.repository.ReviewRepository;
import com.global.auth.community.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewRepository reviewRepository;
    private final StorageService storageService;

    @PostMapping
    public ApiResponse<Review> createReview(
            @RequestHeader("X-User-Name") String username,
            @RequestParam("productId") Long productId,
            @RequestParam("content") String content,
            @RequestParam("rating") Integer rating,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        // 1. 파일 업로드 (추상화된 StorageService 사용)
        List<String> imageUrls = List.of();
        if (files != null) {
            imageUrls = files.stream()
                    .map(storageService::upload)
                    .collect(Collectors.toList());
        }

        // 2. 리뷰 저장
        Review review = Review.builder()
                .userId(username)
                .productId(productId)
                .content(content)
                .rating(rating)
                .imageUrls(imageUrls)
                .build();

        return ApiResponse.success(reviewRepository.save(review));
    }
}
