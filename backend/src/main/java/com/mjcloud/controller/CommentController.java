package com.mjcloud.controller;

import com.mjcloud.dto.CommentDto;
import com.mjcloud.dto.UserDto;
import com.mjcloud.entity.Comment;
import com.mjcloud.entity.Track;
import com.mjcloud.entity.User;
import com.mjcloud.repository.CommentRepository;
import com.mjcloud.repository.TrackRepository;
import com.mjcloud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;

    // 트랙 댓글 목록 조회
    @GetMapping("/track/{trackId}")
    public ResponseEntity<List<CommentDto.Response>> getComments(@PathVariable Long trackId) {
        List<CommentDto.Response> comments = commentRepository
                .findByTrackIdOrderByCreatedAtDesc(trackId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(comments);
    }

    // 댓글 작성
    @PostMapping("/track/{trackId}")
    public ResponseEntity<CommentDto.Response> addComment(
            @PathVariable Long trackId,
            @RequestBody CommentDto.Request request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("트랙 없음"));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .track(track)
                .build();

        commentRepository.save(comment);
        return ResponseEntity.ok(toResponse(comment));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));
        if (!comment.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new RuntimeException("권한 없음");
        }
        commentRepository.delete(comment);
        return ResponseEntity.noContent().build();
    }

    // Entity → DTO
    private CommentDto.Response toResponse(Comment comment) {
        return CommentDto.Response.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .user(UserDto.Simple.builder()
                        .id(comment.getUser().getId())
                        .username(comment.getUser().getUsername())
                        .displayName(comment.getUser().getDisplayName())
                        .profileImage(comment.getUser().getProfileImage())
                        .build())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
