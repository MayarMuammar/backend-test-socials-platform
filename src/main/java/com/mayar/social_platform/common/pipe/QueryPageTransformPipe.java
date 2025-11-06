package com.mayar.social_platform.common.pipe;

import com.mayar.social_platform.common.dto.PageQuery;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class QueryPageTransformPipe {

    private Integer parseIntOrDefault(String value, Integer defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    public PageQuery transform(Map<String, String> params) {
        PageQuery pageQuery = PageQuery.builder().build();

        Map<String, Object> filter = new HashMap<>();

        filter.put("isDeleted", Collections.singletonList(Map.of("operator", "$eq", "value", false)));

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value == null || value.trim().isEmpty()) {
                continue;
            }

            if (key.startsWith("filter.")) {
                processFilter(key, value, filter);
            } else {
                switch (key) {
                    case "page":
                        pageQuery.setPage(parseIntOrDefault(value, 1));
                        break;
                    case "limit":
                        pageQuery.setLimit(parseIntOrDefault(value, null));
                    case "sortBy":
                        pageQuery.setSortBy(value);
                    case "sortDirection":
                        pageQuery.setSortDirection(value);
                    case "search":
                        pageQuery.setSearch(value);
                        break;
                    case "isDeleted":
                        boolean isDeleted = Boolean.parseBoolean(value);
                        filter.put("isDeleted", Collections.singletonList(
                                Map.of("operator", "$eq", "value", isDeleted)
                        ));
                        pageQuery.setIsDeleted(isDeleted);
                        break;
                }
            }
        }
        pageQuery.setFilter(filter);
        return pageQuery;
    }

    private String mapOperator(String operatorKey) {
        Map<String, String> operatorMap = Map.ofEntries(
                Map.entry("eq", "$eq"),
                Map.entry("ne", "$ne"),
                Map.entry("gt", "$gt"),
                Map.entry("gte", "$gte"),
                Map.entry("lt", "$lt"),
                Map.entry("lte", "$lte"),
                Map.entry("in", "$in"),
                Map.entry("nin", "$nin"),
                Map.entry("regex", "$regex"),
                Map.entry("size", "$size"),
                Map.entry("sizeGt", "sizeGt"),
                Map.entry("sizeGte", "sizeGte"),
                Map.entry("sizeLt", "sizeLt"),
                Map.entry("sizeLte", "sizeLte")
        );

        return operatorMap.get(operatorKey);
    }

    private void processFilter(String key, String value, Map<String, Object> filter) {
        String[] keyParts = key.split("\\.");
        if (keyParts.length < 2) {
            return;
        }

        String fieldPath = String.join(".", Arrays.copyOfRange(keyParts, 1, keyParts.length));

        String[] conditions = value.split(";");

        for (String condition : conditions) {
            if (condition.trim().isEmpty()) {
                continue;
            }

            String[] conditionParts = condition.split("::", 2);
            if (conditionParts.length < 2) {
                continue;
            }

            String operatorKey = conditionParts[0].trim();
            String filterValue = conditionParts[1].trim();

            String operator = mapOperator(operatorKey);
            if (operator == null) {
                continue;
            }

            Object processedValue = processValue(operator, filterValue);

            if (!filter.containsKey(fieldPath)) {
                filter.put(fieldPath, new ArrayList<>());
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> fieldConditions = (List<Map<String, Object>>) filter.get(fieldPath);
            if (processedValue != null) {
                fieldConditions.add(Map.of(
                        "operator", operator,
                        "value", processedValue
                ));
            }

        }
    }

    private Object processValue(String operator, String value) {
        switch (operator) {
            case "$in":
            case "$nin":
                return Arrays.asList(value.split(","));

            case "$regex":
                String[] regexParts = value.split("/");
                if (regexParts.length > 1) {
                    return Map.of(
                            "pattern", regexParts[0],
                            "flags", regexParts[1]
                    );
                }
                return Map.of("pattern", value, "flags", "");

            case "$size":
            case "sizeGt":
            case "sizeGte":
            case "sizeLt":
            case "sizeLte":
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return value;
                }

            default:
                if ("true".equalsIgnoreCase(value)) {
                    return true;
                }
                if ("false".equalsIgnoreCase(value)) {
                    return false;
                }
                if ("null".equalsIgnoreCase(value)) {
                    return null;
                }
                try {
                    if (value.contains(".")) {
                        return Double.parseDouble(value);
                    } else {
                        return Integer.parseInt(value);
                    }
                } catch (NumberFormatException e) {
                    return value;
                }
        }
    }

}
