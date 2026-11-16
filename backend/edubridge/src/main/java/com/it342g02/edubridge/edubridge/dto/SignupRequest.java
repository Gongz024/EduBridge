package com.it342g02.edubridge.edubridge.dto;

import com.it342g02.edubridge.edubridge.entity.Role;

public record SignupRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        Role role,
        String phoneNumber,
        String preferredLanguage) {
}
