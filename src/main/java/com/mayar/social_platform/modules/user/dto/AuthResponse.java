package com.mayar.social_platform.modules.user.dto;

import com.mayar.social_platform.modules.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;

    private String id;
    private String username;
    private String email;
    private String fullName;
    private UserRole role;
}
