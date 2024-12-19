package com.ci.Cruming.common.constants;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileType {
    JPEG("image/jpeg"),
    JPG("image/jpeg"),
    PNG("image/png"),
    GIF("image/gif"),
    WEBP("image/webp");

    private final String mimeType;

    public static FileType fromExtension(String extension) {
        try {
            return FileType.valueOf(extension.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CrumingException(ErrorCode.INVALID_FILE_EXTENSION);
        }
    }
}
