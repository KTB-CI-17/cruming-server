package com.ci.Cruming.domain.file;

import com.ci.Cruming.domain.User;
import com.ci.Cruming.domain.constants.FileStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "file")
@Getter
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

    @Column(nullable = false, length = 50)
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
