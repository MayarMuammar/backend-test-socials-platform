package com.mayar.social_platform.modules.post.repository;


import com.mayar.social_platform.common.dto.PageList;
import com.mayar.social_platform.common.dto.PageQuery;
import com.mayar.social_platform.common.query.MongoQueryBuilder;
import com.mayar.social_platform.exception.BadRequestException;
import com.mayar.social_platform.modules.post.entity.PostDocument;
import com.mayar.social_platform.modules.post.entity.PostStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongodb")
public class PostMongoRepository implements IPostRepository{
    private final MongoTemplate mongoTemplate;

    public PostMongoRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Value("${pagination.default-limit:20}")
    private Integer defaultLimit;

    private final String collectionName = "posts";
    private MongoQueryBuilder queryBuilder;

    private MongoQueryBuilder getQueryBuilder() {
        if (queryBuilder == null) {
            queryBuilder = new MongoQueryBuilder(
                    List.of("content")  // Text search fields
            );
        }
        return queryBuilder;
    }


    public PostDocument create(PostDocument document, String userId) {
        document.setStatus(PostStatus.UNDER_APPROVAL);
        document.setLikesCount(0);
        document.setCommentsCount(0);
        document.setIsDeleted(false);
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());
        document.setAuthorId(userId);

        PostDocument created = mongoTemplate.insert(document, collectionName);

        return findById(created.getId()).orElse(created);
    }

    public Optional<PostDocument> findById(String id) {
        MatchOperation matchId = Aggregation.match(Criteria.where("_id").is(id).and("isDeleted").is(false));

        LookupOperation lookupUser = LookupOperation.newLookup()
                .from("users")
                .localField("authorId")
                .foreignField("_id")
                .as("author");

        UnwindOperation unwindUser = UnwindOperation.newUnwind().path("author").noArrayIndex().preserveNullAndEmptyArrays();

        Aggregation aggregation = Aggregation.newAggregation(
                matchId,
                lookupUser,
                unwindUser
        );

        List<PostDocument>  results = mongoTemplate.aggregate(aggregation,
                collectionName,
                PostDocument.class).getMappedResults();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public Optional<PostDocument> approve(String id, String adminUserName) {
        Optional<PostDocument> postDocument = findById(id);
        if (postDocument.isPresent()) {
            PostDocument post = postDocument.get();

            if(!post.getStatus().equals(PostStatus.UNDER_APPROVAL)) {
                throw new BadRequestException("Post is not under approval");
            }

            Query query = new Query(Criteria.where("_id").is(id));

            Update update = new Update();
            update.set("status", PostStatus.APPROVED);
            update.set("reviewedAt",  LocalDateTime.now());
            update.set("reviewedBy", adminUserName);
            update.unset("rejectionReason");

            mongoTemplate.updateFirst(query, update, collectionName);

            return findById(id);
        }
        return Optional.empty();
    }

    public Optional<PostDocument> reject(String id, String rejectionReason, String adminUserName) {
        Optional<PostDocument> postDocument = findById(id);
        if(postDocument.isPresent()) {
            PostDocument post = postDocument.get();

            if(!post.getStatus().equals(PostStatus.UNDER_APPROVAL)) {
                throw new BadRequestException("Post is not under approval");
            }

            Query query = new Query(Criteria.where("_id").is(id));
            Update update = new Update();
            update.set("status", PostStatus.REJECTED);
            update.set("rejectionReason", rejectionReason);
            update.set("reviewedAt",  LocalDateTime.now());
            update.set("reviewedBy", adminUserName);

            mongoTemplate.updateFirst(query, update, collectionName);
            return findById(id);
        }
        return Optional.empty();
    }

    public PageList<PostDocument> getPosts(PageQuery pageQuery) {
        List<AggregationOperation> operations = new ArrayList<>();

        List<Criteria> criteria = getQueryBuilder().buildCriteria(pageQuery);
        if(!criteria.isEmpty()) {
            Criteria combinedCriteria = new Criteria().andOperator
                    (criteria.toArray(new Criteria[0]));
            operations.add(Aggregation.match(combinedCriteria));
        }

        LookupOperation lookupUser = LookupOperation.newLookup()
                .from("users")
                .localField("authorId")
                .foreignField("_id")
                .as("author");
        operations.add(lookupUser);

        operations.add(Aggregation.unwind("author", true));

        if(pageQuery.getSortBy() != null) {
            Sort.Direction direction = pageQuery.getSortDirection().equalsIgnoreCase("asc")
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;
            operations.add(Aggregation.sort(direction, pageQuery.getSortBy()));
        }

        long totalCount = count(pageQuery);

        Integer limit = pageQuery.getLimit() != null ? pageQuery.getLimit() : defaultLimit;
        int page = pageQuery.getPage() != null && pageQuery.getPage() > 0 ? pageQuery.getPage() : 1;
        int skip = (page -1 ) * limit;

        operations.add(Aggregation.skip(skip));
        operations.add(Aggregation.limit(limit));

        Aggregation aggregation = Aggregation.newAggregation(operations);

        List<PostDocument> posts = mongoTemplate.aggregate(
                aggregation,
                collectionName,
                PostDocument.class
        ).getMappedResults();

        return PageList.of(posts, totalCount, page, limit);
    }

    public long count(PageQuery pageQuery) {
        Query query = getQueryBuilder().buildCountQuery(pageQuery);
        return mongoTemplate.count(query, collectionName);
    }
}
