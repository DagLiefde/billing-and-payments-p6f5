package com.fabrica.p6f5.springapp.controller;

import com.fabrica.p6f5.springapp.dto.ApiResponse;
import com.fabrica.p6f5.springapp.entity.User;
import com.fabrica.p6f5.springapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * User Controller following Open/Closed Principle.
 * This controller is open for extension but closed for modification.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Get all users (Admin only).
     * 
     * @return ResponseEntity with list of users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve users: " + e.getMessage()));
        }
    }
    
    /**
     * Get user by ID.
     * 
     * @param id the user ID
     * @return ResponseEntity with user details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        try {
            Optional<User> user = userService.findById(id);
            if (user.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve user: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/preferences")
    public ResponseEntity<ApiResponse<Object>> getPreferences(@PathVariable Long id) {
        try {
            return userService.getPreferences(id)
                    .<ResponseEntity<ApiResponse<Object>>>map(p -> ResponseEntity.ok(ApiResponse.success("Preferences retrieved", p)))
                    .orElse(ResponseEntity.ok(ApiResponse.success("Preferences not set", null)));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to retrieve preferences: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/preferences")
    public ResponseEntity<ApiResponse<Object>> updatePreferences(@PathVariable Long id,
                                                                 @RequestBody java.util.Map<String, String> body) {
        try {
            var pref = userService.upsertPreferences(id, body.get("font_size"), body.get("contrast_mode"));
            return ResponseEntity.ok(ApiResponse.success("Preferences updated", pref));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update preferences: " + e.getMessage()));
        }
    }
    
    /**
     * Update user (Admin only or own profile).
     * 
     * @param id the user ID
     * @param user the updated user data
     * @return ResponseEntity with updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.findById(#id).get().username == authentication.name")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            Optional<User> existingUser = userService.findById(id);
            if (existingUser.isPresent()) {
                User updatedUser = userService.save(user);
                return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to update user: " + e.getMessage()));
        }
    }
    
    /**
     * Delete user (Admin only).
     * 
     * @param id the user ID
     * @return ResponseEntity with deletion result
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        try {
            Optional<User> user = userService.findById(id);
            if (user.isPresent()) {
                userService.deleteById(id);
                return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.error("Failed to delete user: " + e.getMessage()));
        }
    }
}
