package com.vynsi.dto;

import com.vynsi.model.MediaAssetStatus;
import com.vynsi.model.MediaAssetType;

import java.time.Instant;
import java.util.UUID;

public record MediaAssetResponse(
        UUID id,
        UUID talentId,
        MediaAssetType assetType,
        String storageKey,
        String originalFilename,
        String mimeType,
        Long fileSize,
        String checksum,
        MediaAssetStatus status,
        Instant createdAt
) {
}