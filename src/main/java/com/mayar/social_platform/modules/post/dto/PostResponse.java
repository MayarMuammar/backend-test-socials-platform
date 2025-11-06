package com.mayar.social_platform.modules.post.dto;

import com.mayar.social_platform.common.dto.BaseResponse;
import com.mayar.social_platform.modules.post.entity.PostStatus;
import com.mayar.social_platform.modules.user.dto.FeedUserResponse;
import com.mayar.social_platform.modules.user.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PostResponse extends BaseResponse {
    private String authorId;
    private String content;
    private ArrayList<String> mediaUrls;
    private UserResponse author;
    private Integer likesCount;
    private Integer commentsCount;
    private PostStatus status;
    private String rejectionReason;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
}
