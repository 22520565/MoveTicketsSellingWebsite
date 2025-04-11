package com.movie.main.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.util.Locale;

public final class CamelCaseNamingStrategy implements PhysicalNamingStrategy {
    @Override
    public Identifier toPhysicalCatalogName(final Identifier name, final JdbcEnvironment context) {
        return formatIdentifier(name);
    }

    @Override
    public Identifier toPhysicalSchemaName(final Identifier name, final JdbcEnvironment context) {
        return formatIdentifier(name);
    }

    @Override
    public Identifier toPhysicalTableName(final Identifier name, final JdbcEnvironment context) {
        return formatIdentifier(name);
    }

    @Override
    public Identifier toPhysicalSequenceName(final Identifier name, final JdbcEnvironment context) {
        return formatIdentifier(name);
    }

    @Override
    public Identifier toPhysicalColumnName(final Identifier name, final JdbcEnvironment context) {
        return formatIdentifier(name);
    }

    private static Identifier formatIdentifier(final Identifier identifier) {
        if (identifier == null) {
            return identifier;
        }

        final var text = identifier.getText();
        if (text == null || text.isBlank()) {
            return identifier;
        }

        final var words = text.split("_");
        final var formattedName = new StringBuilder();
        formattedName.ensureCapacity((text.length() - words.length) + 1);

        for (final var word : words) {
            if (!word.isBlank()) {
                formattedName.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1));
            }
        }

        return Identifier.toIdentifier(formattedName.toString());
    }
}
