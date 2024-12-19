package com.ci.Cruming.post.repository;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.category = :category ORDER BY p.createdAt DESC")
    Page<Post> findByPostInCategory(Pageable pageable, @Param("category") Category category);
}
