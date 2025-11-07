package com.mayar.social_platform.common.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageQuery {

    @Builder.Default
    private Integer page = 1;

    private Integer limit;

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortDirection = "desc";

    @Builder.Default
    private Boolean isDeleted = false;

    @Builder.Default
    private Map<String, Object> filter = new HashMap<>();

    private String search;
}
