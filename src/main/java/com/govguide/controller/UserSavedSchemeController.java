package com.govguide.controller;

import com.govguide.dto.SchemeDto;
import com.govguide.entity.SavedScheme;
import com.govguide.entity.Scheme;
import com.govguide.entity.User;
import com.govguide.exception.ResourceNotFoundException;
import com.govguide.repository.SavedSchemeRepository;
import com.govguide.repository.SchemeRepository;
import com.govguide.repository.UserRepository;
import com.govguide.service.SchemeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/saved-schemes")
public class UserSavedSchemeController {

    private final SavedSchemeRepository savedSchemeRepository;
    private final UserRepository userRepository;
    private final SchemeRepository schemeRepository;
    private final SchemeService schemeService;

    public UserSavedSchemeController(SavedSchemeRepository savedSchemeRepository,
                                    UserRepository userRepository,
                                    SchemeRepository schemeRepository,
                                    SchemeService schemeService) {
        this.savedSchemeRepository = savedSchemeRepository;
        this.userRepository = userRepository;
        this.schemeRepository = schemeRepository;
        this.schemeService = schemeService;
    }

    @GetMapping
    public ResponseEntity<List<SchemeDto>> getSavedSchemes(Authentication authentication) {
        User user = getUser(authentication);
        List<SavedScheme> savedList = savedSchemeRepository.findByUserIdOrderBySavedAtDesc(user.getId());
        List<SchemeDto> dtos = savedList.stream()
                .map(ss -> schemeService.mapToDto(ss.getScheme(), user.getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{schemeId}")
    @Transactional
    public ResponseEntity<Map<String, String>> saveScheme(@PathVariable Long schemeId, Authentication authentication) {
        User user = getUser(authentication);
        Scheme scheme = schemeRepository.findById(schemeId)
                .orElseThrow(() -> new ResourceNotFoundException("Scheme not found with ID: " + schemeId));

        if (!savedSchemeRepository.existsByUserIdAndSchemeId(user.getId(), scheme.getId())) {
            SavedScheme ss = new SavedScheme(user, scheme);
            savedSchemeRepository.save(ss);
        }

        return ResponseEntity.ok(Map.of("message", "Scheme saved successfully"));
    }

    @DeleteMapping("/{schemeId}")
    @Transactional
    public ResponseEntity<Map<String, String>> removeSavedScheme(@PathVariable Long schemeId, Authentication authentication) {
        User user = getUser(authentication);
        savedSchemeRepository.deleteByUserIdAndSchemeId(user.getId(), schemeId);
        return ResponseEntity.ok(Map.of("message", "Scheme removed from saved list"));
    }

    private User getUser(Authentication authentication) {
        if (authentication == null) {
            throw new ResourceNotFoundException("User is not authenticated");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
