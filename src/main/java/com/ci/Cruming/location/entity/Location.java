package com.ci.Cruming.location.entity;

import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.timeline.entity.Timeline;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "location")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String placeName;

    @Column(nullable = false, length = 200)
    private String address;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "homeGym")
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "location")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "location")
    private List<Timeline> timelines = new ArrayList<>();
}
