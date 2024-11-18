package com.ci.Cruming.timeline.entity;

import com.ci.Cruming.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "timeline_like")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimelineLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeline_id", nullable = false)
    private Timeline timeline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public TimelineLike(Timeline timeline, User user) {
        this.timeline = timeline;
        this.user = user;
    }
}
