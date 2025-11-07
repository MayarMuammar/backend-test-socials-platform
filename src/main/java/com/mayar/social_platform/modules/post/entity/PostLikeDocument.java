package com.mayar.social_platform.modules.post.entity;

import com.mayar.social_platform.common.entity.BaseDocument;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "likes")
@CompoundIndex(name = "idx_like_post_user", def = "{'post_id': 1, 'user_id': 1}", unique = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikeDocument extends BaseDocument {

    @Id
    private String id;

    @Field("post_id")
    private String postId;

    @Field("user_id")
    private String userId;
}
