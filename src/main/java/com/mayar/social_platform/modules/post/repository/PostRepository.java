package com.mayar.social_platform.modules.post.repository;

import com.mayar.social_platform.common.dto.PageList;
import com.mayar.social_platform.common.dto.PageQuery;
import com.mayar.social_platform.common.query.PostgresQueryBuilder;
import com.mayar.social_platform.exception.BadRequestException;
import com.mayar.social_platform.exception.NotFoundException;
import com.mayar.social_platform.modules.post.entity.Post;
import com.mayar.social_platform.modules.post.entity.PostDocument;
import com.mayar.social_platform.modules.post.entity.PostStatus;
import com.mayar.social_platform.modules.user.entity.User;
import com.mayar.social_platform.modules.user.entity.UserDocument;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("postgres")
public class PostRepository implements IPostRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${pagination.default-limit:20}")
    private Integer defaultLimit;

    private PostgresQueryBuilder<Post> queryBuilder;

    private PostgresQueryBuilder<Post> getQueryBuilder() {
        if (queryBuilder == null) {
            queryBuilder = new PostgresQueryBuilder<>(
                    entityManager,
                    Post.class,
                    List.of("content")  // Text search fields
            );
        }
        return queryBuilder;
    }

//    @Transactional
    public PostDocument create(PostDocument document, String userId) {
        Post post = new Post();
        post.setContent(document.getContent());
        post.setMediaUrls(document.getMediaUrls());
        post.setCreatedAt(LocalDateTime.now());
        post.setStatus(PostStatus.UNDER_APPROVAL);
        post.setLikesCount(0);
        post.setCommentsCount(0);
        post.setIsDeleted(false);

        User author = entityManager.getReference(User.class, UUID.fromString(userId));
        if(author == null) {
            throw new NotFoundException("Author Not Found");
        }
        post.setAuthor(author);
        entityManager.persist(post);
        return convertToDocument(post);
    }

    public Optional<PostDocument> findById(String id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = cb.createQuery(Post.class);
        Root<Post> root = query.from(Post.class);

        // JOIN FETCH to populate user
        root.fetch("author", JoinType.LEFT);

        query.select(root);
        query.where(
                cb.equal(root.get("id"), UUID.fromString(id)),
                cb.equal(root.get("isDeleted"), false)
        );

        try {
            Post post = entityManager.createQuery(query).getSingleResult();
            return Optional.of(convertToDocument(post));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

//    @Transactional
    public Optional<PostDocument> approve(String id, String adminUsername) {
        Optional<PostDocument> postOpt = findById(id);
        if (postOpt.isPresent()) {
            PostDocument document = postOpt.get();
            // Only Approve pending posts
            if(!document.getStatus().equals(PostStatus.UNDER_APPROVAL)) {
                throw new BadRequestException("Post is not under approval");
            }

            Post post = entityManager.find(Post.class, UUID.fromString(id));
            post.setStatus(PostStatus.APPROVED);
            post.setReviewedBy(adminUsername);
            post.setReviewedAt(LocalDateTime.now());
            post.setRejectionReason(null);
            entityManager.merge(post);
            return Optional.of(convertToDocument(post));
        }
        return Optional.empty();
    }

//    @Transactional
    public Optional<PostDocument> reject(String id, String rejectionReason, String adminUsername) {
        Optional<PostDocument> postOpt = findById(id);
        if (postOpt.isPresent()) {
            PostDocument document = postOpt.get();
            // Only Reject pending posts
            if(!document.getStatus().equals(PostStatus.UNDER_APPROVAL)) {
                throw new BadRequestException("Post is not under approval");
            }

            Post post = entityManager.find(Post.class, UUID.fromString(id));
            post.setStatus(PostStatus.REJECTED);
            post.setRejectionReason(rejectionReason);
            post.setReviewedBy(adminUsername);
            post.setReviewedAt(LocalDateTime.now());
            entityManager.merge(post);
            return Optional.of(convertToDocument(post));
        }
        return Optional.empty();
    }

    public PageList<PostDocument> getPosts(PageQuery pageQuery) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> query = getQueryBuilder().buildQuery(pageQuery);

        Root<Post> root = (Root<Post>) query.getRoots().iterator().next();

        root.fetch("author", JoinType.LEFT);
        query.select(root);
        Integer limit = pageQuery.getLimit() != null ? pageQuery.getLimit() : defaultLimit;
        int page = pageQuery.getPage() != null && pageQuery.getPage() > 0 ? pageQuery.getPage() : 1;
        int skip = (page - 1) * limit;

        TypedQuery<Post> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(skip);
        typedQuery.setMaxResults(limit);

        List<Post> posts = typedQuery.getResultList();

        long totalCount = count(pageQuery);
        List<PostDocument> documents = posts.stream()
                .map(this::convertToDocument)
                .toList();
        return PageList.of(documents, totalCount, page, limit);
    }

    @Override
    public Optional<PostDocument> incrementPostLike(String postId) {
        Optional<PostDocument> postOpt = findById(postId);

        if (postOpt.isPresent()) {
            PostDocument document = postOpt.get();

            if(document.getStatus().equals(PostStatus.UNDER_APPROVAL)) {
                throw new BadRequestException("Post is not approved yet.");
            }

            Post post = entityManager.find(Post.class, UUID.fromString(postId));
            post.setLikesCount(post.getLikesCount() + 1);
            post.setUpdatedAt(LocalDateTime.now());
            entityManager.merge(post);
            return Optional.of(convertToDocument(post));
        }

        return Optional.empty();
    }

    @Override
    public Optional<PostDocument> decrementPostLike(String postId) {
        Optional<PostDocument> postOpt = findById(postId);

        if (postOpt.isPresent()) {
            PostDocument document = postOpt.get();

            if(document.getStatus().equals(PostStatus.UNDER_APPROVAL)) {
                throw new BadRequestException("Post is not approved yet.");
            }

            Post post = entityManager.find(Post.class, UUID.fromString(postId));
            post.setLikesCount(post.getLikesCount() - 1);
            post.setUpdatedAt(LocalDateTime.now());
            entityManager.merge(post);
            return Optional.of(convertToDocument(post));
        }

        return Optional.empty();
    }

    @Override
    public Optional<PostDocument> incrementPostComment(String postId) {
        Optional<PostDocument> postOpt = findById(postId);

        if (postOpt.isPresent()) {
            PostDocument document = postOpt.get();

            if(document.getStatus().equals(PostStatus.UNDER_APPROVAL)) {
                throw new BadRequestException("Post is not approved yet.");
            }

            Post post = entityManager.find(Post.class, UUID.fromString(postId));
            post.setCommentsCount(post.getCommentsCount() + 1);
            post.setUpdatedAt(LocalDateTime.now());
            entityManager.merge(post);
            return Optional.of(convertToDocument(post));
        }

        return Optional.empty();
    }

    @Override
    public Optional<PostDocument> decrementPostComment(String postId) {
        Optional<PostDocument> postOpt = findById(postId);

        if (postOpt.isPresent()) {
            PostDocument document = postOpt.get();

            if(document.getStatus().equals(PostStatus.UNDER_APPROVAL)) {
                throw new BadRequestException("Post is not approved yet.");
            }

            Post post = entityManager.find(Post.class, UUID.fromString(postId));
            post.setCommentsCount(post.getCommentsCount() - 1);
            post.setUpdatedAt(LocalDateTime.now());
            entityManager.merge(post);
            return Optional.of(convertToDocument(post));
        }

        return Optional.empty();
    }

    public long count(PageQuery pageQuery) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Post> root = countQuery.from(Post.class);

        countQuery.select(cb.count(root));

        // Apply same predicates as main query
        List<Predicate> predicates = getQueryBuilder().buildPredicates(cb, root, pageQuery);
        if (!predicates.isEmpty()) {
            countQuery.where(predicates.toArray(new Predicate[0]));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private PostDocument convertToDocument(Post post) {
        PostDocument doc = PostDocument.builder()
                .content(post.getContent())
                .authorId(post.getAuthor() != null ? post.getAuthor().getId().toString() :
                        null
                )
                .mediaUrls(post.getMediaUrls())
                .status(post.getStatus())
                .rejectionReason(post.getRejectionReason())
                .reviewedAt(post.getReviewedAt())
                .reviewedBy(post.getReviewedBy())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .build();
        doc.setId(post.getId().toString());
        doc.setCreatedAt(post.getCreatedAt());
        doc.setUpdatedAt(post.getUpdatedAt());

        if(post.getAuthor() != null) {
            UserDocument authorDoc = UserDocument.builder()
                    .username(post.getAuthor().getUsername())
                    .email(post.getAuthor().getEmail())
                    .fullName(post.getAuthor().getFullName())
                    .role(post.getAuthor().getRole())
                    .build();
            authorDoc.setId(post.getAuthor().getId().toString());
            doc.setAuthor(authorDoc);
        }

        return doc;
    }

}
