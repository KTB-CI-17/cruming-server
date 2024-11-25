package com.ci.Cruming.post.service;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.file.dto.FileResponse;
import com.ci.Cruming.file.dto.mapper.FileMapper;
import com.ci.Cruming.file.entity.File;
import com.ci.Cruming.file.service.FileService;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.location.service.LocationService;
import com.ci.Cruming.post.dto.PostListResponse;
import com.ci.Cruming.post.dto.PostProblemRequest;
import com.ci.Cruming.post.dto.PostGeneralRequest;
import com.ci.Cruming.post.dto.PostResponse;
import com.ci.Cruming.post.dto.mapper.PostMapper;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.post.service.validator.PostValidator;
import com.ci.Cruming.user.entity.User;
import com.ci.Cruming.post.repository.PostRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final LocationService locationService;
    private final FileService fileService;
    private final PostValidator postValidator;
    private final PostMapper postMapper;
    private final FileMapper fileMapper;

    @Transactional
    public void createGeneral(User user, PostGeneralRequest request, List<MultipartFile> files) {
        postValidator.validatePostGeneralRequest(request);
        fileService.validateFiles(files, request.files());

        Post post = postMapper.toGeneralPost(user, request);
        Post savedPost = postRepository.save(post);

        fileService.uploadFiles(files, request.files(), savedPost);
    }

    @Transactional
    public void createProblem(User user, PostProblemRequest request, List<MultipartFile> files) {
        postValidator.validatePostProblemRequest(request);
        fileService.validateFiles(files, request.files());

        Location location = locationService.getOrCreateLocation(request.location());
        Post post = postMapper.toProblemPost(user, request, location);
        Post savedPost = postRepository.save(post);

        fileService.uploadFiles(files, request.files(), savedPost);
    }

    @Transactional
    public void updateGeneral(User user, Long postId, PostGeneralRequest request, List<MultipartFile> files) {
        Post post = getPost(postId);
        postValidator.validatePostAuthor(post, user);
        postValidator.validatePostGeneralRequest(request);
        fileService.validateFiles(files, request.files());

        post.update(request.title(), request.content());

        if (files != null && !files.isEmpty()) {
            fileService.uploadFiles(files, request.files(), post);
        }
    }

    @Transactional
    public void updateProblem(User user, Long postId, PostProblemRequest request, List<MultipartFile> files) {
        Post post = getPost(postId);
        postValidator.validatePostAuthor(post, user);
        postValidator.validatePostProblemRequest(request);
        fileService.validateFiles(files, request.files());

        Location location = locationService.getOrCreateLocation(request.location());
        post.update(request.title(), request.content(), request.level(), location);

        if (files != null && !files.isEmpty()) {
            fileService.uploadFiles(files, request.files(), post);
        }
    }

    @Transactional
    public void deletePost(User user, Long postId) {
        Post post = getPost(postId);
        postValidator.validatePostAuthor(post, user);
        postRepository.delete(post);
        fileService.deleteByPost(post);
    }

    public Page<PostListResponse> findPostList(Pageable pageable, Category category) {
        return postRepository.findByPostInCategory(pageable, category)
                .map(postMapper::toPostListResponse);
    }

    public PostResponse findPost(User user, Long postId) {
        Post post = getPost(postId);
        List<File> filesByPost = fileService.getFilesByPost(post);
        List<FileResponse> files = filesByPost.stream()
                .map(fileMapper::toFileResponse)
                .collect(Collectors.toList());

        return postMapper.toPostResponse(user, post, files);
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CrumingException(ErrorCode.POST_NOT_FOUND));
    }
}
