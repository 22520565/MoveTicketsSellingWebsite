package com.movie.main.resource;

import com.stripe.Stripe;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.FieldNameConstants.Include;

@FieldNameConstants
public final class ResourceStrings {
    private static final Dotenv dotenv = Dotenv.load();

    @Include
    public static final String JWT_RAW_KEY;

    @Include
    public static final String DEFAULT_ADMIN_USERNAME;

    @Include
    public static final String DEFAULT_ADMIN_PASSWORD;

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
        DEFAULT_ADMIN_USERNAME = dotenv.get(ResourceStrings.Fields.DEFAULT_ADMIN_USERNAME, "Admin");
        DEFAULT_ADMIN_PASSWORD = dotenv.get(ResourceStrings.Fields.DEFAULT_ADMIN_PASSWORD, "Admin");
        JWT_RAW_KEY = dotenv.get(ResourceStrings.Fields.JWT_RAW_KEY);
        CLOUDINARY_CLOUD_NAME = dotenv.get(ResourceStrings.Fields.CLOUDINARY_CLOUD_NAME);
        CLOUDINARY_API_KEY = dotenv.get(ResourceStrings.Fields.CLOUDINARY_API_KEY);
        CLOUDINARY_API_SECRET = dotenv.get(ResourceStrings.Fields.CLOUDINARY_API_SECRET);
        STRIPE_SECRET_KEY = dotenv.get(ResourceStrings.Fields.STRIPE_SECRET_KEY);
        STRIPE_WEBHOOK_SECRET = dotenv.get(ResourceStrings.Fields.STRIPE_WEBHOOK_SECRET);
        STRIPE_CURRENCY = dotenv.get(ResourceStrings.Fields.STRIPE_CURRENCY);

        Stripe.apiKey = ResourceStrings.STRIPE_SECRET_KEY;
    }

    private ResourceStrings() {}
}
