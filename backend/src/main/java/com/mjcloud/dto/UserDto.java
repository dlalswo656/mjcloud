package com.mjcloud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserDto {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Simple {
        private Long id;
        private String username;
        private String displayName;
        private String profileImage;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Profile {
        private Long id;
        private String username;
        private String displayName;
        private String bio;
        private String profileImage;
        private long followerCount;
        private long followingCount;
        private long trackCount;
        private boolean followedByMe;
    }
}
