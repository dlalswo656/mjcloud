package com.mjcloud.service;

import com.mjcloud.dto.TrackDto;
import com.mjcloud.dto.UserDto;
import com.mjcloud.entity.*;
import com.mjcloud.entity.QTrack;
import com.mjcloud.repository.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackService {
    //
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final JPAQueryFactory queryFactory;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // 트랙 업로드
    public TrackDto.Response uploadTrack(String username, MultipartFile audioFile,
                                          MultipartFile coverFile, TrackDto.UploadRequest request) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // 오디오 파일 저장
        String audioUrl = saveFile(audioFile, "audio");
        String coverUrl = coverFile != null && !coverFile.isEmpty() ? saveFile(coverFile, "cover") : null;

        Track track = Track.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .genre(request.getGenre())
                .audioUrl(audioUrl)
                .coverImage(coverUrl)
                .user(user)
                .build();

        trackRepository.save(track);
        return toResponse(track, username);
    }

    // 피드 (전체 최신순) - QueryDSL 사용
    public List<TrackDto.Response> getFeed(String username, String genre, String keyword) {
        QTrack track = QTrack.track;
        BooleanBuilder builder = new BooleanBuilder();

        if (genre != null && !genre.isEmpty()) {
            builder.and(track.genre.eq(genre));
        }
        if (keyword != null && !keyword.isEmpty()) {
            builder.and(track.title.containsIgnoreCase(keyword)
                    .or(track.user.username.containsIgnoreCase(keyword)));
        }

        List<Track> tracks = queryFactory
                .selectFrom(track)
                .where(builder)
                .orderBy(track.createdAt.desc())
                .limit(50)
                .fetch();

        return tracks.stream().map(t -> toResponse(t, username)).collect(Collectors.toList());
    }

    // 인기 트랙 (재생수 순)
    public List<TrackDto.Response> getPopularTracks(String username) {
        QTrack track = QTrack.track;
        List<Track> tracks = queryFactory
                .selectFrom(track)
                .orderBy(track.playCount.desc())
                .limit(20)
                .fetch();
        return tracks.stream().map(t -> toResponse(t, username)).collect(Collectors.toList());
    }

    // 단일 트랙 조회 + 재생수 증가
    public TrackDto.Response getTrack(Long trackId, String username) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("트랙 없음"));
        track.setPlayCount(track.getPlayCount() + 1);
        trackRepository.save(track);
        return toResponse(track, username);
    }

    // 특정 유저 트랙 목록
    public List<TrackDto.Response> getUserTracks(Long userId, String username) {
        return trackRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(t -> toResponse(t, username)).collect(Collectors.toList());
    }

    // 좋아요 토글
    public boolean toggleLike(Long trackId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("트랙 없음"));

        if (likeRepository.existsByUserIdAndTrackId(user.getId(), trackId)) {
            likeRepository.findByUserIdAndTrackId(user.getId(), trackId)
                    .ifPresent(likeRepository::delete);
            return false;
        } else {
            likeRepository.save(Like.builder().user(user).track(track).build());
            return true;
        }
    }

    // 트랙 삭제
    public void deleteTrack(Long trackId, String username) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("트랙 없음"));
        if (!track.getUser().getUsername().equals(username)) {
            throw new RuntimeException("권한 없음");
        }
        trackRepository.delete(track);
    }

    // 파일 저장 유틸
    private String saveFile(MultipartFile file, String type) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path dir = Paths.get(uploadDir, type);
        Files.createDirectories(dir);
        Files.copy(file.getInputStream(), dir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + type + "/" + fileName;
    }

    // Entity → DTO 변환
    private TrackDto.Response toResponse(Track track, String username) {
        Long userId = username != null ?
                userRepository.findByUsername(username).map(User::getId).orElse(null) : null;

        return TrackDto.Response.builder()
                .id(track.getId())
                .title(track.getTitle())
                .description(track.getDescription())
                .audioUrl(track.getAudioUrl())
                .coverImage(track.getCoverImage())
                .genre(track.getGenre())
                .playCount(track.getPlayCount())
                .likeCount(likeRepository.countByTrackId(track.getId()))
                .commentCount((long) commentRepository.findByTrackIdOrderByCreatedAtDesc(track.getId()).size())
                .likedByMe(userId != null && likeRepository.existsByUserIdAndTrackId(userId, track.getId()))
                .uploader(UserDto.Simple.builder()
                        .id(track.getUser().getId())
                        .username(track.getUser().getUsername())
                        .displayName(track.getUser().getDisplayName())
                        .profileImage(track.getUser().getProfileImage())
                        .build())
                .createdAt(track.getCreatedAt())
                .build();
    }
}
