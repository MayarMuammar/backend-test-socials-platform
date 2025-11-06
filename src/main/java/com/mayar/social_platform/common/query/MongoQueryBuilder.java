package com.mayar.social_platform.common.query;

import com.mayar.social_platform.common.dto.PageQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MongoQueryBuilder implements QueryBuilder<Query> {

    private final List<String> textSearchFields;

    public MongoQueryBuilder(List<String> textSearchFields) {
        this.textSearchFields = textSearchFields != null ? textSearchFields : new ArrayList<>();
    }

    @Override
    public Query buildCountQuery(PageQuery pageQuery) {
        Query query = new Query();

        List<Criteria> criteriaList = buildCriteria(pageQuery);
        for (Criteria criteria : criteriaList) {
            query.addCriteria(criteria);
        }

        return query;
    }

    @Override
    public Query buildQuery(PageQuery pageQuery) {
        Query query = new Query();

        List<Criteria> criteriaList = buildCriteria(pageQuery);
        for (Criteria criteria : criteriaList) {
            query.addCriteria(criteria);
        }

        // Add sorting
        if (pageQuery.getSortBy() != null) {
            org.springframework.data.domain.Sort.Direction direction =
                    pageQuery.getSortDirection().equalsIgnoreCase("asc")
                            ? org.springframework.data.domain.Sort.Direction.ASC
                            : org.springframework.data.domain.Sort.Direction.DESC;

            query.with(org.springframework.data.domain.Sort.by(direction, pageQuery.getSortBy()));
        }

        return query;
    }

    public List<Criteria> buildCriteria(PageQuery pageQuery) {
        List<Criteria> criteriaList = new ArrayList<>();

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

                        Criteria criteria = buildSingleCriteria(fieldName, operator, value);
                        if (criteria != null) {
                            criteriaList.add(criteria);
                        }
                    }
                }
            }
        }

        if (pageQuery.getSearch() != null && !pageQuery.getSearch().isEmpty()) {
            Criteria searchCriteria = buildTextSearchCriteria(pageQuery.getSearch());
            if (searchCriteria != null) {
                criteriaList.add(searchCriteria);
            }
        }

        return criteriaList;
    }

    private Criteria buildSingleCriteria(String fieldName, String operator, Object value) {
        Criteria criteria = Criteria.where(fieldName);

        switch (operator) {
            case "$eq":
                return criteria.is(value);

            case "$ne":
                return criteria.ne(value);

            case "$gt":
                return criteria.gt(value);

            case "$gte":
                return criteria.gte(value);

            case "$lt":
                return criteria.lt(value);

            case "$lte":
                return criteria.lte(value);

            case "$in":
                if (value instanceof List) {
                    return criteria.in((List<?>) value);
                }
                return null;

            case "$nin":
                if (value instanceof List) {
                    return criteria.nin((List<?>) value);
                }
                return null;

            case "$regex":
                String pattern = extractRegexPattern(value);
                String flags = extractRegexFlags(value);
                if (pattern != null) {
                    int regexOptions = flags.contains("i")
                            ? Pattern.CASE_INSENSITIVE
                            : 0;
                    return criteria.regex(Pattern.compile(pattern, regexOptions));
                }
                return null;

            case "$size":
                if (value instanceof Number) {
                    return criteria.size(((Number) value).intValue());
                }
                return null;

            case "sizeGt":
            case "sizeGte":
            case "sizeLt":
            case "sizeLte":
                return buildSizeComparisonCriteria(fieldName, operator, value);

            default:
                return null;
        }
    }

    private Criteria buildSizeComparisonCriteria(String fieldName, String operator, Object value) {
        if (!(value instanceof Number)) {
            return null;
        }

        int size = ((Number) value).intValue();

        // Build $expr query
        String mongoOperator = switch (operator) {
            case "sizeGt" -> "$gt";
            case "sizeGte" -> "$gte";
            case "sizeLt" -> "$lt";
            case "sizeLte" -> "$lte";
            default -> null;
        };

        if (mongoOperator == null) {
            return null;
        }

        // Create $expr criteria
        // { $expr: { $gt: [ { $size: "$fieldName" }, size ] } }
        return Criteria.where("$expr").is(
                Map.of(mongoOperator, Arrays.asList(
                        Map.of("$size", "$" + fieldName),
                        size
                ))
        );
    }

    private Criteria buildTextSearchCriteria(String search) {
        if (textSearchFields.isEmpty()) {
            return null;
        }

        List<Criteria> orCriteria = new ArrayList<>();
        Pattern pattern = Pattern.compile(search, Pattern.CASE_INSENSITIVE);

        for (String field : textSearchFields) {
            orCriteria.add(Criteria.where(field).regex(pattern));
        }

        return new Criteria().orOperator(orCriteria.toArray(new Criteria[0]));
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

    private String extractRegexFlags(Object value) {
        if (value instanceof Map) {
            Object flags = ((Map<?, ?>) value).get("flags");
            return flags != null ? (String) flags : "";
        }
        return "";
    }


}
