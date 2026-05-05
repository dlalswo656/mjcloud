package com.mjcloud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDto {

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class SignupRequest {
        private String username;
        private String password;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class TokenResponse {
        private String token;
        private String username;
        private Long userId;
    }
}
