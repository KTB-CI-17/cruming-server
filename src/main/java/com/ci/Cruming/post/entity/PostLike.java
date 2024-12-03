package com.ci.Cruming.post.entity;

import com.ci.Cruming.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "post_like",
    uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_post_like_post_user",
                    columnNames = {"post_id", "user_id"}
            )
    }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
