package com.mjcloud.service;

import com.mjcloud.dto.UserDto;
import com.mjcloud.entity.Follow;
import com.mjcloud.entity.User;
import com.mjcloud.repository.FollowRepository;
import com.mjcloud.repository.TrackRepository;
import com.mjcloud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final TrackRepository trackRepository;

    // 내 프로필 조회
    public UserDto.Profile getMyProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        return toProfile(user, username);
    }

    // 특정 유저 프로필 조회
    public UserDto.Profile getProfile(Long userId, String viewerUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        return toProfile(user, viewerUsername);
    }

    // 팔로우 토글
    @Transactional
    public boolean toggleFollow(Long targetUserId, String followerUsername) {
        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("대상 사용자 없음"));

        if (follower.getId().equals(targetUserId)) {
            throw new RuntimeException("자기 자신을 팔로우할 수 없습니다");
        }

        if (followRepository.existsByFollowerIdAndFollowingId(follower.getId(), targetUserId)) {
            followRepository.findByFollowerIdAndFollowingId(follower.getId(), targetUserId)
                    .ifPresent(followRepository::delete);
            return false; // 언팔로우
        } else {
            followRepository.save(Follow.builder()
                    .follower(follower)
                    .following(target)
                    .build());
            return true; // 팔로우
        }
    }

    // 프로필 수정
    @Transactional
    public UserDto.Profile updateProfile(String username, String displayName, String bio) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        if (displayName != null && !displayName.isBlank()) user.setDisplayName(displayName);
        if (bio != null) user.setBio(bio);
        userRepository.save(user);
        return toProfile(user, username);
    }

    // Entity → DTO
    private UserDto.Profile toProfile(User user, String viewerUsername) {
        Long viewerId = null;
        if (viewerUsername != null) {
            viewerId = userRepository.findByUsername(viewerUsername)
                    .map(User::getId).orElse(null);
        }

        boolean followedByMe = viewerId != null &&
                followRepository.existsByFollowerIdAndFollowingId(viewerId, user.getId());

        return UserDto.Profile.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .bio(user.getBio())
                .profileImage(user.getProfileImage())
                .followerCount(followRepository.countByFollowingId(user.getId()))
                .followingCount(followRepository.countByFollowerId(user.getId()))
                .trackCount(trackRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).size())
                .followedByMe(followedByMe)
                .build();
    }
}
