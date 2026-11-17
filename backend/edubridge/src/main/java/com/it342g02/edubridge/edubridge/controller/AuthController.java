package com.it342g02.edubridge.edubridge.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.it342g02.edubridge.edubridge.dto.AuthResponse;
import com.it342g02.edubridge.edubridge.dto.LoginRequest;
import com.it342g02.edubridge.edubridge.dto.SignupRequest;
import com.it342g02.edubridge.edubridge.entity.User;
import com.it342g02.edubridge.edubridge.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(request));
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // Returns the user that owns the JWT sent in the Authorization header
    @GetMapping("/me")
    public User me(@AuthenticationPrincipal User user) {
        return user;
    }
}
