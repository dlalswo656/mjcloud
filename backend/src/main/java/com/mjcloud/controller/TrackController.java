package com.mjcloud.controller;

import com.mjcloud.dto.TrackDto;
import com.mjcloud.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracks")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    // 트랙 업로드
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<TrackDto.Response> upload(
            @AuthenticationPrincipal UserDetails user,
            @RequestPart("audio") MultipartFile audio,
            @RequestPart(value = "cover", required = false) MultipartFile cover,
            @RequestPart("title") String title,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart(value = "genre", required = false) String genre) throws Exception {
        TrackDto.UploadRequest req = new TrackDto.UploadRequest(title, description, genre);
        return ResponseEntity.ok(trackService.uploadTrack(user.getUsername(), audio, cover, req));
    }

    // 피드 (전체/검색/장르 필터)
    @GetMapping("/feed")
    public ResponseEntity<List<TrackDto.Response>> getFeed(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String keyword) {
        String username = user != null ? user.getUsername() : null;
        return ResponseEntity.ok(trackService.getFeed(username, genre, keyword));
    }

    // 인기 트랙
    @GetMapping("/popular")
    public ResponseEntity<List<TrackDto.Response>> getPopular(
            @AuthenticationPrincipal UserDetails user) {
        String username = user != null ? user.getUsername() : null;
        return ResponseEntity.ok(trackService.getPopularTracks(username));
    }

    // 단일 트랙 조회
    @GetMapping("/{id}")
    public ResponseEntity<TrackDto.Response> getTrack(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        String username = user != null ? user.getUsername() : null;
        return ResponseEntity.ok(trackService.getTrack(id, username));
    }

    // 유저 트랙 목록
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TrackDto.Response>> getUserTracks(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails user) {
        String username = user != null ? user.getUsername() : null;
        return ResponseEntity.ok(trackService.getUserTracks(userId, username));
    }

    // 좋아요 토글
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Boolean>> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        boolean liked = trackService.toggleLike(id, user.getUsername());
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    // 트랙 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        trackService.deleteTrack(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
