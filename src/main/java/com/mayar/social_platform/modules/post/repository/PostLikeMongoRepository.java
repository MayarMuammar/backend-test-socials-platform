package com.mayar.social_platform.modules.post.repository;


import com.mayar.social_platform.modules.post.entity.PostLikeDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Profile("mongodb")
public class PostLikeMongoRepository implements IPostLikeRepository{

    private final MongoTemplate mongoTemplate;

    public PostLikeMongoRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean existsByPostIdAndUserId(String postId, String userId) {
        Query query = new Query(Criteria.where("post_id").is(postId).and("user_id").is(userId));
        return mongoTemplate.exists(query, PostLikeDocument.class);
    }

    @Override
    public void saveLike(String postId, String userId) {
        PostLikeDocument doc = PostLikeDocument.builder()
                .postId(postId)
                .userId(userId)
                .build();
        mongoTemplate.save(doc);
    }

    @Override
    public void removeLike(String postId, String userId) {
        Query query = new Query(Criteria.where("post_id").is(postId).and("user_id").is(userId));
        mongoTemplate.remove(query, PostLikeDocument.class);
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
