package com.mayar.social_platform.modules.user.service;

import com.mayar.social_platform.common.security.JwtUtil;
import com.mayar.social_platform.exception.BadRequestException;
import com.mayar.social_platform.exception.NotFoundException;
import com.mayar.social_platform.exception.UnAuthorizedException;
import com.mayar.social_platform.modules.user.dto.AuthResponse;
import com.mayar.social_platform.modules.user.dto.LoginRequest;
import com.mayar.social_platform.modules.user.dto.RegisterRequest;
import com.mayar.social_platform.modules.user.dto.UserResponse;
import com.mayar.social_platform.modules.user.entity.UserDocument;
import com.mayar.social_platform.modules.user.entity.UserRole;
import com.mayar.social_platform.modules.user.mapper.AuthResponseMapper;
import com.mayar.social_platform.modules.user.mapper.UserMapper;
import com.mayar.social_platform.modules.user.repository.IUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(IUserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse registerUser(RegisterRequest request) {

        if(userRepository.findUserByUsernameOrEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        if(userRepository.findUserByUsernameOrEmail(request.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        UserDocument user = UserDocument.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(hashedPassword)
                .fullName(request.getFullName())
                .role(UserRole.ROLE_USER)
                .build();

        UserDocument createdUser = userRepository.createUser(user);

        String token = jwtUtil.generateToken(createdUser.getId(), createdUser.getUsername(), createdUser.getRole());

        return AuthResponseMapper.toResponse(createdUser, token);
    }

    public UserResponse registerAdmin(RegisterRequest request, String adminUserName) {

        if(userRepository.findUserByUsernameOrEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        if(userRepository.findUserByUsernameOrEmail(request.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        UserDocument user = UserDocument.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(hashedPassword)
                .fullName(request.getFullName())
                .role(UserRole.ROLE_ADMIN)
                .build();

        UserDocument createdUser = userRepository.createUser(user);


        return UserMapper.toResponse(createdUser);
    }

    public AuthResponse login(LoginRequest request) {
        UserDocument user = userRepository.findUserByUsernameOrEmail(request.getUsernameOrEmail())
                .orElseThrow(() -> new BadRequestException("Invalid username/email or password"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid username/email or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        return AuthResponseMapper.toResponse(user, token);
    }

    public UserDocument verifyToken(String token) {
        if(!jwtUtil.validateToken(token)) {
            throw new UnAuthorizedException("Unauthorized");
        }

        String userId = jwtUtil.parseToken(token).getId();

        return userRepository.findUserById(userId).orElseThrow(() -> new BadRequestException("Invalid token"));
    }

    public UserResponse getUserById(String id) {
        return userRepository.findUserById(id)
                .map(UserMapper::toResponse)
                .orElseThrow(() -> NotFoundException.forResource("User", id));
    }






}
