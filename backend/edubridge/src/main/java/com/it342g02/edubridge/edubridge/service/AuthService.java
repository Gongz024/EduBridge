package com.it342g02.edubridge.edubridge.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.it342g02.edubridge.edubridge.dto.AuthResponse;
import com.it342g02.edubridge.edubridge.dto.LoginRequest;
import com.it342g02.edubridge.edubridge.dto.SignupRequest;
import com.it342g02.edubridge.edubridge.entity.User;
import com.it342g02.edubridge.edubridge.security.JwtService;

@Service
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;

    public AuthService(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public AuthResponse signup(SignupRequest request) {
        requireText(request.firstName(), "First name");
        requireText(request.lastName(), "Last name");
        requireText(request.email(), "Email");
        if (request.role() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role is required.");
        }

        User user = new User();
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(request.email());
        user.setPasswordHash(request.password());
        user.setRole(request.role());
        user.setPhoneNumber(request.phoneNumber());
        user.setPreferredLanguage(request.preferredLanguage());

        User saved = userService.createUser(user);
        return new AuthResponse(jwtService.generateToken(saved), saved);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userService.authenticate(request.email(), request.password());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
        }
        return new AuthResponse(jwtService.generateToken(user), user);
    }

    private void requireText(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, label + " is required.");
        }
    }
}
