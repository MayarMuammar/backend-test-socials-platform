package com.mayar.social_platform.modules.user.mapper;

import com.mayar.social_platform.modules.user.dto.UserResponse;
import com.mayar.social_platform.modules.user.entity.User;
import com.mayar.social_platform.modules.user.entity.UserDocument;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .fullName(user.getFullName())
                .id(user.getId().toString())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public static UserResponse toResponse(UserDocument user) {
        return UserResponse.builder()
                .fullName(user.getFullName())
                .id(user.getId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
