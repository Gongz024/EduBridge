package com.it342g02.edubridge.edubridge.dto;

import com.it342g02.edubridge.edubridge.entity.User;

public record AuthResponse(String token, User user) {
}
