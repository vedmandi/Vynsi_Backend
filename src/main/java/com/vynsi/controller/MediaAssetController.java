package com.vynsi.controller;

import com.vynsi.dto.MediaAssetResponse;
import com.vynsi.model.MediaAssetType;
import com.vynsi.service.MediaAssetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/talents/{talentId}/assets")
public class MediaAssetController {

    private final MediaAssetService mediaAssetService;

    public MediaAssetController(
            MediaAssetService mediaAssetService
    ) {
        this.mediaAssetService = mediaAssetService;
    }

    @PostMapping
    public ResponseEntity<MediaAssetResponse> uploadAsset(
            @PathVariable UUID talentId,

            @RequestParam
            MediaAssetType assetType,

            @RequestPart("file")
            MultipartFile file
    ) {

        MediaAssetResponse response =
                mediaAssetService.uploadAsset(
                        talentId,
                        assetType,
                        file
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<MediaAssetResponse>>
    getAssets(
            @PathVariable UUID talentId
    ) {

        return ResponseEntity.ok(
                mediaAssetService.getAssets(talentId)
        );
    }
}