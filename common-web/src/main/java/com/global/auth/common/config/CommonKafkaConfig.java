package com.global.auth.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * 전사 공통 Kafka 에러 핸들링 및 재시도 설정
 */
@Slf4j
@Configuration
public class CommonKafkaConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        // 1. 실패 시 1초 간격으로 최대 3번 재시도
        FixedBackOff fixedBackOff = new FixedBackOff(1000L, 2L);

        // 2. 재시도 끝에 실패하면 {토픽명}.DLT 토픽으로 메시지 격리
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
                (record, ex) -> {
                    log.error(">>>> Moving to DLQ: Topic: {}, Partition: {}, Offset: {}, Error: {}",
                            record.topic(), record.partition(), record.offset(), ex.getMessage());
                    return null; // 기본값인 {topic}.DLT 사용
                });

        return new DefaultErrorHandler(recoverer, fixedBackOff);
    }
}
