package com.mayar.social_platform.modules.post.entity;

import com.mayar.social_platform.common.entity.BaseDocument;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommentDocument extends BaseDocument {

    @Field("post_id")
    @Indexed
    private String postId;

    @Field("user_id")
    @Indexed
    private String userId;

    @Field("content")
    private String content;
}
