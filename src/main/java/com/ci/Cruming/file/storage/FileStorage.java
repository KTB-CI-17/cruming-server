package com.ci.Cruming.file.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {
    String store(MultipartFile file, String fileKey);

    boolean exists(String fileUrl);

    String getUrl(String fileKey);
}
