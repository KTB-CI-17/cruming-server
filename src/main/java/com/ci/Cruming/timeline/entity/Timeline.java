package com.ci.Cruming.timeline.entity;

import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.common.constants.Visibility;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "timeline")
@SQLDelete(sql = "UPDATE timeline SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
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
    @Where(clause = "deleted_at IS NULL")
    private List<TimelineLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "timeline")
    @Builder.Default
    @Where(clause = "deleted_at IS NULL")
    private List<TimelineReply> replies = new ArrayList<>();
    
    public int getLikeCount() {
        return this.likes.size();
    }
    
    public int getReplyCount() {
        return this.replies.size();
    }

}
