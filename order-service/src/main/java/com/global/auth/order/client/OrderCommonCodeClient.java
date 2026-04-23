package com.global.auth.order.client;

import com.global.auth.common.client.CommonCodeClient;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * order-service용 공통 코드 클라이언트
 */
@FeignClient(name = "common-service", url = "${common-service.url}")
public interface OrderCommonCodeClient extends CommonCodeClient {
}
