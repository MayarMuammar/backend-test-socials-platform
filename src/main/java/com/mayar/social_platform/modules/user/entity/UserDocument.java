package com.mayar.social_platform.modules.user.entity;


import com.mayar.social_platform.common.entity.BaseDocument;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDocument extends BaseDocument {
    @Field("username")
    @Indexed(unique = true)
    private String username;

    @Field("email")
    @Indexed(unique = true)
    private String email;

    @Field("password_hash")
    private String passwordHash;

    @Field("full_name")
    private String fullName;

    @Field("role")
    private UserRole role = UserRole.USER;
}
