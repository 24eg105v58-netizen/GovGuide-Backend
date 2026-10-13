package com.govguide.service;

import com.govguide.dto.EligibilityCheckRequest;
import com.govguide.dto.EligibilityCheckResponse;
import com.govguide.dto.EligibilityResultItemDto;
import com.govguide.entity.*;
import com.govguide.repository.EligibilityCheckRepository;
import com.govguide.repository.SchemeRepository;
import com.govguide.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EligibilityService {

    private final SchemeRepository schemeRepository;
    private final EligibilityCheckRepository eligibilityCheckRepository;
    private final UserRepository userRepository;

    public EligibilityService(SchemeRepository schemeRepository,
                              EligibilityCheckRepository eligibilityCheckRepository,
                              UserRepository userRepository) {
        this.schemeRepository = schemeRepository;
        this.eligibilityCheckRepository = eligibilityCheckRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public EligibilityCheckResponse evaluateEligibility(EligibilityCheckRequest request, String userEmail) {
        List<Scheme> schemes = schemeRepository.findByPublicationStatus("PUBLISHED");
        EligibilityCheckResponse response = new EligibilityCheckResponse();

        User currentUser = null;
        if (userEmail != null && !userEmail.isBlank()) {
            currentUser = userRepository.findByEmail(userEmail).orElse(null);
        }

        for (Scheme scheme : schemes) {
            EligibilityResultItemDto result = evaluateScheme(scheme, request);

            if ("LIKELY_ELIGIBLE".equals(result.getStatus())) {
                response.getLikelyEligible().add(result);
            } else if ("POSSIBLY_ELIGIBLE".equals(result.getStatus())) {
                response.getPossiblyEligible().add(result);
            } else {
                response.getNotEligible().add(result);
            }

            // Save check record in database
            if (currentUser != null) {
                try {
                    String conditionsLog = "Matched: " + String.join(", ", result.getMatchingConditions()) +
                            " | Unmet: " + String.join(", ", result.getUnmetConditions());
                    EligibilityCheck check = new EligibilityCheck(currentUser, scheme, result.getStatus(), conditionsLog);
                    eligibilityCheckRepository.save(check);
                } catch (Exception ignored) {
                }
            }
        }

        response.setTotalEvaluated(schemes.size());
        return response;
    }

    public EligibilityResultItemDto evaluateScheme(Scheme scheme, EligibilityCheckRequest request) {
        EligibilityResultItemDto dto = new EligibilityResultItemDto();
        dto.setSchemeId(scheme.getId());
        dto.setSchemeName(scheme.getSchemeName());
        dto.setSchemeCode(scheme.getSchemeCode());
        dto.setCategory(scheme.getCategory());
        dto.setGovernmentLevel(scheme.getGovernmentLevel());
        dto.setDepartment(scheme.getDepartment());
        dto.setBenefits(scheme.getBenefits());
        dto.setOfficialUrl(scheme.getOfficialUrl());
        dto.setLastVerifiedAt(scheme.getLastVerifiedAt());
        dto.setInformationSource(scheme.getInformationSource());

        if (scheme.getDocuments() != null) {
            dto.setRequiredDocuments(scheme.getDocuments().stream()
                    .map(d -> d.getDocumentName() + (d.getDescription() != null ? " (" + d.getDescription() + ")" : ""))
                    .collect(Collectors.toList()));
        }

        SchemeEligibilityRule rule = scheme.getEligibilityRule();
        if (rule == null) {
            dto.setStatus("POSSIBLY_ELIGIBLE");
            dto.getMissingDetails().add("Detailed eligibility rules for this scheme are currently under administrative review.");
            return dto;
        }

        List<String> matched = new ArrayList<>();
        List<String> unmet = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        // 1. Age condition
        if (request.getAge() != null) {
            int age = request.getAge();
            boolean ageValid = true;
            if (rule.getMinimumAge() != null && age < rule.getMinimumAge()) {
                ageValid = false;
                unmet.add("Minimum age requirement is " + rule.getMinimumAge() + " years (your age: " + age + ").");
            }
            if (rule.getMaximumAge() != null && age > rule.getMaximumAge()) {
                ageValid = false;
                unmet.add("Maximum age limit is " + rule.getMaximumAge() + " years (your age: " + age + ").");
            }
            if (ageValid) {
                matched.add("Age (" + age + " years) falls within the eligible range (" +
                        (rule.getMinimumAge() != null ? rule.getMinimumAge() : 0) + " to " +
                        (rule.getMaximumAge() != null ? rule.getMaximumAge() : 120) + " years).");
            }
        } else {
            missing.add("Age verification required.");
        }

        // 2. State condition
        if (request.getState() != null && !request.getState().isBlank()) {
            String eligibleStates = rule.getEligibleStates();
            if (eligibleStates == null || "ALL".equalsIgnoreCase(eligibleStates.trim())) {
                matched.add("Available across all Indian States and Union Territories (including " + request.getState() + ").");
            } else {
                List<String> statesList = Arrays.stream(eligibleStates.split(","))
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .toList();
                if (statesList.contains(request.getState().trim().toLowerCase())) {
                    matched.add("Scheme is active and applicable in " + request.getState() + ".");
                } else {
                    unmet.add("This is a state-specific scheme applicable in: " + eligibleStates + " (selected state: " + request.getState() + ").");
                }
            }
        } else {
            missing.add("State of residence not provided.");
        }

        // 3. Occupation condition
        if (request.getOccupation() != null && !request.getOccupation().isBlank()) {
            String eligibleOccupations = rule.getEligibleOccupations();
            if (eligibleOccupations == null || "ALL".equalsIgnoreCase(eligibleOccupations.trim())) {
                matched.add("Open to individuals of any occupation.");
            } else {
                List<String> occList = Arrays.stream(eligibleOccupations.split(","))
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .toList();
                String userOcc = request.getOccupation().trim().toLowerCase();
                if (occList.contains(userOcc) || (userOcc.contains("farmer") && occList.contains("farmer")) ||
                        (userOcc.contains("student") && occList.contains("student"))) {
                    matched.add("Your occupation (" + request.getOccupation() + ") qualifies under the target beneficiary group.");
                } else {
                    unmet.add("Targeted toward: " + eligibleOccupations + " (your occupation: " + request.getOccupation() + ").");
                }
            }
        } else {
            missing.add("Occupation details not specified.");
        }

        // 4. Annual Income condition
        if (request.getAnnualIncome() != null) {
            BigDecimal income = request.getAnnualIncome();
            boolean incomeValid = true;
            if (rule.getMinimumIncome() != null && income.compareTo(rule.getMinimumIncome()) < 0) {
                incomeValid = false;
                unmet.add("Minimum income threshold is ₹" + rule.getMinimumIncome() + ".");
            }
            if (rule.getMaximumIncome() != null && income.compareTo(rule.getMaximumIncome()) > 0) {
                incomeValid = false;
                unmet.add("Annual family income ceiling is ₹" + rule.getMaximumIncome() + " (your declared income: ₹" + income + ").");
            }
            if (incomeValid) {
                matched.add("Annual income of ₹" + income + " satisfies the financial eligibility limit" +
                        (rule.getMaximumIncome() != null ? " (up to ₹" + rule.getMaximumIncome() + ")" : "") + ".");
            }
        } else {
            missing.add("Annual family income not specified.");
        }

        // 5. Social Category condition
        if (request.getSocialCategory() != null && !request.getSocialCategory().isBlank()) {
            String eligibleCats = rule.getEligibleSocialCategories();
            if (eligibleCats == null || "ALL".equalsIgnoreCase(eligibleCats.trim())) {
                matched.add("Open to all social categories without caste-based restrictions.");
            } else {
                if ("Prefer not to disclose".equalsIgnoreCase(request.getSocialCategory().trim())) {
                    missing.add("This scheme requires social category verification (" + eligibleCats + "). You chose not to disclose.");
                } else {
                    List<String> catList = Arrays.stream(eligibleCats.split(","))
                            .map(String::trim)
                            .map(String::toLowerCase)
                            .toList();
                    if (catList.contains(request.getSocialCategory().trim().toLowerCase())) {
                        matched.add("Category (" + request.getSocialCategory() + ") is eligible under this scheme.");
                    } else {
                        unmet.add("Reserved for social categories: " + eligibleCats + " (selected: " + request.getSocialCategory() + ").");
                    }
                }
            }
        }

        // 6. Education Level condition
        if (request.getEducationLevel() != null && !request.getEducationLevel().isBlank()) {
            String eligibleEdu = rule.getEligibleEducationLevels();
            if (eligibleEdu == null || "ALL".equalsIgnoreCase(eligibleEdu.trim())) {
                matched.add("No specific educational qualification barrier.");
            } else {
                List<String> eduList = Arrays.stream(eligibleEdu.split(","))
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .toList();
                if (eduList.contains(request.getEducationLevel().trim().toLowerCase())) {
                    matched.add("Education qualification (" + request.getEducationLevel() + ") matches criteria.");
                } else {
                    unmet.add("Requires education level: " + eligibleEdu + " (your level: " + request.getEducationLevel() + ").");
                }
            }
        }

        // 7. Gender condition (where scheme is gender-specific e.g. Sukanya Samriddhi)
        if (rule.getGenderRequirement() != null && !"ALL".equalsIgnoreCase(rule.getGenderRequirement())) {
            if (request.getGender() != null && !request.getGender().isBlank() && !"ALL".equalsIgnoreCase(request.getGender())) {
                if (rule.getGenderRequirement().equalsIgnoreCase(request.getGender().trim())) {
                    matched.add("Meets specific gender criteria (" + rule.getGenderRequirement() + ").");
                } else {
                    unmet.add("Exclusively intended for: " + rule.getGenderRequirement() + ".");
                }
            } else {
                missing.add("Specific gender qualification applies (" + rule.getGenderRequirement() + ").");
            }
        }

        // 8. Disability requirement
        if ("REQUIRED".equalsIgnoreCase(rule.getDisabilityStatus())) {
            if ("REQUIRED".equalsIgnoreCase(request.getDisabilityStatus())) {
                matched.add("Meets disability support assistance criteria.");
            } else {
                unmet.add("Designed specifically for persons with benchmark disabilities (PwD).");
            }
        }

        // 9. Additional conditions note
        if (rule.getAdditionalConditions() != null && !rule.getAdditionalConditions().isBlank()) {
            missing.add("Additional condition to verify: " + rule.getAdditionalConditions());
        }

        dto.setMatchingConditions(matched);
        dto.setUnmetConditions(unmet);
        dto.setMissingDetails(missing);

        // Final Categorization:
        // A. If ANY mandatory condition is unmet -> NOT_ELIGIBLE
        // B. If no condition is unmet, but there are missing details -> POSSIBLY_ELIGIBLE
        // C. If no unmet conditions and no critical missing details -> LIKELY_ELIGIBLE
        if (!unmet.isEmpty()) {
            dto.setStatus("NOT_ELIGIBLE");
        } else if (!missing.isEmpty()) {
            dto.setStatus("POSSIBLY_ELIGIBLE");
        } else {
            dto.setStatus("LIKELY_ELIGIBLE");
        }

        return dto;
    }
}
