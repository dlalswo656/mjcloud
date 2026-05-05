package com.mjcloud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class CommentDto {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        private String content;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String content;
        private UserDto.Simple user;
        private LocalDateTime createdAt;
    }
}
