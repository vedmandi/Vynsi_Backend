package com.vynsi.service;

import com.vynsi.dao.IdentityVerificationRepository;
import com.vynsi.dao.MediaAssetRepository;
import com.vynsi.dao.TalentProfileRepository;
import com.vynsi.dto.TalentResponse;
import com.vynsi.entity.IdentityVerification;
import com.vynsi.entity.TalentProfile;
import com.vynsi.entity.User;
import com.vynsi.exception.InvalidRequestException;
import com.vynsi.exception.ResourceNotFoundException;
import com.vynsi.model.IdentityVerificationStatus;
import com.vynsi.model.MediaAssetStatus;
import com.vynsi.model.MediaAssetType;
import com.vynsi.model.TalentStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TalentOnboardingService {

    private final TalentProfileRepository talentProfileRepository;
    private final IdentityVerificationRepository identityVerificationRepository;
    private final MediaAssetRepository mediaAssetRepository;


    public TalentOnboardingService(
            TalentProfileRepository talentProfileRepository,
            IdentityVerificationRepository identityVerificationRepository,
            MediaAssetRepository mediaAssetRepository
    ) {
        this.talentProfileRepository = talentProfileRepository;
        this.identityVerificationRepository = identityVerificationRepository;
        this.mediaAssetRepository = mediaAssetRepository;
    }

    @Transactional
    public TalentResponse startIdentityVerification(UUID talentId) {

        TalentProfile talent = getTalent(talentId);

        IdentityVerification verification =
                new IdentityVerification();

        verification.setTalent(talent);

        verification.setProvider("MANUAL");

        verification.setVerificationStatus(
                IdentityVerificationStatus.PENDING
        );

        identityVerificationRepository.save(verification);

        requireStatus(talent, TalentStatus.DRAFT);

        validateProfileForIdentity(talent);

        talent.setStatus(TalentStatus.IDENTITY_PENDING);

        TalentProfile saved =
                talentProfileRepository.save(talent);


        return toResponse(saved);
    }

    @Transactional
    public TalentResponse verifyIdentity(UUID talentId) {

        TalentProfile talent = getTalent(talentId);

        requireStatus(
                talent,
                TalentStatus.IDENTITY_PENDING
        );

        IdentityVerification verification =
                identityVerificationRepository
                        .findTopByTalent_IdOrderByCreatedAtDesc(talentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Identity verification not found"
                                )
                        );

        verification.setVerificationStatus(
                IdentityVerificationStatus.VERIFIED
        );

        verification.setVerifiedAt(
                java.time.Instant.now()
        );

        identityVerificationRepository.save(verification);

        talent.setStatus(
                TalentStatus.ASSETS_PENDING
        );

        TalentProfile saved =
                talentProfileRepository.save(talent);

        return toResponse(saved);
    }

    @Transactional
    public TalentResponse completeAssets(UUID talentId) {

        TalentProfile talent = getTalent(talentId);

        requireStatus(
                talent,
                TalentStatus.ASSETS_PENDING
        );

        boolean hasImage =
                mediaAssetRepository
                        .existsByTalent_IdAndAssetTypeAndStatus(
                                talentId,
                                MediaAssetType.IMAGE,
                                MediaAssetStatus.UPLOADED
                        );

        boolean hasVoice =
                mediaAssetRepository
                        .existsByTalent_IdAndAssetTypeAndStatus(
                                talentId,
                                MediaAssetType.VOICE,
                                MediaAssetStatus.UPLOADED
                        );

        boolean hasVideo =
                mediaAssetRepository
                        .existsByTalent_IdAndAssetTypeAndStatus(
                                talentId,
                                MediaAssetType.VIDEO,
                                MediaAssetStatus.UPLOADED
                        );

        if (!hasImage || !hasVoice || !hasVideo) {

            throw new InvalidRequestException(
                    "IMAGE, VOICE and VIDEO assets are required before completing asset onboarding"
            );
        }

        talent.setStatus(
                TalentStatus.CONSENT_PENDING
        );

        TalentProfile saved =
                talentProfileRepository.save(talent);

        return toResponse(saved);
    }

    private TalentProfile getTalent(UUID talentId) {
        return talentProfileRepository.findById(talentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Talent profile not found: " + talentId
                        )
                );
    }

    private void requireStatus(
            TalentProfile talent,
            TalentStatus requiredStatus
    ) {

        if (talent.getStatus() != requiredStatus) {

            throw new InvalidRequestException(
                    "Talent must be in "
                            + requiredStatus
                            + " status. Current status: "
                            + talent.getStatus()
            );
        }
    }

    private void validateProfileForIdentity(
            TalentProfile talent
    ) {

        if (isBlank(talent.getLegalName())) {
            throw new InvalidRequestException(
                    "Legal name is required before identity verification"
            );
        }

        if (isBlank(talent.getCountryCode())) {
            throw new InvalidRequestException(
                    "Country code is required before identity verification"
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private TalentResponse toResponse(
            TalentProfile talent
    ) {

        User user = talent.getUser();

        return new TalentResponse(
                talent.getId(),
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                talent.getLegalName(),
                talent.getStageName(),
                talent.getBiography(),
                talent.getCountryCode(),
                talent.getStatus(),
                talent.getCreatedAt(),
                talent.getUpdatedAt()
        );
    }
}