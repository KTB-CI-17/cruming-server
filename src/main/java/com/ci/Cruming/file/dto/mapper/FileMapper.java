package com.ci.Cruming.file.dto.mapper;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.ci.Cruming.common.constants.FileStatus;
import com.ci.Cruming.common.constants.FileType;
import com.ci.Cruming.common.utils.FileUtils;
import com.ci.Cruming.file.dto.FileResponse;
import com.ci.Cruming.file.entity.File;
import com.ci.Cruming.file.entity.FileMapping;
import com.ci.Cruming.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Calendar;

@Component
@RequiredArgsConstructor
public class FileMapper {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

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
        String presignedUrl = createPresignedUrl(file.getFileKey());

        return new FileResponse(
                file.getId(),
                file.getFileName(),
                presignedUrl,
                file.getDisplayOrder()
        );
    }

    public String createPresignedUrl(String fileKey) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 30); // 30분 유효

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileKey)
                .withMethod(HttpMethod.GET)
                .withExpiration(cal.getTime());

        URL url = amazonS3Client.generatePresignedUrl(request);
        return url.toString();
    }
}
