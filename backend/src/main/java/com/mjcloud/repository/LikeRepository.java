package com.mjcloud.repository;

import com.mjcloud.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndTrackId(Long userId, Long trackId);
    Optional<Like> findByUserIdAndTrackId(Long userId, Long trackId);
    long countByTrackId(Long trackId);
}
