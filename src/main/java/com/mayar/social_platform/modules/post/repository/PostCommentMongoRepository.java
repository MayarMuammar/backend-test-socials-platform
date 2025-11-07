package com.mayar.social_platform.modules.post.repository;

import com.mayar.social_platform.modules.post.entity.PostCommentDocument;
import com.mayar.social_platform.modules.post.entity.PostLikeDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongodb")
public class PostCommentMongoRepository implements IPostCommentRepository {

    private final MongoTemplate mongoTemplate;
    public PostCommentMongoRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public PostCommentDocument createComment(String postId, String userId, String content) {
        PostCommentDocument doc = PostCommentDocument.builder()
                .postId(postId)
                .userId(userId)
                .content(content)
                .build();
        mongoTemplate.save(doc);
        return null;
    }

    @Override
    public Optional<PostCommentDocument> getByIdAndUserId(String commentId, String userId) {
        Query query = new Query(Criteria.where("_id").is(commentId).and("user_id").is(userId));
        PostCommentDocument doc = mongoTemplate.findOne(query, PostCommentDocument.class);

        if (doc == null) {
            return Optional.empty();
        }
        return Optional.of(doc);

    }

    @Override
    public void deleteComment(String commentId, String userId) {
        Query query = new Query(Criteria.where("_id").is(commentId).and("user_id").is(userId));
        mongoTemplate.remove(query, PostCommentDocument.class);
    }

    @Override
    public long countByPostId(String postId) {
        return 0;
    }

    @Override
    public List<PostCommentDocument> getCommentsByPost(String postId) {
        return List.of();
    }
}
