package com.movie.main.resource;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.FieldNameConstants.Include;

@FieldNameConstants
public final class ResourceStrings {
    private static final Dotenv dotenv = Dotenv.load();

    @Include
    public static final String CLOUDINARY_CLOUD_NAME;

    @Include
    public static final String CLOUDINARY_API_KEY;

    @Include
    public static final String CLOUDINARY_API_SECRET;

    static {
        CLOUDINARY_CLOUD_NAME = dotenv.get(ResourceStrings.Fields.CLOUDINARY_CLOUD_NAME);
        CLOUDINARY_API_KEY = dotenv.get(ResourceStrings.Fields.CLOUDINARY_API_KEY);
        CLOUDINARY_API_SECRET = dotenv.get(ResourceStrings.Fields.CLOUDINARY_API_SECRET);
    }

    private ResourceStrings() {}
}
