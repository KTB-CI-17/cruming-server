package com.ci.Cruming.domain;

import com.ci.Cruming.domain.post.Post;
import com.ci.Cruming.domain.timeline.Timeline;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "location")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String placeName;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(precision = 17, scale = 14)
    private BigDecimal latitude;

    @Column(precision = 17, scale = 14)
    private BigDecimal longitude;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "homeGym")
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "location")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "location")
    private List<Timeline> timelines = new ArrayList<>();
}
