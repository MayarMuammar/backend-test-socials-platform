package com.mayar.social_platform.modules.user.dto;

import com.mayar.social_platform.common.dto.BaseResponse;
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
public class FeedUserResponse extends BaseResponse {
    private String fullName;
    private String email;
    private String username;
}
