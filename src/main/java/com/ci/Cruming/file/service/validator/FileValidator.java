package com.ci.Cruming.file.service.validator;

import com.ci.Cruming.common.constants.FileType;
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.common.utils.FileUtils;
import com.ci.Cruming.file.dto.FileRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@Slf4j
public class FileValidator {
    @Value("${file.max-size}")
    private Long maxFileSize;

    private static final int MAX_FILES_PER_REQUEST = 5;

    public void validateProblemPostFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CrumingException(ErrorCode.INVALID_PROBLEM_POST_FILE);
        }
    }

    public void validateFiles(List<MultipartFile> files, List<FileRequest> fileRequests) {
        if (files == null || files.isEmpty()) {
            return;
        }

        if (files.size() > MAX_FILES_PER_REQUEST) {
            throw new CrumingException(ErrorCode.MAX_FILE_COUNT_EXCEEDED);
        }

        if (files.size() != fileRequests.size()) {
            throw new CrumingException(ErrorCode.INVALID_FILE_REQUEST);
        }

        files.forEach(this::validateFile);
        validateFileNameMatching(files, fileRequests);
    }

    private void validateFile(MultipartFile file) {
        validateFileName(file);
        validateFileSize(file);
        validateFileType(file);
    }

    private void validateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isEmpty()) {
            log.error("File name is empty");
            throw new CrumingException(ErrorCode.INVALID_FILE_NAME);
        }

        if (originalFilename.contains("..")) {
            log.error("File name contains invalid path sequence: {}", originalFilename);
            throw new CrumingException(ErrorCode.INVALID_FILE_NAME);
        }
    }

    private void validateFileSize(MultipartFile file) {
        if (file.getSize() == 0) {
            log.error("File is empty: {}", file.getOriginalFilename());
            throw new CrumingException(ErrorCode.EMPTY_FILE);
        }

        if (file.getSize() > maxFileSize) {
            log.error("File size exceeds maximum limit: {} bytes", file.getSize());
            throw new CrumingException(ErrorCode.FILE_SIZE_EXCEED);
        }
    }

    private void validateFileType(MultipartFile file) {
        String fileExtension = FileUtils.getFileExtension(file.getOriginalFilename());
        try {
            FileType.valueOf(fileExtension.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Unsupported file type: {}", fileExtension);
            throw new CrumingException(ErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    private void validateFileNameMatching(List<MultipartFile> files, List<FileRequest> fileRequests) {
        for (int i = 0; i < files.size(); i++) {
            if (!files.get(i).getOriginalFilename().equals(fileRequests.get(i).originalFileName())) {
                throw new CrumingException(ErrorCode.FILE_NAME_MISMATCH);
            }
        }
    }
}