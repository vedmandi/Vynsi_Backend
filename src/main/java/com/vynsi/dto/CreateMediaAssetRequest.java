package com.vynsi.dto;

import com.vynsi.model.MediaAssetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMediaAssetRequest(

        @NotNull
        MediaAssetType assetType,

        @NotBlank
        String storageKey,

        String originalFilename,

        String mimeType,

        Long fileSize,

        String checksum

) {
}