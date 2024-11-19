package com.ci.Cruming.location.entity;

import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.timeline.entity.Timeline;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "location")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "homeGym")
    private List<User> users = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "location")
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "location")
    private List<Timeline> timelines = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (!placeName.equals(location.placeName)) return false;
        return address.equals(location.address);
    }

    @Override
    public int hashCode() {
        int result = placeName.hashCode();
        result = 31 * result + address.hashCode();
        return result;
    }
}
