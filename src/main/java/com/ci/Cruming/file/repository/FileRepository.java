package com.ci.Cruming.file.repository;

import com.ci.Cruming.common.constants.FileTargetType;
import com.ci.Cruming.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    @Query("SELECT f FROM File f " +
            "WHERE f.mapping.targetId = :postId " +
            "AND f.mapping.targetType = :targetType " +
            "ORDER BY f.displayOrder")
    List<File> findByPostId(@Param("postId") Long postId,
                            @Param("targetType") FileTargetType targetType);

}
