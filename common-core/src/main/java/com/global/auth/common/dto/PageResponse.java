package com.global.auth.common.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 전사 표준 페이징 응답 규격 (ADR 014 확장)
 */
@Getter
public class PageResponse<T> {
    private List<T> content;          // 실제 데이터 리스트
    private int pageNumber;           // 현재 페이지 번호 (0부터 시작)
    private int pageSize;             // 한 페이지당 데이터 개수
    private long totalElements;       // 전체 데이터 개수
    private int totalPages;           // 전체 페이지 수
    private boolean last;              // 마지막 페이지 여부

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(page);
    }
}
