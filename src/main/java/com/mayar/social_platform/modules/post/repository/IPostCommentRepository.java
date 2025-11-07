package com.mayar.social_platform.modules.post.repository;

import com.mayar.social_platform.modules.post.entity.PostCommentDocument;

import java.util.List;

public interface IPostCommentRepository {
    PostCommentDocument createComment(String postId, String userId, String content);
    void deleteComment(String postId, String userId);
    long countByPostId(String postId);
    List<PostCommentDocument> getCommentsByPost(String postId);
}
