package com.govguide.repository;

import com.govguide.entity.SchemeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchemeDocumentRepository extends JpaRepository<SchemeDocument, Long> {
    List<SchemeDocument> findBySchemeId(Long schemeId);
}
