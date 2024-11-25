package com.ci.Cruming.file.entity;

import com.ci.Cruming.common.constants.FileTargetType;
import com.ci.Cruming.common.constants.FileType;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.common.constants.FileStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "file")
@Getter
@Builder
@Where(clause = "status IS NOT DELETED")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mapping_id", nullable = false)
    private FileMapping mapping;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileKey;

    @Column
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "file_type")
    private FileType fileType;

    @Column(nullable = false)
    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileStatus status;

    @Column
    private Integer displayOrder;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void delete() {
        this.status = FileStatus.DELETED;
    }

}
