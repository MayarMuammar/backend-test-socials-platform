package com.mayar.social_platform.common.query;

import com.mayar.social_platform.common.dto.PageQuery;

public interface QueryBuilder<T> {
    T buildQuery(PageQuery pageQuery);

    T buildCountQuery(PageQuery pageQuery);
}
