package com.mayar.social_platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException forResource(String resourceType, String id) {
        return new NotFoundException(String.format("Resource %s not found with id: %s", resourceType, id));
    }

    public static NotFoundException forResource(String resourceType) {
        return new NotFoundException(String.format("Resource %s not found", resourceType));
    }
}
