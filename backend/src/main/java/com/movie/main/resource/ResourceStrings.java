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

    @Include
    public static final String STRIPE_SECRET_KEY;

    @Include
    public static final String STRIPE_WEBHOOK_SECRET;

    @Include
    public static final String STRIPE_CURRENCY;

    static {
        CLOUDINARY_CLOUD_NAME = dotenv.get(ResourceStrings.Fields.CLOUDINARY_CLOUD_NAME);
        CLOUDINARY_API_KEY = dotenv.get(ResourceStrings.Fields.CLOUDINARY_API_KEY);
        CLOUDINARY_API_SECRET = dotenv.get(ResourceStrings.Fields.CLOUDINARY_API_SECRET);
        STRIPE_SECRET_KEY = dotenv.get(ResourceStrings.Fields.STRIPE_SECRET_KEY);
        STRIPE_WEBHOOK_SECRET = dotenv.get(ResourceStrings.Fields.STRIPE_WEBHOOK_SECRET);
        STRIPE_CURRENCY = dotenv.get(ResourceStrings.Fields.STRIPE_CURRENCY);
    }

    private ResourceStrings() {}
}
