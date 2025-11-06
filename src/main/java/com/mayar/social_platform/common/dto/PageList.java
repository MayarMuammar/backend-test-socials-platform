package com.mayar.social_platform.common.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageList<T> {

    private List<T> items;
    private Long totalItems;
    private Integer itemsPerPage;
    private Integer currentPage;
    private Integer totalPages;
    private Integer limit;


    public static <T> PageList<T> of(List<T> items, long totalItems, Integer page, Integer limit) {
        int totalPages = (int) Math.ceil((double) totalItems /limit);

        return PageList.<T>builder()
                .items(items)
                .totalItems(totalItems)
                .itemsPerPage(items.size())
                .currentPage(page)
                .totalPages(totalPages)
                .limit(limit)
                .build();

    }

}
