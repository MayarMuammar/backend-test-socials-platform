package com.mayar.social_platform.modules.post.entity;

import com.mayar.social_platform.common.entity.BaseEntity;
import com.mayar.social_platform.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name= "posts", indexes = {
        @Index(name = "idx_post_status", columnList = "status"),
        @Index(name = "idx_post_author_id", columnList = "author_id"),
        @Index(name = "idx_post_status_created_at", columnList = "status created_at DESC"),
        @Index(name = "idx_post_not_deleted", columnList = "is_deleted, status, created_at DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "media_urls")
    private ArrayList<String> mediaUrls;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PostStatus status = PostStatus.UNDER_APPROVAL;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "likes_count", nullable = false)
    private Integer likesCount = 0;

    @Column(name = "comments_count", nullable = false)
    private Integer commentsCount = 0;
}
