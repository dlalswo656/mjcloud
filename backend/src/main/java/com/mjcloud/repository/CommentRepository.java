package com.mjcloud.repository;

import com.mjcloud.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTrackIdOrderByCreatedAtDesc(Long trackId);
}
