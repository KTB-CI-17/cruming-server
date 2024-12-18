package com.ci.Cruming.file.service;

import com.ci.Cruming.common.constants.FileTargetType;
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.common.utils.FileUtils;
import com.ci.Cruming.file.dto.FileRequest;
import com.ci.Cruming.file.dto.mapper.FileMapper;
import com.ci.Cruming.file.entity.File;
import com.ci.Cruming.file.entity.FileMapping;
import com.ci.Cruming.file.repository.FileMappingRepository;
import com.ci.Cruming.file.repository.FileRepository;
import com.ci.Cruming.file.storage.S3FileStorage;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final S3FileStorage s3FileStorage;
    private final FileMappingRepository fileMappingRepository;
    private final FileRepository fileRepository;
    private final FileMapper fileMapper;
    private final FileUtils fileUtils;

    @Transactional
    public FileMapping createFiles(User user, FileMapping fileMapping, List<MultipartFile> files, List<FileRequest> fileRequests) {
        if (CollectionUtils.isEmpty(files) || CollectionUtils.isEmpty(fileRequests)) {
            return null;
        }

        addFiles(user, fileMapping, files, fileRequests);

        return fileMappingRepository.save(fileMapping);
    }

    @Transactional
    public void editFiles(User user, FileMapping fileMapping, List<MultipartFile> newFiles, List<FileRequest> newFileRequests) {
        if (CollectionUtils.isEmpty(newFiles) || CollectionUtils.isEmpty(newFileRequests)) {
            return;
        }
        addFiles(user, fileMapping, newFiles, newFileRequests);

        fileMappingRepository.save(fileMapping);
    }

    @Transactional
    public void deleteFiles(List<Long> fileIds) {
        Optional.ofNullable(fileIds)
                .filter(ids -> !ids.isEmpty())
                .ifPresent(ids -> ids.forEach(fileId -> fileRepository.getReferenceById(fileId).delete()));
    }

    private void addFiles(User user, FileMapping fileMapping, List<MultipartFile> files, List<FileRequest> fileRequests) {
        for (MultipartFile file : files) {
            FileRequest matchingRequest = fileRequests.stream()
                    .filter(request -> request.originalFileName().equals(file.getOriginalFilename()))
                    .findFirst()
                    .orElseThrow(() -> new CrumingException(ErrorCode.INVALID_FILE_REQUEST));
            String fileKey = fileUtils.generatePostFileKey(fileUtils.getFileExtension(file.getOriginalFilename()));
            String storedUrl = s3FileStorage.store(file, fileKey);
            File fileEntity = fileMapper.toFile(file, fileMapping, user, matchingRequest.displayOrder(), storedUrl, fileKey);
            fileMapping.addFile(fileEntity);
        }
    }

    public List<File> getFilesByPost(Post post) {
        return fileRepository.findByPostId(post.getId(), FileTargetType.POST);
    }

    public void deleteByPost(Post post) {
        fileRepository.deleteByPostId(post.getId(), FileTargetType.POST);
    }

    public String storeProfileImageAndGetFileKey(MultipartFile file) {
        String fileKey = fileUtils.generateProfileImageKey(fileUtils.getFileExtension(file.getOriginalFilename()));
        s3FileStorage.store(file, fileKey);
        return fileKey;
    }
}