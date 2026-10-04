package com.vynsi.controller;

import com.vynsi.dto.TalentResponse;
import com.vynsi.service.TalentOnboardingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/talents/{talentId}/onboarding")
public class TalentOnboardingController {

    private final TalentOnboardingService talentOnboardingService;

    public TalentOnboardingController(
            TalentOnboardingService talentOnboardingService
    ) {
        this.talentOnboardingService =
                talentOnboardingService;
    }

    @PostMapping("/identity/start")
    public ResponseEntity<TalentResponse> startIdentityVerification(
            @PathVariable UUID talentId
    ) {

        return ResponseEntity.ok(
                talentOnboardingService
                        .startIdentityVerification(talentId)
        );
    }

    @PostMapping("/identity/verify")
    public ResponseEntity<TalentResponse> verifyIdentity(
            @PathVariable UUID talentId
    ) {

        return ResponseEntity.ok(
                talentOnboardingService
                        .verifyIdentity(talentId)
        );
    }

    @PostMapping("/assets/complete")
    public ResponseEntity<TalentResponse> completeAssets(
            @PathVariable UUID talentId
    ) {

        return ResponseEntity.ok(
                talentOnboardingService.completeAssets(talentId)
        );
    }
}