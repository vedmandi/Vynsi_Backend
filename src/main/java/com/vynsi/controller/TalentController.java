package com.vynsi.controller;

import com.vynsi.dto.CreateTalentRequest;
import com.vynsi.dto.TalentResponse;
import com.vynsi.service.TalentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/talents")
public class TalentController {

    private final TalentService talentService;

    public TalentController(
            TalentService talentService
    ) {
        this.talentService = talentService;
    }

    @GetMapping
    public ResponseEntity<List<TalentResponse>> getAllTalents() {

        return ResponseEntity.ok(
                talentService.getAllTalents()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TalentResponse> getTalentById(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                talentService.getTalentById(id)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<TalentResponse> getTalentByUserId(
            @PathVariable UUID userId
    ) {

        return ResponseEntity.ok(
                talentService.getTalentByUserId(userId)
        );
    }

    @PostMapping
    public ResponseEntity<TalentResponse> createTalent(
            @Valid
            @RequestBody
            CreateTalentRequest request
    ) {

        TalentResponse response =
                talentService.createTalent(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}