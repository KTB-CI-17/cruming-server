package com.ci.Cruming.post.repository;

import com.ci.Cruming.post.entity.PostReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReplyRepository extends JpaRepository<PostReply, Long> {
    Page<PostReply> findByPostIdAndParentIsNull(Long postId, Pageable pageable);
    Page<PostReply> findByParentId(Long parentId, Pageable pageable);
}
