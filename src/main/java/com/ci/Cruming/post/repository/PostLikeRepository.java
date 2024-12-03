package com.ci.Cruming.post.repository;

import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.post.entity.PostLike;
import com.ci.Cruming.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostAndUser(Post post, User user);

    Long countByPost(Post post);

    boolean existsByPostAndUser(Post post, User user);



}
