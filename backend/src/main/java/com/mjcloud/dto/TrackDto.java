package com.mjcloud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

public class TrackDto {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private String audioUrl;
        private String coverImage;
        private String genre;
        private Integer duration;
        private Integer playCount;
        private Long likeCount;
        private Long commentCount;
        private boolean likedByMe;
        private UserDto.Simple uploader;
        private LocalDateTime createdAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class UploadRequest {
        private String title;
        private String description;
        private String genre;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class UpdateRequest {
        private String title;
        private String description;
        private String genre;
    }
}
