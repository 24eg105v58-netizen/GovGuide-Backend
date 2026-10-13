package com.govguide.repository;

import com.govguide.entity.EligibilityCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EligibilityCheckRepository extends JpaRepository<EligibilityCheck, Long> {
    List<EligibilityCheck> findByUserIdOrderByCheckedAtDesc(Long userId);
    Page<EligibilityCheck> findByUserIdOrderByCheckedAtDesc(Long userId, Pageable pageable);
    long countByUserId(Long userId);
}
