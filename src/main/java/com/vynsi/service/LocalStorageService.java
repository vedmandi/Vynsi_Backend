package com.vynsi.service;

import com.vynsi.dto.StoredFileResult;
import com.vynsi.exception.InvalidRequestException;
import com.vynsi.model.MediaAssetType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

    private final Path rootDirectory;

    public LocalStorageService(
            @Value("${vynsi.storage.local.root}") String rootDirectory
    ) {
        this.rootDirectory = Paths.get(rootDirectory)
                .toAbsolutePath()
                .normalize();
    }

    @Override
    public StoredFileResult store(
            UUID talentId,
            MediaAssetType assetType,
            MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("File is required");
        }

        validateFileType(assetType, file);

        try {

            String originalFilename =
                    file.getOriginalFilename() == null
                            ? "unknown"
                            : Path.of(file.getOriginalFilename())
                            .getFileName()
                            .toString();

            String extension = getExtension(originalFilename);

            String generatedFilename =
                    UUID.randomUUID() + extension;

            String typeDirectory =
                    assetType.name().toLowerCase();

            Path talentDirectory =
                    rootDirectory
                            .resolve("talents")
                            .resolve(talentId.toString())
                            .resolve(typeDirectory);

            Files.createDirectories(talentDirectory);

            Path target =
                    talentDirectory.resolve(generatedFilename);

            try (InputStream inputStream = file.getInputStream()) {

                Files.copy(
                        inputStream,
                        target,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            String checksum = sha256(target);

            String storageKey =
                    "talents/"
                            + talentId
                            + "/"
                            + typeDirectory
                            + "/"
                            + generatedFilename;

            return new StoredFileResult(
                    storageKey,
                    originalFilename,
                    file.getContentType(),
                    file.getSize(),
                    checksum
            );

        } catch (IOException exception) {

            throw new RuntimeException(
                    "Unable to store uploaded file",
                    exception
            );
        }
    }

    private void validateFileType(
            MediaAssetType assetType,
            MultipartFile file
    ) {

        String contentType = file.getContentType();

        if (contentType == null) {
            throw new InvalidRequestException(
                    "Unable to determine file type"
            );
        }

        boolean valid = switch (assetType) {

            case IMAGE ->
                    contentType.startsWith("image/");

            case VOICE ->
                    contentType.startsWith("audio/");

            case VIDEO ->
                    contentType.startsWith("video/");

            case DOCUMENT, IDENTITY_DOCUMENT ->
                    contentType.equals("application/pdf")
                            || contentType.startsWith("image/");
        };

        if (!valid) {
            throw new InvalidRequestException(
                    "Invalid file type "
                            + contentType
                            + " for "
                            + assetType
            );
        }
    }

    private String getExtension(String filename) {

        int index = filename.lastIndexOf('.');

        if (index < 0) {
            return "";
        }

        return filename.substring(index);
    }

    private String sha256(Path file) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            try (InputStream input = Files.newInputStream(file)) {

                byte[] buffer = new byte[8192];

                int bytesRead;

                while ((bytesRead = input.read(buffer)) != -1) {

                    digest.update(
                            buffer,
                            0,
                            bytesRead
                    );
                }
            }

            return HexFormat.of()
                    .formatHex(digest.digest());

        } catch (
                NoSuchAlgorithmException |
                IOException exception
        ) {

            throw new RuntimeException(
                    "Unable to calculate SHA-256 checksum",
                    exception
            );
        }
    }
}