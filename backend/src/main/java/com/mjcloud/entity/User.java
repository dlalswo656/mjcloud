package com.mjcloud.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 30)
    private String username;       // 아이디

    @Column(nullable = false)
    private String password;       // 비밀번호 (암호화)

    @Column(length = 50)
    private String displayName;    // 표시 이름 (닉네임)

    @Column(length = 200)
    private String bio;            // 자기소개

    private String profileImage;   // 프로필 이미지 경로

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.displayName == null) this.displayName = this.username;
    }

    // 팔로워/팔로잉 수 (조회용)
    @OneToMany(mappedBy = "following", fetch = FetchType.LAZY)
    private List<Follow> followers;

    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY)
    private List<Follow> followings;
}
