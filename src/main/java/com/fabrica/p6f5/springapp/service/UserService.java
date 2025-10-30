package com.fabrica.p6f5.springapp.service;

import com.fabrica.p6f5.springapp.entity.User;
import com.fabrica.p6f5.springapp.entity.UserPreference;
import com.fabrica.p6f5.springapp.repository.UserPreferenceRepository;
import com.fabrica.p6f5.springapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * User Service following Single Responsibility Principle.
 * This service is responsible only for user-related operations.
 */
@Service
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired private UserPreferenceRepository preferenceRepository;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Load user by username for Spring Security.
     * 
     * @param username the username
     * @return UserDetails
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
    
    /**
     * Find user by username or email.
     * 
     * @param usernameOrEmail the username or email
     * @return Optional containing the user if found
     */
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }
    
    /**
     * Save a new user.
     * 
     * @param user the user to save
     * @return the saved user
     */
    public User save(User user) {
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    /**
     * Check if username exists.
     * 
     * @param username the username to check
     * @return true if exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Check if email exists.
     * 
     * @param email the email to check
     * @return true if exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Find user by ID.
     * 
     * @param id the user ID
     * @return Optional containing the user if found
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Find all users.
     * 
     * @return List of all users
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    /**
     * Delete user by ID.
     * 
     * @param id the user ID
     */
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<UserPreference> getPreferences(Long userId) {
        return userRepository.findById(userId).flatMap(preferenceRepository::findByUser);
    }

    public UserPreference upsertPreferences(Long userId, String fontSize, String contrastMode) {
        User user = userRepository.findById(userId).orElseThrow();
        UserPreference pref = preferenceRepository.findByUser(user).orElseGet(() -> {
            UserPreference p = new UserPreference();
            p.setUser(user);
            return p;
        });
        pref.setFontSize(fontSize);
        pref.setContrastMode(contrastMode);
        return preferenceRepository.save(pref);
    }
}
