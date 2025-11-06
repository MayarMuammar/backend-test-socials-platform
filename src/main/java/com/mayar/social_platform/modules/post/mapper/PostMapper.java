package com.mayar.social_platform.modules.post.mapper;

import com.mayar.social_platform.modules.post.dto.PostResponse;
import com.mayar.social_platform.modules.post.entity.Post;
import com.mayar.social_platform.modules.post.entity.PostDocument;
import com.mayar.social_platform.modules.user.entity.UserDocument;
import com.mayar.social_platform.modules.user.mapper.UserMapper;

import java.util.ArrayList;

public class PostMapper {

    public static PostResponse toResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId().toString())
                .author(UserMapper.toResponse(post.getAuthor()))
                .content(post.getContent())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .status(post.getStatus())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .rejectionReason(post.getRejectionReason())
                .reviewedAt(post.getReviewedAt())
                .reviewedBy(post.getReviewedBy())
                .mediaUrls(post.getMediaUrls() != null && !post.getMediaUrls().isEmpty() ? post.getMediaUrls() : new ArrayList<String>())
                .build();
    }

    public static PostResponse toResponse(PostDocument post) {
        return PostResponse.builder()
                .id(post.getId())
                .author(UserMapper.toResponse(post.getAuthor()))
                .content(post.getContent())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .status(post.getStatus())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .rejectionReason(post.getRejectionReason())
                .reviewedAt(post.getReviewedAt())
                .reviewedBy(post.getReviewedBy())
                .mediaUrls(post.getMediaUrls() != null && !post.getMediaUrls().isEmpty() ? post.getMediaUrls() : new ArrayList<String>())
                .build();
    }
}
