package com.govguide.repository;

import com.govguide.entity.SchemeEligibilityRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchemeEligibilityRuleRepository extends JpaRepository<SchemeEligibilityRule, Long> {
    Optional<SchemeEligibilityRule> findBySchemeId(Long schemeId);
}
