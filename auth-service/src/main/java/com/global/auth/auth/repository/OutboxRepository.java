package com.global.auth.auth.repository;

import com.global.auth.auth.domain.OutboxEvent;
import com.global.auth.auth.domain.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByStatus(OutboxStatus status);
}
