package com.it342g02.edubridge.edubridge.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.it342g02.edubridge.edubridge.entity.Role;
import com.it342g02.edubridge.edubridge.entity.User;
import com.it342g02.edubridge.edubridge.repository.UserRepository;

@Service
public class UserService {
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // Create
    // The JSON field "password" is mapped to passwordHash; the raw value is hashed here before saving.
    public User createUser(User user) {
        String rawPassword = user.getPasswordHash();
        requireValidPassword(rawPassword);

        user.setEmail(normalizeEmail(user.getEmail()));
        if (user.getEmail() != null && repository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered.");
        }

        user.setId(null);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        return repository.save(user);
    }

    // Read
    public User getUserById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<User> getAllUsers(Role role) {
        if (role != null) {
            return repository.findByRole(role);
        }
        return repository.findAll();
    }

    // Update
    public User updateUser(Long id, User user) {
        return repository.findById(id).map(existing -> {
            user.setEmail(normalizeEmail(user.getEmail()));
            if (user.getEmail() != null) {
                repository.findByEmail(user.getEmail())
                        .filter(other -> !other.getId().equals(id))
                        .ifPresent(other -> {
                            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered.");
                        });
            }

            user.setId(id);
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(existing.getCreatedAt());
            }

            String rawPassword = user.getPasswordHash();
            if (rawPassword == null || rawPassword.isBlank()) {
                user.setPasswordHash(existing.getPasswordHash());
            } else {
                requireValidPassword(rawPassword);
                user.setPasswordHash(passwordEncoder.encode(rawPassword));
            }
            return repository.save(user);
        }).orElse(null);
    }

    // Delete
    public boolean deleteUser(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    // Authentication
    public User authenticate(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail == null || password == null) {
            return null;
        }
        return repository.findByEmail(normalizedEmail)
                .filter(user -> passwordEncoder.matches(password, user.getPasswordHash()))
                .orElse(null);
    }

    private void requireValidPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password must be at least " + MIN_PASSWORD_LENGTH + " characters.");
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
