package com.ci.Cruming.common.utils;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileUtils {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new CrumingException(ErrorCode.INVALID_FILE_NAME);
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    public String generatePostFileKey(String fileExtension) {
        return String.format("posts/%s/%s.%s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                UUID.randomUUID(),
                fileExtension);
    }

    public String generateProfileImageKey(String fileExtension) {
        return String.format("profile/%s.%s",
                UUID.randomUUID(),
                fileExtension);
    }

    public String generatePresignedUrl(String fileKey) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 30);

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileKey)
                .withMethod(HttpMethod.GET)
                .withExpiration(cal.getTime());

        URL url = amazonS3Client.generatePresignedUrl(request);
        return url.toString();
    }
}