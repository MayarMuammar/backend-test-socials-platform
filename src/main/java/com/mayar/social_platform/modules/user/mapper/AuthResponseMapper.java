package com.mayar.social_platform.modules.user.mapper;

import com.mayar.social_platform.modules.user.dto.AuthResponse;
import com.mayar.social_platform.modules.user.entity.UserDocument;

public class AuthResponseMapper {

    public static AuthResponse toResponse(UserDocument user, String token) {
        return AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .username(user.getUsername())
                .accessToken(token)
                .build();
    }
}
