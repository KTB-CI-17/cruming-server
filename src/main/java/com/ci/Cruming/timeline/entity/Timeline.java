package com.ci.Cruming.timeline.entity;

import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.common.constants.Visibility;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "timeline")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Timeline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(length = 20)
    private String level;

    @Column(nullable = false, length = 3000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    @Column(nullable = false)
    private LocalDateTime activityAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "timeline")
    private List<TimelineLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "timeline")
    private List<TimelineReply> replies = new ArrayList<>();
}
