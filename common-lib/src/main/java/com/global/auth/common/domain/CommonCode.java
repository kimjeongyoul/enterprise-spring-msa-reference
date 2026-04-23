package com.global.auth.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 전사 표준 공통 코드 모델 (Group-Detail 계층 구조)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonCode {
    private String code;       // 실제 코드값 (예: KRW, USD)
    private String name;       // 한글 명칭 (예: 원, 달러)
    private String description; // 부가 설명
    private Integer sortOrder; // 정렬 순서
    private boolean isUsed;    // 사용 여부

    /**
     * 중앙 집중식 공통 코드 저장소 (예시용 인메모리 캐시)
     * 실무에서는 DB 조회 후 캐싱하는 로직으로 확장됨.
     */
    private static final Map<String, List<CommonCode>> CODE_REGISTRY = new ConcurrentHashMap<>();

    public static void register(String groupCode, List<CommonCode> codes) {
        CODE_REGISTRY.put(groupCode, codes);
    }

    public static List<CommonCode> getGroup(String groupCode) {
        return CODE_REGISTRY.getOrDefault(groupCode, List.of());
    }
}
