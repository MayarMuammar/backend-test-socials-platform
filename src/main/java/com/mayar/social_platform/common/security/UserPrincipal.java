package com.mayar.social_platform.common.security;


import com.mayar.social_platform.modules.user.entity.UserDocument;
import com.mayar.social_platform.modules.user.entity.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class UserPrincipal implements UserDetails {

    private final String id;
    private final String username;
    private final String password;
    private final String email;
    private final UserRole role;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(UserDocument user)
    {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPasswordHash();
        this.role = user.getRole();

        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    public static UserPrincipal create(UserDocument user) {
        return new UserPrincipal(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
