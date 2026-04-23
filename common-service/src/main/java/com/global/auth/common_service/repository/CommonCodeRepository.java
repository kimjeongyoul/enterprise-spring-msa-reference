package com.global.auth.common_service.repository;

import com.global.auth.common_service.domain.CommonCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonCodeRepository extends JpaRepository<CommonCodeEntity, Long> {
    List<CommonCodeEntity> findByGroupCodeAndIsUsedTrueOrderBySortOrderAsc(String groupCode);
}
