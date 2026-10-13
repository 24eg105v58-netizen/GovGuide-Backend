package com.govguide.controller;

import com.govguide.dto.AdminDashboardStatsDto;
import com.govguide.dto.SchemeDto;
import com.govguide.dto.UserDto;
import com.govguide.entity.AdminAuditLog;
import com.govguide.entity.User;
import com.govguide.exception.ResourceNotFoundException;
import com.govguide.repository.UserRepository;
import com.govguide.service.AdminService;
import com.govguide.service.AuditLogService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    public AdminController(AdminService adminService,
                           AuditLogService auditLogService,
                           UserRepository userRepository) {
        this.adminService = adminService;
        this.auditLogService = auditLogService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getUsers(search, page, size));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<UserDto> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        String status = body.get("status");
        User admin = getAdmin(authentication);
        UserDto updated = adminService.updateUserStatus(id, status, admin.getId(), admin.getEmail());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/schemes")
    public ResponseEntity<SchemeDto> createScheme(
            @Valid @RequestBody SchemeDto dto,
            Authentication authentication) {
        User admin = getAdmin(authentication);
        SchemeDto created = adminService.createScheme(dto, admin.getId(), admin.getEmail());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/schemes/{id}")
    public ResponseEntity<SchemeDto> updateScheme(
            @PathVariable Long id,
            @Valid @RequestBody SchemeDto dto,
            Authentication authentication) {
        User admin = getAdmin(authentication);
        SchemeDto updated = adminService.updateScheme(id, dto, admin.getId(), admin.getEmail());
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/schemes/{id}/verification")
    public ResponseEntity<SchemeDto> updateVerification(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        String status = body.get("verificationStatus");
        User admin = getAdmin(authentication);
        SchemeDto updated = adminService.updateVerificationStatus(id, status, admin.getId(), admin.getEmail());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/schemes/{id}")
    public ResponseEntity<Map<String, String>> deleteScheme(
            @PathVariable Long id,
            Authentication authentication) {
        User admin = getAdmin(authentication);
        adminService.deleteScheme(id, admin.getId(), admin.getEmail());
        return ResponseEntity.ok(Map.of("message", "Scheme successfully deleted"));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<Page<AdminAuditLog>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(auditLogService.getLogs(PageRequest.of(page, size)));
    }

    private User getAdmin(Authentication authentication) {
        if (authentication == null) {
            throw new ResourceNotFoundException("Not authenticated");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));
    }
}
