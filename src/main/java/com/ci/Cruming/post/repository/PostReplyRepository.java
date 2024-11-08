package com.ci.Cruming.post.repository;

import com.ci.Cruming.post.entity.PostReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReplyRepository extends JpaRepository<PostReply, Long> {

}
