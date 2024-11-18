package com.ci.Cruming.user.entity;


import com.ci.Cruming.location.entity.Location;
import jakarta.persistence.*;
import lombok.*;
import com.ci.Cruming.common.constants.Platform;
import com.ci.Cruming.common.constants.UserStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_gym", nullable = false)
    private Location homeGym;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    public User(String nickname, Platform platform, String platformId) {
        this.nickname = nickname;
        this.platform = platform;
        this.platformId = platformId;
    }
} 