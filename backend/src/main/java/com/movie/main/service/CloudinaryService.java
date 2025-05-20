package com.movie.main.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.movie.main.dto.internal.CloudinaryImage;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(@NotNull final Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Nullable
    public CloudinaryImage uploadImage(@NotNull final MultipartFile file) {
        try {
            final var result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return new CloudinaryImage(result.get("secure_url").toString(), result.get("public_id").toString());
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return null;
        }
    }

    public boolean deleteImage(@NotBlank final String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return true;
        }
        catch (final Exception exception) {
            log.error(exception.getMessage());
            return false;
        }
    }
}
