package com.govguide.controller;

import com.govguide.dto.UserDto;
import com.govguide.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/profile")
public class UserProfileController {

    private final AuthService authService;

    public UserProfileController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<UserDto> getProfile(Authentication authentication) {
        return ResponseEntity.ok(authService.getCurrentUser(authentication.getName()));
    }

    @PatchMapping("/language")
    public ResponseEntity<UserDto> updateLanguage(@RequestBody Map<String, String> body, Authentication authentication) {
        String lang = body.get("preferredLanguage");
        if (lang == null || lang.isBlank()) {
            lang = "en";
        }
        UserDto updated = authService.updateLanguage(authentication.getName(), lang);
        return ResponseEntity.ok(updated);
    }
}
