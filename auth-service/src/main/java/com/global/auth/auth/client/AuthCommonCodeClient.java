package com.global.auth.auth.client;

import com.global.auth.common.client.CommonCodeClient;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * common-lib의 규약을 사용하여 실제 common-service를 가리키는 Feign 클라이언트
 */
@FeignClient(name = "common-service", url = "${common-service.url}")
public interface AuthCommonCodeClient extends CommonCodeClient {
}
