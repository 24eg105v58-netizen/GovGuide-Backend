package com.govguide.service;

import com.govguide.dto.*;
import com.govguide.entity.*;
import com.govguide.exception.BadRequestException;
import com.govguide.exception.ResourceNotFoundException;
import com.govguide.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final SchemeRepository schemeRepository;
    private final SchemeService schemeService;
    private final AuditLogService auditLogService;

    public AdminService(UserRepository userRepository,
                        SchemeRepository schemeRepository,
                        SchemeService schemeService,
                        AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.schemeRepository = schemeRepository;
        this.schemeService = schemeService;
        this.auditLogService = auditLogService;
    }

    public AdminDashboardStatsDto getDashboardStats() {
        AdminDashboardStatsDto stats = new AdminDashboardStatsDto();
        stats.setTotalUsers(userRepository.countByRole("ROLE_USER"));
        stats.setTotalSchemes(schemeRepository.count());
        stats.setCentralSchemes(schemeRepository.countByGovernmentLevel("CENTRAL"));
        stats.setStateSchemes(schemeRepository.countByGovernmentLevel("STATE"));
        stats.setVerifiedSchemes(schemeRepository.countByVerificationStatus("VERIFIED"));
        stats.setUnverifiedSchemes(schemeRepository.countByVerificationStatus("UNVERIFIED"));
        stats.setRecentSchemes(schemeService.getRecentlyAddedSchemes(null));
        return stats;
    }

    public Page<UserDto> getUsers(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> usersPage;

        if (search != null && !search.isBlank()) {
            usersPage = userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }

        List<UserDto> dtos = usersPage.getContent().stream()
                .map(u -> new UserDto(u.getId(), u.getFullName(), u.getEmail(), u.getMobileNumber(), u.getRole(), u.getPreferredLanguage(), u.getAccountStatus(), u.getCreatedAt()))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, usersPage.getTotalElements());
    }

    @Transactional
    public UserDto updateUserStatus(Long userId, String status, Long adminId, String adminEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if ("ROLE_ADMIN".equals(user.getRole())) {
            throw new BadRequestException("Cannot alter status of administrator accounts directly.");
        }

        user.setAccountStatus(status.toUpperCase());
        User updated = userRepository.save(user);

        auditLogService.logAction(adminId, adminEmail, "UPDATE_USER_STATUS", "User", userId, "Changed status to: " + status);

        return new UserDto(updated.getId(), updated.getFullName(), updated.getEmail(), updated.getMobileNumber(), updated.getRole(), updated.getPreferredLanguage(), updated.getAccountStatus(), updated.getCreatedAt());
    }

    @Transactional
    public SchemeDto createScheme(SchemeDto dto, Long adminId, String adminEmail) {
        if (schemeRepository.findBySchemeCode(dto.getSchemeCode()).isPresent()) {
            throw new BadRequestException("A scheme with scheme code '" + dto.getSchemeCode() + "' already exists.");
        }

        Scheme scheme = new Scheme();
        populateSchemeFromDto(scheme, dto);

        Scheme saved = schemeRepository.save(scheme);

        auditLogService.logAction(adminId, adminEmail, "CREATE_SCHEME", "Scheme", saved.getId(), "Created scheme: " + saved.getSchemeName());

        return schemeService.mapToDto(saved, null);
    }

    @Transactional
    public SchemeDto updateScheme(Long schemeId, SchemeDto dto, Long adminId, String adminEmail) {
        Scheme scheme = schemeRepository.findById(schemeId)
                .orElseThrow(() -> new ResourceNotFoundException("Scheme not found with ID: " + schemeId));

        populateSchemeFromDto(scheme, dto);

        Scheme updated = schemeRepository.save(scheme);

        auditLogService.logAction(adminId, adminEmail, "UPDATE_SCHEME", "Scheme", updated.getId(), "Updated scheme: " + updated.getSchemeName());

        return schemeService.mapToDto(updated, null);
    }

    @Transactional
    public SchemeDto updateVerificationStatus(Long schemeId, String verificationStatus, Long adminId, String adminEmail) {
        Scheme scheme = schemeRepository.findById(schemeId)
                .orElseThrow(() -> new ResourceNotFoundException("Scheme not found with ID: " + schemeId));

        scheme.setVerificationStatus(verificationStatus.toUpperCase());
        scheme.setLastVerifiedAt(LocalDate.now());
        Scheme updated = schemeRepository.save(scheme);

        auditLogService.logAction(adminId, adminEmail, "UPDATE_VERIFICATION", "Scheme", schemeId, "Set verification to: " + verificationStatus);

        return schemeService.mapToDto(updated, null);
    }

    @Transactional
    public void deleteScheme(Long schemeId, Long adminId, String adminEmail) {
        Scheme scheme = schemeRepository.findById(schemeId)
                .orElseThrow(() -> new ResourceNotFoundException("Scheme not found with ID: " + schemeId));

        String schemeName = scheme.getSchemeName();
        schemeRepository.delete(scheme);

        auditLogService.logAction(adminId, adminEmail, "DELETE_SCHEME", "Scheme", schemeId, "Deleted scheme: " + schemeName);
    }

    private void populateSchemeFromDto(Scheme scheme, SchemeDto dto) {
        scheme.setSchemeName(dto.getSchemeName());
        scheme.setSchemeCode(dto.getSchemeCode());
        scheme.setCategory(dto.getCategory());
        scheme.setGovernmentLevel(dto.getGovernmentLevel());
        scheme.setDepartment(dto.getDepartment());
        scheme.setShortDescription(dto.getShortDescription());
        scheme.setDetailedDescription(dto.getDetailedDescription());
        scheme.setWhyExists(dto.getWhyExists());
        scheme.setWhoCanBenefit(dto.getWhoCanBenefit());
        scheme.setBenefits(dto.getBenefits());
        scheme.setApplicationProcess(dto.getApplicationProcess());
        scheme.setOfficialUrl(dto.getOfficialUrl());
        scheme.setVerificationStatus(dto.getVerificationStatus() != null ? dto.getVerificationStatus() : "VERIFIED");
        scheme.setPublicationStatus(dto.getPublicationStatus() != null ? dto.getPublicationStatus() : "PUBLISHED");
        scheme.setApplicationStatus(dto.getApplicationStatus() != null ? dto.getApplicationStatus() : "OPEN");
        scheme.setLastVerifiedAt(dto.getLastVerifiedAt() != null ? dto.getLastVerifiedAt() : LocalDate.now());
        scheme.setInformationSource(dto.getInformationSource() != null ? dto.getInformationSource() : "Official Government Notification");

        if (dto.getEligibilityRule() != null) {
            SchemeRuleDto rd = dto.getEligibilityRule();
            SchemeEligibilityRule rule = scheme.getEligibilityRule();
            if (rule == null) {
                rule = new SchemeEligibilityRule();
            }
            rule.setMinimumAge(rd.getMinimumAge());
            rule.setMaximumAge(rd.getMaximumAge());
            rule.setMinimumIncome(rd.getMinimumIncome());
            rule.setMaximumIncome(rd.getMaximumIncome());
            rule.setEligibleStates(rd.getEligibleStates() != null ? rd.getEligibleStates() : "ALL");
            rule.setEligibleOccupations(rd.getEligibleOccupations() != null ? rd.getEligibleOccupations() : "ALL");
            rule.setEligibleSocialCategories(rd.getEligibleSocialCategories() != null ? rd.getEligibleSocialCategories() : "ALL");
            rule.setEligibleEducationLevels(rd.getEligibleEducationLevels() != null ? rd.getEligibleEducationLevels() : "ALL");
            rule.setGenderRequirement(rd.getGenderRequirement() != null ? rd.getGenderRequirement() : "ALL");
            rule.setDisabilityStatus(rd.getDisabilityStatus() != null ? rd.getDisabilityStatus() : "ALL");
            rule.setLandholdingMaxAcres(rd.getLandholdingMaxAcres());
            rule.setAdditionalConditions(rd.getAdditionalConditions());
            scheme.setEligibilityRule(rule);
        }

        if (dto.getDocuments() != null) {
            scheme.getDocuments().clear();
            for (DocumentDto dd : dto.getDocuments()) {
                SchemeDocument doc = new SchemeDocument(dd.getDocumentName(), dd.getDescription(), dd.getMandatoryStatus());
                scheme.addDocument(doc);
            }
        }
    }
}
