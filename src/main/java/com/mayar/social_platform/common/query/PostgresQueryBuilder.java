package com.mayar.social_platform.common.query;

import com.mayar.social_platform.common.dto.PageQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostgresQueryBuilder<T> implements QueryBuilder<CriteriaQuery<T>> {

    private final EntityManager entityManager;
    private final Class<T> entityClass;
    private final List<String> textSearchFields;

    public PostgresQueryBuilder(EntityManager entityManager,
                                Class<T> entityClass,
                                List<String> textSearchFields) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
        this.textSearchFields = textSearchFields != null ? textSearchFields : new ArrayList<>();
    }

    @Override
    public CriteriaQuery<T> buildQuery(PageQuery pageQuery) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(entityClass);
        Root<T> root = query.from(entityClass);

        List<Predicate> predicates = buildPredicates(cb, root, pageQuery);
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }

        if (pageQuery.getSortBy() != null) {
            Order order = pageQuery.getSortDirection().equalsIgnoreCase("asc")
                    ? cb.asc(root.get(pageQuery.getSortBy()))
                    : cb.desc(root.get(pageQuery.getSortBy()));
            query.orderBy(order);
        }

        return query;
    }

    @Override
    public CriteriaQuery<T> buildCountQuery(PageQuery pageQuery) {
        return buildQuery(pageQuery);
    }

    public List<Predicate> buildPredicates(CriteriaBuilder cb, Root<T> root, PageQuery pageQuery) {
        List<Predicate> predicates = new ArrayList<>();

        if (pageQuery.getFilter() != null && !pageQuery.getFilter().isEmpty()) {
            for (Map.Entry<String, Object> entry : pageQuery.getFilter().entrySet()) {
                String fieldName = entry.getKey();
                Object filterValue = entry.getValue();

                if (filterValue instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> conditions = (List<Map<String, Object>>) filterValue;
                    for (Map<String, Object> condition : conditions) {
                        String operator = (String) condition.get("operator");
                        Object value = condition.get("value");

                        Predicate predicate = buildPredicate(cb, root, fieldName, operator, value);
                        if (predicate != null) {
                            predicates.add(predicate);
                        }
                    }
                }
            }
        }

        if (pageQuery.getSearch() != null && !pageQuery.getSearch().trim().isEmpty()) {
            List<Predicate> searchPredicates = buildTextSearchPredicates(cb, root, pageQuery.getSearch());
            if (!searchPredicates.isEmpty()) {
                predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
            }
        }

        return predicates;
    }

    private List<Predicate> buildTextSearchPredicates(CriteriaBuilder cb, Root<T> root, String search) {
        List<Predicate> searchPredicates = new ArrayList<>();
        String searchLower = "%" + search.toLowerCase() + "%";

        for (String field : textSearchFields) {
            try {
                Path<String> path = root.get(field);
                searchPredicates.add(cb.like(cb.lower(path), searchLower));
            } catch (Exception e) {
                // Field might not exist or not be searchable
            }
        }

        return searchPredicates;
    }

    private Predicate buildPredicate(CriteriaBuilder cb,
                                     Root<T> root, String fieldName,
                                     String operator, Object value) {
        try {
            Path<Object> path = root.get(fieldName);

            switch (operator) {
                case "$eq":
                    return value == null ? cb.isNull(path) : cb.equal(path, value);
                case "$ne":
                    return value == null ? cb.isNotNull(path) : cb.notEqual(path, value);
                case "$lt":
                    return cb.lessThan(path.as(Comparable.class), (Comparable) convertValue(value));
                case "$lte":
                    return cb.lessThanOrEqualTo(path.as(Comparable.class), (Comparable) convertValue(value));
                case "$gt":
                    return cb.greaterThan(path.as(Comparable.class), (Comparable) convertValue(value));
                case "$gte":
                    return cb.greaterThanOrEqualTo(path.as(Comparable.class), (Comparable) convertValue(value));
                case "$in":
                    if (value instanceof List<?> values) {
                        return path.in(values.stream().map(this::convertValue).toList());
                    }
                    return null;

                case "$nin":
                    if (value instanceof List<?> values) {
                        return cb.not(path.in(values.stream().map(this::convertValue).toList()));
                    }
                    return null;

                case "$regex":
                    String pattern = extractRegexPattern(value);
                    if (pattern != null) {
                        return cb.like(cb.lower(path.as(String.class)),
                                "%" + pattern.toLowerCase() + "%");
                    }
                    return null;

                default:
                    return null;
            }
        } catch (Exception e) {
            System.err.println("Error building predicate for field: " + fieldName +
                    ", operator: " + operator + ", error: " + e.getMessage());
            return null;
        }
    }

    private String extractRegexPattern(Object value) {
        if (value instanceof String) {
            return (String) value;
        }
        if (value instanceof Map) {
            return (String) ((Map<?, ?>) value).get("pattern");
        }
        return null;
    }


    private Object convertValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String strValue) {
            try {
                return LocalDateTime.parse(strValue);
            } catch (Exception ignored) {
            }
            try {
                return LocalDate.parse(strValue);
            } catch (Exception ignored) {
            }
        }

        return value;
    }


}
