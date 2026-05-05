package com.mjcloud.repository;

import com.mjcloud.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long>,
        QuerydslPredicateExecutor<Track> {
    List<Track> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Track> findByGenreOrderByPlayCountDesc(String genre);
}
