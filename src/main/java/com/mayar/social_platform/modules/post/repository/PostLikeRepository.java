package com.mayar.social_platform.modules.post.repository;

import com.mayar.social_platform.modules.post.entity.PostLike;
import com.mayar.social_platform.modules.post.entity.PostLikeDocument;
import com.mayar.social_platform.modules.user.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@Profile("postgres")
public class PostLikeRepository implements IPostLikeRepository {

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {}

    @Override
    public boolean existsByPostIdAndUserId(String postId, String userId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<PostLike> root = criteriaQuery.from(PostLike.class);

        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.and(
                criteriaBuilder.equal(root.get("postId"), UUID.fromString(postId))),
                criteriaBuilder.equal(root.get("user").get("id"), UUID.fromString(userId))
        );

        Long count = entityManager.createQuery(criteriaQuery).getSingleResult();
        return count > 0;
    }

    @Override
    public void saveLike(String postId, String userId) {
        PostLike like = new PostLike();
        like.setPostId(UUID.fromString(postId));
        like.setUser(entityManager.getReference(User.class, UUID.fromString(userId)));
        like.setCreatedAt(LocalDateTime.now());
        entityManager.persist(like);
    }

    @Override
    public void removeLike(String postId, String userId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<PostLike> delete = cb.createCriteriaDelete(PostLike.class);
        Root<PostLike> root = delete.from(PostLike.class);

        Predicate condition = cb.and(
                cb.equal(root.get("postId"), UUID.fromString(postId)),
                cb.equal(root.get("user").get("id"), UUID.fromString(userId))
        );
        delete.where(condition);

        entityManager.createQuery(delete).executeUpdate();
    }

    @Override
    public long getLikeCount(String postId) {
        return 0;
    }

    @Override
    public List<PostLikeDocument> getLikeByPostId(String postId) {
        return List.of();
    }
}
