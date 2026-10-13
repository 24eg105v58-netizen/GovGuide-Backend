package com.govguide;

import com.govguide.dto.EligibilityCheckRequest;
import com.govguide.dto.EligibilityCheckResponse;
import com.govguide.dto.EligibilityResultItemDto;
import com.govguide.entity.Scheme;
import com.govguide.entity.SchemeEligibilityRule;
import com.govguide.repository.EligibilityCheckRepository;
import com.govguide.repository.SchemeRepository;
import com.govguide.repository.UserRepository;
import com.govguide.service.EligibilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EligibilityServiceTest {

    @Mock
    private SchemeRepository schemeRepository;

    @Mock
    private EligibilityCheckRepository checkRepository;

    @Mock
    private UserRepository userRepository;

    private EligibilityService eligibilityService;

    private Scheme pmKisanScheme;
    private Scheme rythuBharosaScheme;

    @BeforeEach
    void setUp() {
        eligibilityService = new EligibilityService(schemeRepository, checkRepository, userRepository);

        // Setup PM-KISAN
        pmKisanScheme = new Scheme();
        pmKisanScheme.setId(1L);
        pmKisanScheme.setSchemeName("Pradhan Mantri Kisan Samman Nidhi");
        pmKisanScheme.setSchemeCode("PM-KISAN");
        pmKisanScheme.setCategory("Agriculture");
        pmKisanScheme.setGovernmentLevel("CENTRAL");
        pmKisanScheme.setDepartment("Ministry of Agriculture");
        pmKisanScheme.setShortDescription("₹6,000 per year");
        pmKisanScheme.setBenefits("₹6,000 per year");
        pmKisanScheme.setOfficialUrl("https://pmkisan.gov.in");
        pmKisanScheme.setPublicationStatus("PUBLISHED");
        pmKisanScheme.setLastVerifiedAt(LocalDate.now());

        SchemeEligibilityRule pmKisanRule = new SchemeEligibilityRule();
        pmKisanRule.setMinimumAge(18);
        pmKisanRule.setMaximumAge(100);
        pmKisanRule.setMinimumIncome(BigDecimal.ZERO);
        pmKisanRule.setMaximumIncome(new BigDecimal("1000000"));
        pmKisanRule.setEligibleStates("ALL");
        pmKisanRule.setEligibleOccupations("Farmer,Agricultural labourer");
        pmKisanRule.setEligibleSocialCategories("ALL");
        pmKisanRule.setEligibleEducationLevels("ALL");
        pmKisanRule.setGenderRequirement("ALL");
        pmKisanRule.setDisabilityStatus("ALL");
        pmKisanScheme.setEligibilityRule(pmKisanRule);

        // Setup Rythu Bharosa (Andhra Pradesh state scheme)
        rythuBharosaScheme = new Scheme();
        rythuBharosaScheme.setId(2L);
        rythuBharosaScheme.setSchemeName("YSR Rythu Bharosa");
        rythuBharosaScheme.setSchemeCode("RYTHU-BHAROSA");
        rythuBharosaScheme.setCategory("Agriculture");
        rythuBharosaScheme.setGovernmentLevel("STATE");
        rythuBharosaScheme.setDepartment("AP Agriculture");
        rythuBharosaScheme.setShortDescription("₹13,500 per year");
        rythuBharosaScheme.setBenefits("₹13,500 per year");
        rythuBharosaScheme.setOfficialUrl("https://ysrrythubharosa.ap.gov.in");
        rythuBharosaScheme.setPublicationStatus("PUBLISHED");
        rythuBharosaScheme.setLastVerifiedAt(LocalDate.now());

        SchemeEligibilityRule rythuRule = new SchemeEligibilityRule();
        rythuRule.setMinimumAge(18);
        rythuRule.setMaximumAge(99);
        rythuRule.setMinimumIncome(BigDecimal.ZERO);
        rythuRule.setMaximumIncome(new BigDecimal("500000"));
        rythuRule.setEligibleStates("Andhra Pradesh");
        rythuRule.setEligibleOccupations("Farmer,Agricultural labourer");
        rythuRule.setEligibleSocialCategories("ALL");
        rythuRule.setEligibleEducationLevels("ALL");
        rythuBharosaScheme.setEligibilityRule(rythuRule);
    }

    @Test
    void testFarmerEligibleForPMKisan() {
        when(schemeRepository.findByPublicationStatus("PUBLISHED"))
                .thenReturn(List.of(pmKisanScheme));

        EligibilityCheckRequest request = new EligibilityCheckRequest();
        request.setAge(35);
        request.setState("Telangana");
        request.setOccupation("Farmer");
        request.setAnnualIncome(new BigDecimal("120000"));
        request.setSocialCategory("OBC");
        request.setEducationLevel("Secondary education");

        EligibilityCheckResponse response = eligibilityService.evaluateEligibility(request, null);

        assertEquals(1, response.getLikelyEligible().size());
        assertEquals("LIKELY_ELIGIBLE", response.getLikelyEligible().get(0).getStatus());
        assertEquals("PM-KISAN", response.getLikelyEligible().get(0).getSchemeCode());
        assertEquals(0, response.getNotEligible().size());
    }

    @Test
    void testStateSpecificSchemeMatchesResidentAndRejectsNonResident() {
        // Resident of Andhra Pradesh should match Rythu Bharosa
        EligibilityCheckRequest apResident = new EligibilityCheckRequest();
        apResident.setAge(40);
        apResident.setState("Andhra Pradesh");
        apResident.setOccupation("Farmer");
        apResident.setAnnualIncome(new BigDecimal("100000"));
        apResident.setSocialCategory("General");
        apResident.setEducationLevel("Primary education");

        EligibilityResultItemDto resultAP = eligibilityService.evaluateScheme(rythuBharosaScheme, apResident);
        assertEquals("LIKELY_ELIGIBLE", resultAP.getStatus());

        // Resident of Maharashtra should be NOT_ELIGIBLE for AP-specific scheme
        EligibilityCheckRequest mhResident = new EligibilityCheckRequest();
        mhResident.setAge(40);
        mhResident.setState("Maharashtra");
        mhResident.setOccupation("Farmer");
        mhResident.setAnnualIncome(new BigDecimal("100000"));
        mhResident.setSocialCategory("General");
        mhResident.setEducationLevel("Primary education");

        EligibilityResultItemDto resultMH = eligibilityService.evaluateScheme(rythuBharosaScheme, mhResident);
        assertEquals("NOT_ELIGIBLE", resultMH.getStatus());
        assertTrue(resultMH.getUnmetConditions().stream().anyMatch(s -> s.contains("state-specific")));
    }

    @Test
    void testUnderageApplicantIsRejected() {
        EligibilityCheckRequest underage = new EligibilityCheckRequest();
        underage.setAge(16); // Required >= 18
        underage.setState("Punjab");
        underage.setOccupation("Farmer");
        underage.setAnnualIncome(new BigDecimal("80000"));
        underage.setSocialCategory("General");
        underage.setEducationLevel("Secondary education");

        EligibilityResultItemDto result = eligibilityService.evaluateScheme(pmKisanScheme, underage);
        assertEquals("NOT_ELIGIBLE", result.getStatus());
        assertTrue(result.getUnmetConditions().stream().anyMatch(s -> s.contains("Minimum age requirement is 18")));
    }

    @Test
    void testExcessiveIncomeIsRejected() {
        EligibilityCheckRequest highIncome = new EligibilityCheckRequest();
        highIncome.setAge(45);
        highIncome.setState("Gujarat");
        highIncome.setOccupation("Farmer");
        highIncome.setAnnualIncome(new BigDecimal("1500000")); // Limit is 1,000,000
        highIncome.setSocialCategory("General");
        highIncome.setEducationLevel("Undergraduate");

        EligibilityResultItemDto result = eligibilityService.evaluateScheme(pmKisanScheme, highIncome);
        assertEquals("NOT_ELIGIBLE", result.getStatus());
        assertTrue(result.getUnmetConditions().stream().anyMatch(s -> s.contains("Annual family income ceiling")));
    }
}
