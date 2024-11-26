package com.ci.Cruming.file.storage;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
@Slf4j
public class LocalFileStorage implements FileStorage {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String store(MultipartFile file, String fileKey) {
        try {
            Path uploadPath = Paths.get(uploadDir)
                    .toAbsolutePath()
                    .normalize();
            Files.createDirectories(uploadPath);

            Path targetLocation = uploadPath.resolve(fileKey);
            Files.createDirectories(targetLocation.getParent());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return targetLocation.toString();

        } catch (IOException ex) {
            throw new CrumingException(ErrorCode.FAIL_FILE_SAVE);
        }
    }

    @Override
    public boolean exists(String fileUrl) {
        return Files.exists(Paths.get(fileUrl));
    }

    @Override
    public String getUrl(String fileKey) {
        return Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize()
                .resolve(fileKey)
                .toString();
    }

    @Override
    public Resource loadAsResource(String fileKey) {
        try {
            Path file = Paths.get(uploadDir).resolve(fileKey);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new CrumingException(ErrorCode.FILE_NOT_FOUND);
            }
        } catch (MalformedURLException e) {
            throw new CrumingException(ErrorCode.FILE_NOT_FOUND);
        }
    }
}