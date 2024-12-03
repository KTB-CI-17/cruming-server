package com.ci.Cruming.common.utils;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FileUtils {
    public static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new CrumingException(ErrorCode.INVALID_FILE_NAME);
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    public static String generateFileKey(String fileExtension) {
        return String.format("posts/%s/%s.%s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                UUID.randomUUID(),
                fileExtension);
    }
}