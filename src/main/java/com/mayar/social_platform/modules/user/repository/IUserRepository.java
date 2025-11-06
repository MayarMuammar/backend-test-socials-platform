package com.mayar.social_platform.modules.user.repository;

import com.mayar.social_platform.modules.user.entity.UserDocument;

import java.util.Optional;

public interface IUserRepository {

    UserDocument createUser(UserDocument userDocument);

    Optional<UserDocument> findUserById(String id);

    Optional<UserDocument> findUserByUsernameOrEmail(String usernameOrEmail);
}
