package com.mayar.social_platform.modules.post.repository;

import com.mayar.social_platform.common.dto.PageList;
import com.mayar.social_platform.common.dto.PageQuery;
import com.mayar.social_platform.modules.post.entity.PostDocument;

import java.util.Optional;

public interface IPostRepository {
    PostDocument create(PostDocument postDocument, String userId);

    Optional<PostDocument> findById(String id);

    Optional<PostDocument> approve(String id, String adminUsername);

    Optional<PostDocument> reject(String id, String rejectionReason, String adminUsername);

    PageList<PostDocument> getPosts(PageQuery pageQuery);

    long count(PageQuery pageQuery);
}
