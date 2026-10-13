package com.govguide.controller;

import com.govguide.dto.VoiceQueryRequest;
import com.govguide.dto.VoiceQueryResponse;
import com.govguide.service.VoiceAssistantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/voice")
public class VoiceAssistantController {

    private final VoiceAssistantService voiceAssistantService;

    public VoiceAssistantController(VoiceAssistantService voiceAssistantService) {
        this.voiceAssistantService = voiceAssistantService;
    }

    @PostMapping("/process")
    public ResponseEntity<VoiceQueryResponse> processVoiceQuery(@Valid @RequestBody VoiceQueryRequest request) {
        VoiceQueryResponse response = voiceAssistantService.processVoiceQuery(request);
        return ResponseEntity.ok(response);
    }
}
