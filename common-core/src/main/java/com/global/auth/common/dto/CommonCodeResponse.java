package com.global.auth.common.dto;

import lombok.*;

import java.io.Serializable;

/**
 * 전사 표준 공통 코드 DTO (Metadata)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CommonCodeResponse implements Serializable {
    private String groupCode;
    private String code;
    private String name;
    private String description;
    private Integer sortOrder;
    private boolean isUsed;
}
