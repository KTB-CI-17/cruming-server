package com.ci.Cruming.follow.entity;

import com.ci.Cruming.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "follow")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private User follower;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    private User following;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
