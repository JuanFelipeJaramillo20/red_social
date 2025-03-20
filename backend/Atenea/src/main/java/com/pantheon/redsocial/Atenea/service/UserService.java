package com.pantheon.redsocial.Atenea.service;

import com.pantheon.redsocial.Atenea.model.User;
import com.pantheon.redsocial.Atenea.payload.UserRegisterRequest;
import com.pantheon.redsocial.Atenea.repository.UserRepository;
import com.pantheon.redsocial.Atenea.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(UserRegisterRequest userRegisterRequest) {
        if (userRepository.existsByUsername(userRegisterRequest.getUsername())) {
            logger.warn("Registration attempt failed: Username '{}' is already taken", userRegisterRequest.getUsername());
            throw new IllegalArgumentException("The username is already taken");
        }

        User user = new User();
        user.setUsername(userRegisterRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        user.setEmail(userRegisterRequest.getEmail());
        user.setFullName(userRegisterRequest.getFullName());
        user.setAvatarUrl(userRegisterRequest.getAvatarUrl());

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", user.getUsername());

        return savedUser;
    }

    @Transactional(readOnly = true)
    public User getUserProfile(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User profile request failed: User '{}' not found", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
    }

    @Transactional
    public User updateUserProfile(String username, User updatedUser) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User profile update failed: User '{}' not found", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        user.setFullName(updatedUser.getFullName());
        user.setAvatarUrl(updatedUser.getAvatarUrl());
        //user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        //user.setEmail(updatedUser.getEmail());
        //user.setUsername(updatedUser.getUsername());
        user.setUpdatedAt(LocalDateTime.now());

        User updated = userRepository.save(user);
        logger.info("User profile updated successfully: {}", user.getUsername());

        return updated;
    }

    @Transactional(readOnly = true)
    public Page<User> searchUsers(String keyword, int page, int size) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                logger.warn("User search attempt failed: Empty search keyword");
                throw new IllegalArgumentException("Search keyword cannot be empty");
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<User> users = userRepository.searchUsers(keyword, pageable);

            if (users.isEmpty()) {
                logger.info("User search: No users found for keyword '{}'", keyword);
                return Page.empty(pageable);
            } else {
                logger.info("User search successful: Found {} users for keyword '{}'", users.getTotalElements(), keyword);
            }

            return users;
        } catch (Exception e) {
            logger.error("Error occurred during user search: {}", e.getMessage(), e);
            throw new RuntimeException("An error occurred while searching for users. Please try again.");
        }
    }

    /**
     * Generates a password reset token for the user.
     *
     * @param email The email of the user requesting a password reset.
     * @return The reset token.
     */
    public String generatePasswordResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            logger.warn("Password reset request failed: No account found with email '{}'", email);
            throw new IllegalArgumentException("No account found with this email.");
        }

        User user = userOpt.get();
        String resetToken = TokenUtil.generateToken();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(30); // Token valid for 30 min

        user.setResetToken(resetToken);
        user.setResetTokenExpiry(expiryTime);
        userRepository.save(user);

        logger.info("Password reset token generated for user '{}'. Token expires at {}", user.getUsername(), expiryTime);
        return resetToken;
    }

    /**
     * Resets the password using a valid token.
     *
     * @param resetToken The token provided by the user.
     * @param newPassword The new password to set.
     */
    public void resetPassword(String resetToken, String newPassword) {
        Optional<User> userOpt = userRepository.findByResetToken(resetToken);
        if (userOpt.isEmpty()) {
            logger.warn("Password reset attempt failed: Invalid or expired reset token '{}'", resetToken);
            throw new IllegalArgumentException("Invalid or expired reset token.");
        }

        User user = userOpt.get();
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            logger.warn("Password reset attempt failed: Token for user '{}' has expired", user.getUsername());
            throw new IllegalArgumentException("Reset token has expired.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        logger.info("Password successfully reset for user '{}'", user.getUsername());
    }
}
