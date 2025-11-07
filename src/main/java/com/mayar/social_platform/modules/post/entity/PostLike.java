package com.mayar.social_platform.modules.post.entity;

import com.mayar.social_platform.common.entity.BaseEntity;
import com.mayar.social_platform.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "likes", indexes = {
        @Index(name = "idx_like_post_user", columnList = "post_id, user_id", unique = true),
        @Index(name = "idx_like_post", columnList = "post_id"),
        @Index(name = "idx_like_user", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLike extends BaseEntity {

    @Column(name = "post_id", nullable = false)
    private UUID postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
