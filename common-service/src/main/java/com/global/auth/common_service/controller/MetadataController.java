package com.global.auth.common_service.controller;

import com.global.auth.common.dto.CommonCodeResponse;
import com.global.auth.common_service.domain.CommonCodeEntity;
import com.global.auth.common_service.repository.CommonCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/metadata")
@RequiredArgsConstructor
public class MetadataController {

    private final CommonCodeRepository commonCodeRepository;

    @GetMapping("/codes/{groupCode}")
    public List<CommonCodeResponse> getCodesByGroup(@PathVariable String groupCode) {
        return commonCodeRepository.findByGroupCodeAndIsUsedTrueOrderBySortOrderAsc(groupCode)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private CommonCodeResponse toResponse(CommonCodeEntity entity) {
        return CommonCodeResponse.builder()
                .groupCode(entity.getGroupCode())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .sortOrder(entity.getSortOrder())
                .isUsed(entity.isUsed())
                .build();
    }
}
