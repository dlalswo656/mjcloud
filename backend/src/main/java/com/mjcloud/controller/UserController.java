package com.mjcloud.controller;

import com.mjcloud.dto.UserDto;
import com.mjcloud.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 내 프로필
    @GetMapping("/me")
    public ResponseEntity<UserDto.Profile> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getMyProfile(userDetails.getUsername()));
    }

    // 특정 유저 프로필
    @GetMapping("/{id}")
    public ResponseEntity<UserDto.Profile> getProfile(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(userService.getProfile(id, username));
    }

    // 팔로우 토글
    @PostMapping("/{id}/follow")
    public ResponseEntity<Map<String, Boolean>> toggleFollow(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean following = userService.toggleFollow(id, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("following", following));
    }

    // 프로필 수정
    @PutMapping("/me")
    public ResponseEntity<UserDto.Profile> updateProfile(
            @RequestBody UpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.updateProfile(userDetails.getUsername(), request.getDisplayName(), request.getBio()));
    }

    @Data
    static class UpdateRequest {
        private String displayName;
        private String bio;
    }
}
