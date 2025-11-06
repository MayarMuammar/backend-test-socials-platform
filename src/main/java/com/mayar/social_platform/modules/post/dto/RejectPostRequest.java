package com.mayar.social_platform.modules.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectPostRequest {
    @NotBlank(message = "Rejection reason is required")
    @Size(min = 10, message = "Rejection reason is required")
    private String rejectionReason;
}
