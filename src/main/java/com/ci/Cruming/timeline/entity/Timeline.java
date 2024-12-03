package com.ci.Cruming.timeline.entity;

import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.common.constants.Visibility;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "timeline")
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
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
    @Builder.Default
    private List<TimelineLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "timeline")
    @Builder.Default
    private List<TimelineReply> replies = new ArrayList<>();

    private Timeline(Long id, User user, Location location, String level, String content, 
                   Visibility visibility, LocalDateTime activityAt) {
        this.id = id;
        this.user = user;
        this.location = location;
        this.level = level;
        this.content = content;
        this.visibility = visibility;
        this.activityAt = activityAt;
        this.createdAt = LocalDateTime.now();
    }
    
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
    
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
    
    public int getLikeCount() {
        return this.likes.size();
    }
    
    public int getReplyCount() {
        return this.replies.size();
    }
}
