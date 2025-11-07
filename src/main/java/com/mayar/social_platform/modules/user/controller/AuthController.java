package com.mayar.social_platform.modules.user.controller;

import com.mayar.social_platform.common.annotation.CurrentUser;
import com.mayar.social_platform.common.security.UserPrincipal;
import com.mayar.social_platform.modules.user.dto.AuthResponse;
import com.mayar.social_platform.modules.user.dto.LoginRequest;
import com.mayar.social_platform.modules.user.dto.RegisterRequest;
import com.mayar.social_platform.modules.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = userService.login(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        AuthResponse authResponse = userService.registerUser(registerRequest);
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> me(@CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(new Object() {
            public final String id = currentUser.getId();
            public final String username = currentUser.getUsername();
            public final String email = currentUser.getEmail();
            public final String role = currentUser.getRole().name();
        });
    }
}
