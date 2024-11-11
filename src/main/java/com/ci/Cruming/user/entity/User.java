package com.ci.Cruming.user.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nickname;

    private Short height;
    
    @Column(name = "arm_reach")
    private Short armReach;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;

    @Column(name = "platform_id")
    private String platformId;

    @Column(length = 300)
    private String intro;

    @Column(name = "home_gym")
    private Long homeGym;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    public User(String nickname, Platform platform, String platformId) {
        this.nickname = nickname;
        this.platform = platform;
        this.platformId = platformId;
    }
} 