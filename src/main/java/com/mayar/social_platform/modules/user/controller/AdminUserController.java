package com.mayar.social_platform.modules.user.controller;

import com.mayar.social_platform.common.annotation.CurrentUser;
import com.mayar.social_platform.common.security.UserPrincipal;
import com.mayar.social_platform.modules.user.dto.RegisterRequest;
import com.mayar.social_platform.modules.user.dto.UserResponse;
import com.mayar.social_platform.modules.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> registerAdmin(@RequestBody RegisterRequest registerRequest, @CurrentUser UserPrincipal user) {
        UserResponse userResponse = userService.registerAdmin(registerRequest, user.getUsername());
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }
}
