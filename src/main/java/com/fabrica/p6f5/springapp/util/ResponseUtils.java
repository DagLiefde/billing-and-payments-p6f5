package com.fabrica.p6f5.springapp.util;

import com.fabrica.p6f5.springapp.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Utility class for creating standardized API responses.
 * Reduces code duplication in controllers.
 */
public final class ResponseUtils {
    
    private ResponseUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Create success response with data.
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>(true, message, data);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Create success response with created status.
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>(true, message, data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Create error response.
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus status) {
        ApiResponse<T> response = new ApiResponse<>(false, message, null);
        return ResponseEntity.status(status).body(response);
    }
}

