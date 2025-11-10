package com.mayar.social_platform.modules.post.repository;

import com.mayar.social_platform.modules.post.entity.PostComment;
import com.mayar.social_platform.modules.post.entity.PostCommentDocument;
import com.mayar.social_platform.modules.post.entity.PostLike;
import com.mayar.social_platform.modules.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("postgres")
public class PostCommentRepository implements IPostCommentRepository {

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public PostCommentDocument createComment(String postId, String userId, String content) {
        PostComment comment = new PostComment();
        comment.setPostId(UUID.fromString(postId));
        comment.setUser(entityManager.getReference(User.class, UUID.fromString(userId)));
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        entityManager.persist(comment);
        return null;
    }

    @Override
    public Optional<PostCommentDocument> getByIdAndUserId(String commentId, String userId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PostComment> criteriaQuery = criteriaBuilder.createQuery(PostComment.class);
        Root<PostComment> root = criteriaQuery.from(PostComment.class);

        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("id"), UUID.fromString(commentId))),
                criteriaBuilder.equal(root.get("user").get("id"), UUID.fromString(userId))
        );

        try {
            PostComment postComment = entityManager.createQuery(criteriaQuery).getSingleResult();
            return Optional.of(toDocument(postComment));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteComment(String commentId, String userId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<PostComment> delete = cb.createCriteriaDelete(PostComment.class);
        Root<PostComment> root = delete.from(PostComment.class);

        delete.where(
                cb.and(
                        cb.equal(root.get("id"), UUID.fromString(commentId)),
                        cb.equal(root.get("user").get("id"), UUID.fromString(userId))
                )
        );

        entityManager.createQuery(delete).executeUpdate();
    }

    @Override
    public long countByPostId(String postId) {
        return 0;
    }

    @Override
    public List<PostCommentDocument> getCommentsByPost(String postId) {
        return List.of();
    }

    private PostCommentDocument toDocument(PostComment comment) {
        PostCommentDocument doc = PostCommentDocument.builder()
                .postId(comment.getPostId().toString())
                .userId(comment.getUser().getId().toString())
                .content(comment.getContent())
                .build();
        doc.setCreatedAt(comment.getCreatedAt());
        doc.setUpdatedAt(comment.getUpdatedAt());
        doc.setId(comment.getId().toString());
        return doc;
    }
}
