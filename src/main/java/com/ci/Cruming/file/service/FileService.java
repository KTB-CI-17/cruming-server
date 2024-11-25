package com.ci.Cruming.file.service;

import com.ci.Cruming.common.constants.FileTargetType;
import com.ci.Cruming.common.utils.FileUtils;
import com.ci.Cruming.file.dto.FileRequest;
import com.ci.Cruming.file.dto.mapper.FileMapper;
import com.ci.Cruming.file.entity.File;
import com.ci.Cruming.file.entity.FileMapping;
import com.ci.Cruming.file.repository.FileMappingRepository;
import com.ci.Cruming.file.repository.FileRepository;
import com.ci.Cruming.file.service.validator.FileValidator;
import com.ci.Cruming.file.storage.FileStorage;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final FileStorage fileStorage;
    private final FileMappingRepository fileMappingRepository;
    private final FileRepository fileRepository;
    private final FileValidator fileValidator;
    private final FileMapper fileMapper;

    public void validateFiles(List<MultipartFile> files, List<FileRequest> fileRequests) {
        fileValidator.validateFiles(files, fileRequests);
    }

    @Transactional
    public List<File> uploadFiles(List<MultipartFile> files, List<FileRequest> fileRequests, Post post) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        FileMapping fileMapping = fileMapper.toFileMapping(post);
        FileMapping savedMapping = fileMappingRepository.save(fileMapping);

        return saveFiles(files, fileRequests, savedMapping, post.getUser());
    }

    private List<File> saveFiles(List<MultipartFile> files, List<FileRequest> fileRequests,
                                 FileMapping fileMapping, User user) {
        List<File> savedFiles = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);

            String fileKey = FileUtils.generateFileKey(FileUtils.getFileExtension(file.getOriginalFilename()));

            String storedUrl = fileStorage.store(file, fileKey);

            File savedFile = fileMapper.toFile(file, fileMapping, user,
                    fileRequests.get(i).displayOrder(),
                    storedUrl, fileKey);
            savedFiles.add(fileRepository.save(savedFile));
        }

        return savedFiles;
    }

    public List<File> getFilesByPost(Post post) {
        return fileRepository.findByPostId(post.getId(), FileTargetType.POST);
    }
}