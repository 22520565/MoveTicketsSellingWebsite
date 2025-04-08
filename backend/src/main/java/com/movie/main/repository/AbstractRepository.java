package com.movie.main.repository;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ReflectionUtils;

import com.movie.main.dto.InterfaceDTO;
import com.movie.main.entity.Identifiable;
import com.movie.main.ulti.Expected;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractRepository<TEntity extends Identifiable<TKey>, TDto extends InterfaceDTO, TKey> {
    @Nonnull
    protected final EntityManager entityManager;

    @Nonnull
    protected final Class<TEntity> entityClass;

    @Nonnull
    protected final Class<TDto> dtoClass;

    protected AbstractRepository(
            @Nonnull final EntityManager entityManager,
            @Nonnull final Class<TEntity> entityClass,
            @Nonnull final Class<TDto> infoDtoClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
        this.dtoClass = infoDtoClass;
    }

    @Nullable
    public TEntity findById(@Nonnull final TKey id) {
        try {
            return this.entityManager.find(this.entityClass, id);
        } catch (final Exception exception) {
            log.error(null, exception);
            return null;
        }
    }

    @Nullable
    public TEntity findSingleByField(final String fieldName, final Object objectEqualTo) {
        return this.findSingleByField(ReflectionUtils.findField(this.entityClass, fieldName), objectEqualTo);
    }

    @Nullable
    public TEntity findSingleByField(final Field field, final Object objectEqualTo) {
        if (field == null) {
            return null;
        }

        try {
            final var cb = entityManager.getCriteriaBuilder();
            final var query = cb.createQuery(this.entityClass);
            final var root = query.from(this.entityClass);

            final var columnPath = AbstractRepository.getColumnPath(root, field);
            query.select(root).where(cb.equal(columnPath, objectEqualTo));

            final var resultList = entityManager.createQuery(query).setMaxResults(2).getResultList();
            if (resultList.size() != 1) {
                return null;
            }

            return resultList.getFirst();
        } catch (final Exception exception) {
            log.error(null, exception);
            return null;
        }
    }

    @Nonnull
    public List<TEntity> findAllByField(final String fieldName, final Object objectEqualTo) {
        return this.findAllByField(ReflectionUtils.findField(this.entityClass, fieldName), objectEqualTo);
    }

    @Nonnull
    public List<TEntity> findAllByField(final Field field, @Nonnull final Object objectEqualTo) {
        if (field == null) {
            return Collections.emptyList();
        }

        try {
            final var cb = entityManager.getCriteriaBuilder();
            final var query = cb.createQuery(this.entityClass);
            final var root = query.from(this.entityClass);

            final var columnPath = AbstractRepository.getColumnPath(root, field);
            query.select(root).where(cb.equal(columnPath, objectEqualTo));

            return entityManager.createQuery(query).getResultList();
        } catch (final Exception exception) {
            log.error(null, exception);
            return Collections.emptyList();
        }
    }

    @Nonnull
    public List<TEntity> findAllByField(@Nonnull final Field field, @Nonnull final Object objectEqualTo,
            final int maxAmount) {
        try {
            final var cb = entityManager.getCriteriaBuilder();
            final var query = cb.createQuery(this.entityClass);
            final var root = query.from(this.entityClass);

            final var columnPath = AbstractRepository.getColumnPath(root, field);
            query.select(root).where(cb.equal(columnPath, objectEqualTo));

            return entityManager.createQuery(query).setMaxResults(maxAmount).getResultList();
        } catch (final Exception exception) {
            log.error(null, exception);
            return Collections.emptyList();
        }
    }

    @Nullable
    public TDto findDataById(@Nonnull final TKey id) {
        try {
            final var cb = entityManager.getCriteriaBuilder();
            final var query = cb.createQuery(this.dtoClass);
            final var root = query.from(this.entityClass);

            final var idPath = AbstractRepository.getIdExpression(this.entityClass, root);
            if (idPath == null) {
                return null;
            }

            final var selections = AbstractRepository.getNonIdSelections(this.entityClass, root);
            query.multiselect(selections);
            query.where(cb.equal(idPath, id));

            final var resultList = entityManager.createQuery(query).setMaxResults(2).getResultList();
            if (resultList.size() != 1) {
                return null;
            }

            return resultList.getFirst();
        } catch (final Exception exception) {
            log.error(null, exception);
            return null;
        }
    }

    @Nonnull
    public Page<TDto> findAllData(@Nonnull final Pageable pageable) {
        final var offset = pageable.getOffset();
        if (offset > Integer.MAX_VALUE) {
            log.error("Error while fetching paginated data: Offset is out of range");
            return Page.empty();
        }

        final var offsetInt = (int) offset;

        try {
            final var cb = entityManager.getCriteriaBuilder();
            final var query = cb.createQuery(this.dtoClass);
            final var root = query.from(this.entityClass);

            final var selections = AbstractRepository.getNonIdSelections(this.entityClass, root);
            query.multiselect(selections);

            final var resultList = this.entityManager.createQuery(query)
                    .setFirstResult(offsetInt)
                    .setMaxResults(pageable.getPageSize())
                    .getResultList();

            final var countQuery = cb.createQuery(Long.class);
            countQuery.select(cb.count(countQuery.from(this.entityClass)));
            final var total = this.entityManager.createQuery(countQuery).getSingleResult();

            return new PageImpl<>(resultList, pageable, total);
        } catch (final Exception exception) {
            log.error("Error while fetching paginated data", exception);
            return Page.empty();
        }
    }

    public enum AddError {
        EntityExists,
        Persistence,
        Unspecified,
    }

    @Transactional
    public Expected<TEntity, AddError> add(@Nonnull @Valid final TEntity entity) {
        try {
            this.entityManager.persist(entity);
            this.entityManager.flush();
            return Expected.success(entity);
        } catch (final EntityExistsException exception) {
            return Expected.failure(AddError.EntityExists);
        } catch (final PersistenceException exception) {
            return Expected.failure(AddError.Persistence);
        } catch (final Exception exception) {
            log.error(null, exception);
            return Expected.failure(AddError.Unspecified);
        }
    }

    public enum UpdateError {
        EntityNotExists,
        Persistence,
        Unspecified,
    }

    @Transactional
    public Expected<TEntity, UpdateError> update(@Nonnull @Valid final TEntity entity) {
        try {
            return Expected.success(this.entityManager.merge(entity));
        } catch (final IllegalArgumentException exception) {
            return Expected.failure(UpdateError.EntityNotExists);
        } catch (final PersistenceException exception) {
            return Expected.failure(UpdateError.Persistence);
        } catch (final Exception exception) {
            log.error(null, exception);
            return Expected.failure(UpdateError.Unspecified);
        }
    }

    public enum DeleteStatus {
        Success,
        EntityNotExistsError,
        PersistenceError,
        UnspecifiedError,
    }

    @Transactional
    public DeleteStatus deleteById(@Nonnull final TKey id) {
        try {
            final var cb = entityManager.getCriteriaBuilder();
            final var delete = cb.createCriteriaDelete(this.entityClass);
            final var root = delete.from(this.entityClass);

            final var idExpression = AbstractRepository.getIdExpression(this.entityClass, root);
            delete.where(cb.equal(idExpression, id));

            final var succeed = entityManager.createQuery(delete).executeUpdate() > 0;
            if (!succeed) {
                return DeleteStatus.EntityNotExistsError;
            }

            return DeleteStatus.Success;
        } catch (final PersistenceException exception) {
            return DeleteStatus.PersistenceError;
        } catch (final Exception exception) {
            log.error(null, exception);
            return DeleteStatus.UnspecifiedError;
        }
    }

    @Nullable
    protected static <T> Expression<T> getIdExpression(
            @Nonnull final Class<T> entityClass,
            @Nonnull final Root<T> root) {
        final var idExpressions = Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class)
                        && field.isAnnotationPresent(Id.class))
                .map(field -> AbstractRepository.getColumnPath(root, field))
                .toList();

        if (idExpressions.size() != 1) {
            return null;
        }

        return idExpressions.getFirst();
    }

    @Nonnull
    protected static <T> List<Selection<?>> getNonIdSelections(
            @Nonnull final Class<T> entityClass,
            @Nonnull final Root<T> root) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> (!field.isAnnotationPresent(Id.class))
                        && (field.isAnnotationPresent(Column.class)
                                || field.isAnnotationPresent(JoinColumn.class)))
                .map((final var field) -> {
                    if (field.isAnnotationPresent(Column.class)) {
                        return AbstractRepository.getColumnPath(root, field);
                    } else {
                        return AbstractRepository.getJoinColumnPath(root, field);
                    }
                })
                .collect(Collectors.toList());
    }

    @NotBlank
    protected static String getColumnName(@Nonnull final Field field) {
        final var column = field.getAnnotation(Column.class);

        if ((column != null) && (!column.name().isBlank())) {
            return column.name();
        }

        return field.getName();
    }

    protected static <T> Path<T> getColumnPath(@Nonnull final Root<T> root, @Nonnull final Field field) {
        return root.get(AbstractRepository.getColumnName(field));
    }

    @NotBlank
    protected static String getJoinColumnName(@Nonnull final Field field) {
        final var joinColumn = field.getAnnotation(JoinColumn.class);

        if ((joinColumn != null) && (!joinColumn.name().isBlank())) {
            return joinColumn.name();
        }

        final var relatedEntity = field.getType();
        final var idFieldsList = Arrays.stream(relatedEntity.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .toList();

        if (idFieldsList.size() != 1) {
            return null;
        }
        final var idField = idFieldsList.getFirst();

        return String.format("%s_%s", field.getName(), idField.getName());
    }

    @Nullable
    protected static <T> Path<T> getJoinColumnPath(@Nonnull final Root<T> root, @Nonnull final Field field) {
        final var relatedEntity = field.getType();

        final var idFieldsList = Arrays.stream(relatedEntity.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .toList();

        if (idFieldsList.size() != 1) {
            return null;
        }
        final var idField = idFieldsList.getFirst();

        return root.get(field.getName()).get(idField.getName());
    }
}
