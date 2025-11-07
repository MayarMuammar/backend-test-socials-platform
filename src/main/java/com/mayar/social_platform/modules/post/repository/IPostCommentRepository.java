package com.mayar.social_platform.modules.post.repository;

import com.mayar.social_platform.modules.post.entity.PostCommentDocument;

import java.util.List;
import java.util.Optional;

public interface IPostCommentRepository {
    PostCommentDocument createComment(String postId, String userId, String content);
    Optional<PostCommentDocument> getByIdAndUserId(String commentId, String userId);
    void deleteComment(String commentId, String userId);

    long countByPostId(String postId);
    List<PostCommentDocument> getCommentsByPost(String postId);
}
