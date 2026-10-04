package com.vynsi.service;

import com.vynsi.dao.TalentProfileRepository;
import com.vynsi.dao.UserRepository;
import com.vynsi.dto.CreateTalentRequest;
import com.vynsi.dto.TalentResponse;
import com.vynsi.entity.TalentProfile;
import com.vynsi.entity.User;
import com.vynsi.exception.DuplicateResourceException;
import com.vynsi.exception.InvalidRequestException;
import com.vynsi.exception.ResourceNotFoundException;
import com.vynsi.model.TalentStatus;
import com.vynsi.model.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TalentService {

    private final TalentProfileRepository talentProfileRepository;
    private final UserRepository userRepository;

    public TalentService(
            TalentProfileRepository talentProfileRepository,
            UserRepository userRepository
    ) {
        this.talentProfileRepository = talentProfileRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<TalentResponse> getAllTalents() {

        return talentProfileRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TalentResponse getTalentById(UUID id) {

        TalentProfile talent =
                talentProfileRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Talent profile not found: " + id
                                )
                        );

        return toResponse(talent);
    }

    @Transactional(readOnly = true)
    public TalentResponse getTalentByUserId(UUID userId) {

        TalentProfile talent =
                talentProfileRepository.findByUser_Id(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Talent profile not found for user: "
                                                + userId
                                )
                        );

        return toResponse(talent);
    }

    @Transactional
    public TalentResponse createTalent(
            CreateTalentRequest request
    ) {

        User user = userRepository
                .findById(request.userId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: "
                                        + request.userId()
                        )
                );

        if (user.getRole() != UserRole.TALENT) {
            throw new IllegalArgumentException(
                    "Only users with TALENT role can create a talent profile"
            );
        }

        if (talentProfileRepository.existsByUser_Id(user.getId())) {
            throw new InvalidRequestException(
                    "A talent profile already exists for this user"
            );
        }

        TalentProfile talent = new TalentProfile();

        talent.setUser(user);

        talent.setLegalName(
                normalize(request.legalName())
        );

        talent.setStageName(
                normalize(request.stageName())
        );

        talent.setBiography(
                normalize(request.biography())
        );

        talent.setCountryCode(
                normalizeCountryCode(request.countryCode())
        );

        talent.setStatus(TalentStatus.DRAFT);

        TalentProfile savedTalent =
                talentProfileRepository.save(talent);

        return toResponse(savedTalent);
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

    private String normalize(String value) {

        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        return trimmed.isEmpty()
                ? null
                : trimmed;
    }

    private String normalizeCountryCode(String value) {

        String normalized = normalize(value);

        return normalized == null
                ? null
                : normalized.toUpperCase();
    }
}