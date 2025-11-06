package com.mayar.social_platform.modules.post.entity;

import com.mayar.social_platform.common.entity.BaseDocument;
import com.mayar.social_platform.modules.user.entity.UserDocument;
import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "posts")
@CompoundIndexes({
        @CompoundIndex(name = "status_created_at_idx", def = "{'status': 1, 'created_at': -1}"),
        @CompoundIndex(name = "not_deleted_status_created_at_idx", def = "{'is_deleted': 1, 'status': 1, 'created_at': -1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDocument extends BaseDocument {

    @Field("author_id")
    @Indexed
    private String authorId;

    @Field("content")
    private String content;

    @Field("media_urls")
    private ArrayList<String> mediaUrls;

    @Field("status")
    @Indexed
    private PostStatus status = PostStatus.UNDER_APPROVAL;

    @Field("rejection_reason")
    private String rejectionReason;

    @Field("reviewed_by")
    private String reviewedBy;

    @Field("reviewed_at")
    private LocalDateTime reviewedAt;

    @Field("likes_count")
    private Integer likesCount = 0;

    @Field("comments_count")
    private Integer commentsCount = 0;

    @Transient
    private UserDocument author;
}
