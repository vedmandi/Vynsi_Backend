package com.vynsi.service;

import com.vynsi.dto.CreateUserRequest;
import com.vynsi.dto.UserResponse;
import com.vynsi.entity.User;
import com.vynsi.exception.DuplicateResourceException;
import com.vynsi.exception.ResourceNotFoundException;
import com.vynsi.model.UserStatus;
import com.vynsi.dao.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getUserById(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                "User not found: " + id)
        );

        return toResponse(user);
    }

    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "A user with this email already exists");
        }

        User user = new User();

        user.setEmail(
                request.email()
                        .trim()
                        .toLowerCase()
        );

        user.setDisplayName(
                request.displayName().trim()
        );

        user.setRole(request.role());

        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    private UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}