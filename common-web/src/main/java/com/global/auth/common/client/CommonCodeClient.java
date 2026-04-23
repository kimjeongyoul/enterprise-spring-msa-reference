package com.global.auth.common.client;

import com.global.auth.common.dto.CommonCodeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Metadata Management Service (common-service) 통신용 인터페이스
 */
public interface CommonCodeClient {
    
    @GetMapping("/api/v1/metadata/codes/{groupCode}")
    List<CommonCodeResponse> getCodesByGroup(@PathVariable("groupCode") String groupCode);

    @GetMapping("/api/v1/metadata/codes/{groupCode}/{code}")
    CommonCodeResponse getCode(@PathVariable("groupCode") String groupCode, @PathVariable("code") String code);
}
