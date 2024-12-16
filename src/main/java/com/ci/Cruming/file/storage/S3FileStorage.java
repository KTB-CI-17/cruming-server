package com.ci.Cruming.file.storage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileStorage {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String store(MultipartFile file, String fileKey) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3.putObject(new PutObjectRequest(bucket, fileKey, file.getInputStream(), metadata));
            return getUrl(fileKey);
        } catch (IOException e) {
            log.error("Failed to store file to S3", e);
            throw new CrumingException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    public boolean exists(String fileUrl) {
        return amazonS3.doesObjectExist(bucket, fileUrl);
    }

    public String getUrl(String fileKey) {
        return amazonS3.getUrl(bucket, fileKey).toString();
    }

    public Resource loadAsResource(String fileKey) {
        URL url = amazonS3.getUrl(bucket, fileKey);
        return new UrlResource(url);
    }
}