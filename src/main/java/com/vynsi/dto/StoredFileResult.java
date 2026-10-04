package com.vynsi.dto;

public record StoredFileResult(
        String storageKey,
        String originalFilename,
        String mimeType,
        long fileSize,
        String checksum
) {
}