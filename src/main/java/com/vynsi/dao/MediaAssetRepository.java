package com.vynsi.dao;

import com.vynsi.entity.MediaAsset;
import com.vynsi.model.MediaAssetStatus;
import com.vynsi.model.MediaAssetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MediaAssetRepository
        extends JpaRepository<MediaAsset, UUID> {

    List<MediaAsset> findAllByTalent_Id(UUID talentId);

    List<MediaAsset> findAllByTalent_IdAndAssetType(
            UUID talentId,
            MediaAssetType assetType
    );

    List<MediaAsset> findAllByTalent_IdAndStatus(
            UUID talentId,
            MediaAssetStatus status
    );

    boolean existsByTalent_IdAndAssetType(
            UUID talentId,
            MediaAssetType assetType
    );

    boolean existsByTalent_IdAndAssetTypeAndStatus(
            UUID talentId,
            MediaAssetType assetType,
            MediaAssetStatus status
    );

    long countByTalent_IdAndAssetType(
            UUID talentId,
            MediaAssetType assetType
    );

    Optional<MediaAsset> findTopByTalent_IdAndAssetTypeOrderByCreatedAtDesc(
            UUID talentId,
            MediaAssetType assetType
    );
}