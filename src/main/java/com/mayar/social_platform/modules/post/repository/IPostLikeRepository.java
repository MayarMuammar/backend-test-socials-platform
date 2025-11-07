package com.mayar.social_platform.modules.post.repository;

import com.mayar.social_platform.modules.post.entity.PostLikeDocument;

import java.util.List;

public interface IPostLikeRepository {
    boolean existsByPostIdAndUserId(String postId, String userId);
    void saveLike(String postId, String userId);
    void removeLike(String postId, String userId);
    long getLikeCount(String postId);
    List<PostLikeDocument> getLikeByPostId(String postId);
}
