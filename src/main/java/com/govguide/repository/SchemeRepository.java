package com.govguide.repository;

import com.govguide.entity.Scheme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchemeRepository extends JpaRepository<Scheme, Long> {

    Optional<Scheme> findBySchemeCode(String schemeCode);

    List<Scheme> findByPublicationStatus(String publicationStatus);

    Page<Scheme> findByPublicationStatus(String publicationStatus, Pageable pageable);

    long countByGovernmentLevel(String governmentLevel);

    long countByPublicationStatus(String publicationStatus);

    long countByVerificationStatus(String verificationStatus);

    List<Scheme> findTop5ByPublicationStatusOrderByCreatedAtDesc(String publicationStatus);

    @Query("SELECT s FROM Scheme s WHERE s.publicationStatus = 'PUBLISHED' AND (" +
           "LOWER(s.schemeName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.benefits) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.department) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.category) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Scheme> searchPublished(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT s FROM Scheme s WHERE " +
           "(:status IS NULL OR s.publicationStatus = :status) AND " +
           "(:category IS NULL OR s.category = :category) AND " +
           "(:level IS NULL OR s.governmentLevel = :level) AND " +
           "(:keyword IS NULL OR LOWER(s.schemeName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.department) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Scheme> filterSchemes(
            @Param("status") String status,
            @Param("category") String category,
            @Param("level") String level,
            @Param("keyword") String keyword,
            Pageable pageable);
}