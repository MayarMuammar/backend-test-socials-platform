package com.mayar.social_platform.common.initialization;

import com.mayar.social_platform.modules.user.dto.RegisterRequest;
import com.mayar.social_platform.modules.user.repository.IUserRepository;
import com.mayar.social_platform.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final IUserRepository userRepository;
    private final UserService userService;
    private final String adminUsername;
    private final String adminPassword;
    private final String adminEmail;
    private final String adminFullName;

    public AdminInitializer(
            IUserRepository userRepository,
            UserService userService,
            @Value("${admin.username}") String adminUsername,
            @Value("${admin.password}") String adminPassword,
            @Value("${admin.email}") String adminEmail,
            @Value("${admin.full-name}") String adminFullName
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.adminEmail = adminEmail;
        this.adminFullName = adminFullName;
    }

    @Override
    public void run(String... args) throws Exception {
        createDefaultAdmin();
    }

    private void createDefaultAdmin() {
        if(userRepository.findUserByUsernameOrEmail(adminUsername).isPresent()) return;
        if(userRepository.findUserByUsernameOrEmail(adminEmail).isPresent()) return;

        try {
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(adminPassword)
                    .fullName(adminFullName)
                    .build();
            userService.registerAdmin(registerRequest, "System");
        } catch(Exception e) {
            return;
        }
    }

}
