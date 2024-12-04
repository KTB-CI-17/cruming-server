package com.ci.Cruming.post.service;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.common.constants.FileTargetType;
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.file.dto.FileResponse;
import com.ci.Cruming.file.dto.mapper.FileMapper;
import com.ci.Cruming.file.entity.File;
import com.ci.Cruming.file.entity.FileMapping;
import com.ci.Cruming.file.service.FileService;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.location.service.LocationService;
import com.ci.Cruming.post.dto.*;
import com.ci.Cruming.post.dto.mapper.PostMapper;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.post.repository.PostLikeRepository;
import com.ci.Cruming.post.repository.PostReplyRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostReplyRepository postReplyRepository;
    private final LocationService locationService;
    private final PostLikeRepository postLikeRepository;
    private final FileService fileService;
    private final PostValidator postValidator;
    private final PostMapper postMapper;
    private final FileMapper fileMapper;

    @Transactional
    public void createPost(User user, PostRequest request, List<MultipartFile> files) {
        postValidator.validatePostRequest(request);

        Post post = createPostWithLocation(user, request);
        postRepository.save(post);
        FileMapping fileMapping = createFileMapping(post.getId());

        fileMapping = fileService.createFiles(user, fileMapping, files, request.fileRequests());
        post.setFileMapping(fileMapping);
    }


    @Transactional
    public void updatePost(User user, Long postId, PostEditRequest request, List<MultipartFile> newFiles) {
        Post post = getPost(postId);
        postValidator.validatePostAuthor(post, user);
        postValidator.validatePostEditRequest(request);

        fileService.deleteFiles(request.deleteFileIds());

        fileService.editFiles(
                user,
                Optional.ofNullable(post.getFileMapping())
                        .orElseGet(() -> createFileMapping(post.getId())),
                newFiles,
                request.newFiles()
        );

        post.update(request.title(), request.content(), request.level(), updateLocation(request));
        postRepository.save(post);
    }

    @Transactional
    public void deletePost(User user, Long postId) {
        Post post = getPost(postId);
        postValidator.validatePostAuthor(post, user);
        postRepository.delete(post);
        fileService.deleteByPost(post);
    }

    @Transactional
    public void increasePostView(Long postId) {
        Post post = postRepository.getReferenceById(postId);
        post.incrementViews();
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

        boolean isLiked = postLikeRepository.existsByPostAndUser(post, user);
        Long likeCount = postLikeRepository.countByPost(post);
        Long replyCount = postReplyRepository.countByPost(post);

        return postMapper.toPostResponse(user, post, files, isLiked, likeCount, replyCount);
    }

    public PostEditInfo findPostEditInfo(Long postId) {
        Post post = getPost(postId);
        List<FileResponse> files = fileService.getFilesByPost(post)
                .stream()
                .map(fileMapper::toFileResponse)
                .collect(Collectors.toList());

        return postMapper.toPostEditInfo(post, files);
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CrumingException(ErrorCode.POST_NOT_FOUND));
    }


    private Post createPostWithLocation(User user, PostRequest request) {
        Location location = resolveLocation(request);
        return postMapper.toPost(user, request, location);
    }

    private Location resolveLocation(PostRequest request) {
        if (Category.isProblem(request.category())) {
            return locationService.getOrCreateLocation(request.locationRequest());
        }

        return null;
    }

    private Location updateLocation(PostEditRequest request) {
        if (Category.isProblem(request.category())) {
            return locationService.getOrCreateLocation(request.locationRequest());
        }

        return null;
    }

    private FileMapping createFileMapping(Long postId) {
        return FileMapping.builder()
                .targetType(FileTargetType.POST)
                .targetId(postId)
                .build();
    }


}
