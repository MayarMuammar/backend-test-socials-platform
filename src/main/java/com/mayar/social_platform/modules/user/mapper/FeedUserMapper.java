package com.mayar.social_platform.modules.user.mapper;

import com.mayar.social_platform.modules.user.dto.FeedUserResponse;
import com.mayar.social_platform.modules.user.entity.User;
import com.mayar.social_platform.modules.user.entity.UserDocument;

public class FeedUserMapper {
    public static FeedUserResponse toResponse(User user) {
        return FeedUserResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .username(user.getUsername())
                .id(user.getId().toString())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static FeedUserResponse toResponse(UserDocument user ){
        return FeedUserResponse.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .username(user.getUsername())
                .id(user.getId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
