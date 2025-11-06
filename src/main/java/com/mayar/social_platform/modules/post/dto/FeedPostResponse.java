package com.mayar.social_platform.modules.post.dto;

import com.mayar.social_platform.common.dto.BaseResponse;
import com.mayar.social_platform.modules.user.dto.FeedUserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FeedPostResponse extends BaseResponse {
    private String content;
    private ArrayList<String> mediaUrls;
    private FeedUserResponse author;
    private Integer likesCount;
    private Integer commentsCount;
}
