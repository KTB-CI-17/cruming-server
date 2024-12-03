package com.ci.Cruming.file.dto.mapper;

import com.ci.Cruming.common.constants.FileStatus;
import com.ci.Cruming.common.constants.FileTargetType;
import com.ci.Cruming.common.constants.FileType;
import com.ci.Cruming.common.utils.FileUtils;
import com.ci.Cruming.file.dto.FileResponse;
import com.ci.Cruming.file.entity.File;
import com.ci.Cruming.file.entity.FileMapping;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.user.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Component
public class FileMapper {
    public FileMapping toFileMapping(Post post) {
        return FileMapping.builder()
                .targetType(FileTargetType.POST)
                .targetId(post.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }


    public File toFile(MultipartFile multipartFile, FileMapping fileMapping,
                       User user, Integer displayOrder, String storedUrl, String fileKey) {
        return File.builder()
                .mapping(fileMapping)
                .fileName(multipartFile.getOriginalFilename())
                .fileKey(fileKey)
                .url(storedUrl)
                .fileType(FileType.valueOf(FileUtils.getFileExtension(multipartFile.getOriginalFilename()).toUpperCase()))
                .fileSize(multipartFile.getSize())
                .user(user)
                .status(FileStatus.ACTIVE)
                .displayOrder(displayOrder)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public FileResponse toFileResponse(File file) {
        return new FileResponse(
                file.getId(),
                file.getFileName(),
                file.getFileKey(),
                "/files/" + file.getFileKey(),
                file.getFileType(),
                file.getFileSize(),
                file.getDisplayOrder(),
                file.getUser().getId(),
                file.getStatus(),
                file.getCreatedAt()
        );
    }
}