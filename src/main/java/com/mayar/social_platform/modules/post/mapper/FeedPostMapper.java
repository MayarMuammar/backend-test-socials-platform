package com.mayar.social_platform.modules.post.mapper;

import com.mayar.social_platform.modules.post.dto.FeedPostResponse;
import com.mayar.social_platform.modules.post.entity.Post;
import com.mayar.social_platform.modules.post.entity.PostDocument;
import com.mayar.social_platform.modules.user.entity.UserDocument;
import com.mayar.social_platform.modules.user.mapper.FeedUserMapper;

import java.util.ArrayList;
import java.util.List;

public class FeedPostMapper {

    public static FeedPostResponse toResponse(Post post) {
        return FeedPostResponse.builder()
                .id(post.getId().toString())
                .author(FeedUserMapper.toResponse(post.getAuthor()))
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .mediaUrls(post.getMediaUrls() != null && !post.getMediaUrls().isEmpty() ? post.getMediaUrls() : new ArrayList<String>())
                .build();
    }

//    public static List<FeedPostResponse> toResponseList(List<Post> posts) {
//        return posts.stream()
//                .map(FeedPostMapper::toResponse)
//                .toList();
//    }

    public static FeedPostResponse toResponse(PostDocument post) {
        return FeedPostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .author(post.getAuthor() != null
                        ? FeedUserMapper.toResponse(post.getAuthor())
                        : null)
                .mediaUrls(post.getMediaUrls() != null && !post.getMediaUrls().isEmpty()
                        ? post.getMediaUrls()
                        : new ArrayList<String>())
                .build();
    }

    public static List<FeedPostResponse> toResponse(List<PostDocument> postDocuments) {
        return postDocuments.stream()
                .map(FeedPostMapper::toResponse)
                .toList();
    }
}
