package com.mayar.social_platform.modules.user.repository;


import com.mayar.social_platform.modules.user.entity.UserDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Profile("mongodb")
public class UserMongoRepository implements IUserRepository{

    private final MongoTemplate mongoTemplate;
    private final String collectionName = "user";

    public UserMongoRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public UserDocument createUser(UserDocument userDocument) {
        userDocument.setCreatedAt(LocalDateTime.now());
        userDocument.setIsDeleted(false);

        return mongoTemplate.save(userDocument);
    }

    @Override
    public Optional<UserDocument> findUserById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        UserDocument userDocument = mongoTemplate.findOne(query, UserDocument.class);
        return Optional.ofNullable(userDocument);
    }

    @Override
    public Optional<UserDocument> findUserByUsernameOrEmail(String usernameOrEmail) {
        Query query = new Query(new Criteria().orOperator(
                Criteria.where("username").is(usernameOrEmail),
                Criteria.where("email").is(usernameOrEmail)));
        UserDocument userDocument = mongoTemplate.findOne(query, UserDocument.class);
        return Optional.ofNullable(userDocument);
    }
}
