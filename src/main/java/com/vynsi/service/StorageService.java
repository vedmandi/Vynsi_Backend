package com.vynsi.service;

import com.vynsi.dto.StoredFileResult;
import com.vynsi.model.MediaAssetType;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface StorageService {

    StoredFileResult store(
            UUID talentId,
            MediaAssetType assetType,
            MultipartFile file
    );
}