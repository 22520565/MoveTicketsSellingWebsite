package com.movie.main.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.movie.main.resource.ResourceStrings;

@Configuration
public class CloudinaryConfig {
    @Bean
    Cloudinary cloudinary() {
        return new Cloudinary(String.format("cloudinary://%s:%s@%s", ResourceStrings.CLOUDINARY_API_KEY,
                ResourceStrings.CLOUDINARY_API_SECRET, ResourceStrings.CLOUDINARY_CLOUD_NAME));
    }
}
