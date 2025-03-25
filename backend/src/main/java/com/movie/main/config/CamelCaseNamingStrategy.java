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
        if (identifier == null || identifier.getText().isEmpty()) {
            return identifier;
        }

        final var words = identifier.getText().split("_");
        final var formattedName = new StringBuilder();
        for (final String word : words) {
            if (!word.isEmpty()) {
                formattedName.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase(Locale.ROOT));
            }
        }

        return Identifier.toIdentifier(formattedName.toString());
    }
}
