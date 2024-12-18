package com.ci.Cruming.file.repository;

import com.ci.Cruming.common.constants.FileTargetType;
import com.ci.Cruming.file.entity.File;
import com.ci.Cruming.file.entity.FileMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    @Query("SELECT f FROM File f " +
            "JOIN f.mapping m " +
            "WHERE m.targetId = :postId " +
            "AND m.targetType = :targetType " +
            "ORDER BY f.displayOrder")
    List<File> findByPostId(@Param("postId") Long postId,
                            @Param("targetType") FileTargetType targetType);

    @Modifying
    @Query("UPDATE File f SET f.status = 'DELETED' WHERE f.mapping.targetId = :postId AND f.mapping.targetType = :targetType")
    void deleteByPostId(@Param("postId") Long postId, @Param("targetType") FileTargetType targetType);


    @Query("SELECT f FROM File f WHERE f.mapping = :fileMapping ORDER BY f.displayOrder")
    List<File> findAllByFileMapping(@Param("fileMapping") FileMapping fileMapping);

    Optional<File> findFirstByMappingOrderByDisplayOrderAsc(FileMapping mapping);

    @Query("SELECT f FROM File f " +
            "JOIN f.mapping m " +
            "WHERE m.targetId = :timelineId " +
            "AND m.targetType = :targetType " +
            "ORDER BY f.displayOrder")
    List<File> findByTimelineId(Long timelineId, FileTargetType targetType);
}
