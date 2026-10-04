package com.vynsi.service;

import com.vynsi.dao.MediaAssetRepository;
import com.vynsi.dao.TalentProfileRepository;
import com.vynsi.dto.MediaAssetResponse;
import com.vynsi.dto.StoredFileResult;
import com.vynsi.entity.MediaAsset;
import com.vynsi.entity.TalentProfile;
import com.vynsi.exception.InvalidRequestException;
import com.vynsi.exception.ResourceNotFoundException;
import com.vynsi.model.MediaAssetStatus;
import com.vynsi.model.MediaAssetType;
import com.vynsi.model.TalentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class MediaAssetService {

    private final MediaAssetRepository mediaAssetRepository;
    private final TalentProfileRepository talentProfileRepository;
    private final StorageService storageService;

    public MediaAssetService(
            MediaAssetRepository mediaAssetRepository,
            TalentProfileRepository talentProfileRepository,
            StorageService storageService
    ) {
        this.mediaAssetRepository = mediaAssetRepository;
        this.talentProfileRepository = talentProfileRepository;
        this.storageService = storageService;
    }

    @Transactional
    public MediaAssetResponse uploadAsset(
            UUID talentId,
            MediaAssetType assetType,
            MultipartFile file
    ) {

        TalentProfile talent =
                talentProfileRepository.findById(talentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Talent not found: " + talentId
                                )
                        );

        if (talent.getStatus() != TalentStatus.ASSETS_PENDING) {
            throw new InvalidRequestException(
                    "Talent must be in ASSETS_PENDING status"
            );
        }

        StoredFileResult storedFile =
                storageService.store(
                        talentId,
                        assetType,
                        file
                );

        MediaAsset asset = new MediaAsset();

        asset.setTalent(talent);
        asset.setAssetType(assetType);
        asset.setStorageKey(
                storedFile.storageKey()
        );
        asset.setOriginalFilename(
                storedFile.originalFilename()
        );
        asset.setMimeType(
                storedFile.mimeType()
        );
        asset.setFileSize(
                storedFile.fileSize()
        );
        asset.setChecksum(
                storedFile.checksum()
        );
        asset.setStatus(
                MediaAssetStatus.UPLOADED
        );

        MediaAsset saved =
                mediaAssetRepository.save(asset);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MediaAssetResponse> getAssets(
            UUID talentId
    ) {

        return mediaAssetRepository
                .findAllByTalent_Id(talentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private MediaAssetResponse toResponse(
            MediaAsset asset
    ) {

        return new MediaAssetResponse(
                asset.getId(),
                asset.getTalent().getId(),
                asset.getAssetType(),
                asset.getStorageKey(),
                asset.getOriginalFilename(),
                asset.getMimeType(),
                asset.getFileSize(),
                asset.getChecksum(),
                asset.getStatus(),
                asset.getCreatedAt()
        );
    }
}