package com.govguide.controller;

import com.govguide.dto.EligibilityCheckRequest;
import com.govguide.dto.EligibilityCheckResponse;
import com.govguide.entity.EligibilityCheck;
import com.govguide.entity.User;
import com.govguide.repository.EligibilityCheckRepository;
import com.govguide.repository.UserRepository;
import com.govguide.service.EligibilityService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eligibility")
public class EligibilityController {

    private final EligibilityService eligibilityService;
    private final EligibilityCheckRepository checkRepository;
    private final UserRepository userRepository;

    public EligibilityController(EligibilityService eligibilityService,
                                 EligibilityCheckRepository checkRepository,
                                 UserRepository userRepository) {
        this.eligibilityService = eligibilityService;
        this.checkRepository = checkRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/check")
    public ResponseEntity<EligibilityCheckResponse> checkEligibility(
            @Valid @RequestBody EligibilityCheckRequest request,
            Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        EligibilityCheckResponse response = eligibilityService.evaluateEligibility(request, userEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<Page<EligibilityCheck>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        Page<EligibilityCheck> history = checkRepository.findByUserIdOrderByCheckedAtDesc(user.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(history);
    }
}
