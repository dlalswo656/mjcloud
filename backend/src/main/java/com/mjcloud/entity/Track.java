package com.mjcloud.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tracks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;          // 트랙 제목

    @Column(length = 500)
    private String description;    // 설명

    @Column(nullable = false)
    private String audioUrl;       // 음악 파일 경로

    private String coverImage;     // 커버 이미지 경로

    @Column(length = 50)
    private String genre;          // 장르 (팝, 록, 재즈, 클래식 등)

    private Integer duration;      // 재생 시간 (초)

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer playCount = 0; // 재생 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;             // 업로더

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL)
    private List<Like> likes;

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.playCount == null) this.playCount = 0;
    }
}
