package com.govguide.service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.govguide.dto.DocumentDto;
import com.govguide.dto.SchemeDto;
import com.govguide.dto.SchemeRuleDto;
import com.govguide.entity.Scheme;
import com.govguide.entity.SchemeEligibilityRule;
import com.govguide.entity.User;
import com.govguide.exception.ResourceNotFoundException;
import com.govguide.repository.SavedSchemeRepository;
import com.govguide.repository.SchemeRepository;
import com.govguide.repository.UserRepository;

@Service
public class SchemeService {

    private final SchemeRepository schemeRepository;
    private final SavedSchemeRepository savedSchemeRepository;
    private final UserRepository userRepository;

    public SchemeService(SchemeRepository schemeRepository,
                         SavedSchemeRepository savedSchemeRepository,
                         UserRepository userRepository) {
        this.schemeRepository = schemeRepository;
        this.savedSchemeRepository = savedSchemeRepository;
        this.userRepository = userRepository;
    }
@Transactional(readOnly = true)
    public Page<SchemeDto> getPublishedSchemes(
            String keyword,
            String category,
            String governmentLevel,
            String state,
            String occupation,
            String socialCategory,
            int page,
            int size,
            String sortBy,
            String sortDir,
            String userEmail) {

        Sort sort = "desc".equalsIgnoreCase(sortDir) ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        List<Scheme> allPublished = schemeRepository.findByPublicationStatus("PUBLISHED");

        // Filter in-memory for flexible combination of rules and text
        List<Scheme> filtered = allPublished.stream().filter(scheme -> {
            if (keyword != null && !keyword.isBlank()) {
                String kw = keyword.toLowerCase();
                boolean matchesKeyword = scheme.getSchemeName().toLowerCase().contains(kw) ||
                        scheme.getShortDescription().toLowerCase().contains(kw) ||
                        scheme.getBenefits().toLowerCase().contains(kw) ||
                        scheme.getDepartment().toLowerCase().contains(kw) ||
                        scheme.getCategory().toLowerCase().contains(kw);
                if (!matchesKeyword) return false;
            }

            if (category != null && !category.isBlank() && !"ALL".equalsIgnoreCase(category)) {
                if (!scheme.getCategory().equalsIgnoreCase(category.trim())) return false;
            }

            if (governmentLevel != null && !governmentLevel.isBlank() && !"ALL".equalsIgnoreCase(governmentLevel)) {
                if (!scheme.getGovernmentLevel().equalsIgnoreCase(governmentLevel.trim())) return false;
            }

            SchemeEligibilityRule rule = scheme.getEligibilityRule();
            if (rule != null) {
                if (state != null && !state.isBlank() && !"ALL".equalsIgnoreCase(state)) {
                    if (rule.getEligibleStates() != null && !"ALL".equalsIgnoreCase(rule.getEligibleStates())) {
                        String[] states = rule.getEligibleStates().split(",");
                        boolean stateMatch = false;
                        for (String s : states) {
                            if (s.trim().equalsIgnoreCase(state.trim())) {
                                stateMatch = true;
                                break;
                            }
                        }
                        if (!stateMatch) return false;
                    }
                }

                if (occupation != null && !occupation.isBlank() && !"ALL".equalsIgnoreCase(occupation)) {
                    if (rule.getEligibleOccupations() != null && !"ALL".equalsIgnoreCase(rule.getEligibleOccupations())) {
                        String[] occs = rule.getEligibleOccupations().split(",");
                        boolean occMatch = false;
                        for (String o : occs) {
                            if (o.trim().equalsIgnoreCase(occupation.trim()) ||
                                    (occupation.toLowerCase().contains("farmer") && o.toLowerCase().contains("farmer")) ||
                                    (occupation.toLowerCase().contains("student") && o.toLowerCase().contains("student"))) {
                                occMatch = true;
                                break;
                            }
                        }
                        if (!occMatch) return false;
                    }
                }

                if (socialCategory != null && !socialCategory.isBlank() && !"ALL".equalsIgnoreCase(socialCategory)) {
                    if (rule.getEligibleSocialCategories() != null && !"ALL".equalsIgnoreCase(rule.getEligibleSocialCategories())) {
                        String[] cats = rule.getEligibleSocialCategories().split(",");
                        boolean catMatch = false;
                        for (String c : cats) {
                            if (c.trim().equalsIgnoreCase(socialCategory.trim())) {
                                catMatch = true;
                                break;
                            }
                        }
                        if (!catMatch) return false;
                    }
                }
            }

            return true;
        }).collect(Collectors.toList());

        Long currentUserId = getUserIdFromEmail(userEmail);

        int start = Math.min((int) pageable.getOffset(), filtered.size());
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<Scheme> pageContent = filtered.subList(start, end);

        List<SchemeDto> dtos = pageContent.stream()
                .map(scheme -> mapToDto(scheme, currentUserId))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, filtered.size());
    }
@Transactional(readOnly = true)
    public SchemeDto getSchemeById(Long id, String userEmail) {
        Scheme scheme = schemeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scheme not found with ID: " + id));
        Long currentUserId = getUserIdFromEmail(userEmail);
        return mapToDto(scheme, currentUserId);
    }
@Transactional(readOnly = true)
    public List<SchemeDto> getRecentlyAddedSchemes(String userEmail) {
        Long currentUserId = getUserIdFromEmail(userEmail);
        return schemeRepository.findTop5ByPublicationStatusOrderByCreatedAtDesc("PUBLISHED").stream()
                .map(scheme -> mapToDto(scheme, currentUserId))
                .collect(Collectors.toList());
    }
@Transactional(readOnly = true)
    public List<SchemeDto> getRecommendedSchemes(String userEmail) {
        Long currentUserId = getUserIdFromEmail(userEmail);
        return schemeRepository.findByPublicationStatus("PUBLISHED").stream()
                .limit(4)
                .map(scheme -> mapToDto(scheme, currentUserId))
                .collect(Collectors.toList());
    }

    private Long getUserIdFromEmail(String email) {
        if (email == null || email.isBlank()) return null;
        return userRepository.findByEmail(email).map(User::getId).orElse(null);
    }

    public SchemeDto mapToDto(Scheme scheme, Long currentUserId) {
        SchemeDto dto = new SchemeDto();
        dto.setId(scheme.getId());
        dto.setSchemeName(scheme.getSchemeName());
        dto.setSchemeCode(scheme.getSchemeCode());
        dto.setCategory(scheme.getCategory());
        dto.setGovernmentLevel(scheme.getGovernmentLevel());
        dto.setDepartment(scheme.getDepartment());
        dto.setShortDescription(scheme.getShortDescription());
        dto.setDetailedDescription(scheme.getDetailedDescription());
        dto.setWhyExists(scheme.getWhyExists());
        dto.setWhoCanBenefit(scheme.getWhoCanBenefit());
        dto.setBenefits(scheme.getBenefits());
        dto.setApplicationProcess(scheme.getApplicationProcess());
        dto.setOfficialUrl(scheme.getOfficialUrl());
        dto.setVerificationStatus(scheme.getVerificationStatus());
        dto.setPublicationStatus(scheme.getPublicationStatus());
        dto.setApplicationStatus(scheme.getApplicationStatus());
        dto.setLastVerifiedAt(scheme.getLastVerifiedAt());
        dto.setInformationSource(scheme.getInformationSource());

        if (scheme.getEligibilityRule() != null) {
            SchemeEligibilityRule r = scheme.getEligibilityRule();
            SchemeRuleDto ruleDto = new SchemeRuleDto();
            ruleDto.setId(r.getId());
            ruleDto.setMinimumAge(r.getMinimumAge());
            ruleDto.setMaximumAge(r.getMaximumAge());
            ruleDto.setMinimumIncome(r.getMinimumIncome());
            ruleDto.setMaximumIncome(r.getMaximumIncome());
            ruleDto.setEligibleStates(r.getEligibleStates());
            ruleDto.setEligibleOccupations(r.getEligibleOccupations());
            ruleDto.setEligibleSocialCategories(r.getEligibleSocialCategories());
            ruleDto.setEligibleEducationLevels(r.getEligibleEducationLevels());
            ruleDto.setGenderRequirement(r.getGenderRequirement());
            ruleDto.setDisabilityStatus(r.getDisabilityStatus());
            ruleDto.setLandholdingMaxAcres(r.getLandholdingMaxAcres());
            ruleDto.setAdditionalConditions(r.getAdditionalConditions());
            dto.setEligibilityRule(ruleDto);
        }

        if (scheme.getDocuments() != null) {
            dto.setDocuments(scheme.getDocuments().stream().map(d ->
                    new DocumentDto(d.getId(), d.getDocumentName(), d.getDescription(), d.getMandatoryStatus())
            ).collect(Collectors.toList()));
        }

        if (currentUserId != null) {
            dto.setSaved(savedSchemeRepository.existsByUserIdAndSchemeId(currentUserId, scheme.getId()));
        }

        return dto;
    }
}
