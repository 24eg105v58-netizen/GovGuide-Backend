package com.govguide.service;

import com.govguide.entity.AdminAuditLog;
import com.govguide.repository.AdminAuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditLogService {

    private final AdminAuditLogRepository auditLogRepository;

    public AuditLogService(AdminAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void logAction(Long adminUserId, String adminEmail, String action, String entityType, Long entityId, String details) {
        AdminAuditLog log = new AdminAuditLog(adminUserId, adminEmail, action, entityType, entityId, details);
        auditLogRepository.save(log);
    }

    public Page<AdminAuditLog> getLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public List<AdminAuditLog> getRecentLogs() {
        return auditLogRepository.findTop10ByOrderByCreatedAtDesc();
    }
}
