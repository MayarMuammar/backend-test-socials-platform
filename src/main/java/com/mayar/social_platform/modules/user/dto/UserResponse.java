package com.mayar.social_platform.modules.user.dto;

import com.mayar.social_platform.common.dto.BaseResponse;
import com.mayar.social_platform.modules.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserResponse extends BaseResponse {
    private String fullName;
    private String email;
    private String username;
    private UserRole role;
}
