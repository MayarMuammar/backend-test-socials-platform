package com.mayar.social_platform.modules.user.repository;

import com.mayar.social_platform.modules.user.entity.User;
import com.mayar.social_platform.modules.user.entity.UserDocument;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("postgres")
public class UserRepository implements IUserRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserDocument createUser(UserDocument userDocument) {
        User user = new User();
        user.setUsername(userDocument.getUsername());
        user.setEmail(userDocument.getEmail());
        user.setPasswordHash(userDocument.getPasswordHash());
        user.setFullName(userDocument.getFullName());
        user.setRole(userDocument.getRole());
        user.setIsDeleted(userDocument.getIsDeleted());

        entityManager.persist(user);

        return convertToDocument(user);
    }

    @Override
    public Optional<UserDocument> findUserById(String id) {
       try {
            User user = entityManager.find(User.class, UUID.fromString(id));

            if(user != null && !user.getIsDeleted()) {
                return Optional.of(convertToDocument(user));
            }
       } catch (IllegalArgumentException e) {
           // Invalid UUID
       }
       return Optional.empty();
    }

    @Override
    public Optional<UserDocument> findUserByUsernameOrEmail(String usernameOrEmail) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        cq.where(
                cb.or(
                        cb.equal(root.get("username"), usernameOrEmail),
                        cb.equal(root.get("email"), usernameOrEmail)
                )
        );

        try {
            User user = entityManager.createQuery(cq).getSingleResult();
            return Optional.of(convertToDocument(user));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private UserDocument convertToDocument(User user) {
        UserDocument doc = UserDocument.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .fullName(user.getPasswordHash())
                .role(user.getRole())
                .build();
        doc.setId(user.getId().toString());
        doc.setCreatedAt(user.getCreatedAt());
        doc.setUpdatedAt(user.getUpdatedAt());
        doc.setIsDeleted(user.getIsDeleted());

        return doc;
    }
}
