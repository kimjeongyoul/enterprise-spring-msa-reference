package com.global.auth.community.controller;

import com.global.auth.common.dto.ApiResponse;
import com.global.auth.common.dto.PageResponse;
import com.global.auth.community.domain.Review;
import com.global.auth.community.repository.ReviewRepository;
import com.global.auth.community.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
        
        List<String> imageUrls = List.of();
        if (files != null) {
            imageUrls = files.stream()
                    .map(storageService::upload)
                    .collect(Collectors.toList());
        }

        Review review = Review.builder()
                .userId(username)
                .productId(productId)
                .content(content)
                .rating(rating)
                .imageUrls(imageUrls)
                .build();

        return ApiResponse.success(reviewRepository.save(review));
    }

    /**
     * 특정 상품의 리뷰 목록 페이징 조회 (전사 표준 규격 적용)
     */
    @GetMapping("/product/{productId}")
    public ApiResponse<PageResponse<Review>> getReviewsByProduct(
            @PathVariable Long productId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<Review> reviewPage = reviewRepository.findByProductId(productId, pageable);
        return ApiResponse.success(PageResponse.of(reviewPage));
    }
}
