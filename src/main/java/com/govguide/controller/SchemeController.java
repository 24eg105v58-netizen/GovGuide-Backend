package com.govguide.controller;

import com.govguide.dto.SchemeDto;
import com.govguide.service.SchemeService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schemes")
public class SchemeController {

    private final SchemeService schemeService;

    public SchemeController(SchemeService schemeService) {
        this.schemeService = schemeService;
    }

    @GetMapping
    public ResponseEntity<Page<SchemeDto>> getSchemes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String governmentLevel,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String occupation,
            @RequestParam(required = false) String socialCategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication) {

        String userEmail = authentication != null ? authentication.getName() : null;
        Page<SchemeDto> schemes = schemeService.getPublishedSchemes(
                keyword, category, governmentLevel, state, occupation, socialCategory, page, size, sortBy, sortDir, userEmail);
        return ResponseEntity.ok(schemes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchemeDto> getSchemeById(@PathVariable Long id, Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        SchemeDto scheme = schemeService.getSchemeById(id, userEmail);
        return ResponseEntity.ok(scheme);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<SchemeDto>> getRecentSchemes(Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(schemeService.getRecentlyAddedSchemes(userEmail));
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<SchemeDto>> getRecommendedSchemes(Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(schemeService.getRecommendedSchemes(userEmail));
    }
}