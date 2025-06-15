package example.converter;

import example.domain.Tag;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.ConversionContext;
import io.micronaut.data.exceptions.DataAccessException;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.data.model.runtime.convert.AttributeConverter;
import jakarta.inject.Singleton;

import java.sql.Array;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The converter is used to convert an SQL enum java.sql.Array value to a set of example.domain.Tag and back.
 */
@Singleton
public class TagArrayConverter implements AttributeConverter<Set<Tag>, Array> {
    private final JdbcOperations jdbcOperations;

    /**
     * @param jdbcOperations is needed to get the current DB connection safe. It's necessary to construct java.sqlArray
     */
    public TagArrayConverter(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public @Nullable Array convertToPersistedValue(@Nullable Set<Tag> tags, @NonNull ConversionContext context) {
        return jdbcOperations.execute(connection -> connection.createArrayOf("tag", tags.toArray()));
    }

    @Override
    public @Nullable Set<Tag> convertToEntityValue(@Nullable Array sqlArray, @NonNull ConversionContext context) {
        return Optional.ofNullable(sqlArray)
                .map(array -> {
                    try {
                        return (String[]) array.getArray();
                    } catch (SQLException e) {
                        throw new DataAccessException("Can't get array value: " + e.getMessage(), e);
                    }
                })
                .stream()
                .flatMap(Stream::of)
                .map(Tag::valueOf)
                .collect(Collectors.toSet());
    }
}
