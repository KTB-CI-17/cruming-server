package com.ci.Cruming.post.service;

import com.ci.Cruming.common.constants.Category;
import com.ci.Cruming.common.constants.Visibility;
import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.location.dto.LocationRequest;
import com.ci.Cruming.location.entity.Location;
import com.ci.Cruming.location.service.LocationService;
import com.ci.Cruming.post.dto.PostGeneralRequest;
import com.ci.Cruming.post.dto.PostListResponse;
import com.ci.Cruming.post.dto.PostProblemRequest;
import com.ci.Cruming.post.dto.mapper.PostMapper;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.post.repository.PostRepository;
import com.ci.Cruming.post.service.validator.PostValidator;
import com.ci.Cruming.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private LocationService locationService;

    @Mock
    private PostMapper postMapper;

    @Mock
    private PostValidator postValidator;

    @Test
    @DisplayName("일반 게시글 작성 - 성공")
    void createGeneral_Success() {
        // given
        User user = User.builder()
                .id(1L)
                .build();

        PostGeneralRequest request = new PostGeneralRequest("제목", "내용");

        Post post = Post.builder()
                .id(1L)
                .user(user)
                .title(request.title())
                .content(request.content())
                .category(Category.GENERAL)
                .build();

        // when
        when(postMapper.toGeneralPost(any(), any())).thenReturn(post);
        when(postRepository.save(any())).thenReturn(post);

        postService.createGeneral(user, request);

        // then
        verify(postMapper).toGeneralPost(any(), any());
        verify(postRepository).save(any());
        verifyNoMoreInteractions(postRepository, postMapper);
    }

    @Test
    @DisplayName("일반 게시글 작성 - 제목 길이 초과 실패")
    void createGeneral_TitleLengthExceeded() {
        // given
        User user = User.builder().id(1L).build();
        String longTitle = "한글ABC特🎉".repeat(20);
        PostGeneralRequest request = new PostGeneralRequest(longTitle, "내용");

        // when
        doThrow(new CrumingException(ErrorCode.INVALID_POST_TITLE_SIZE))
                .when(postValidator).validatePostGeneralRequest(any());

        // then
        assertThatThrownBy(() -> postService.createGeneral(user, request))
                .isInstanceOf(CrumingException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_POST_TITLE_SIZE);

        verify(postValidator).validatePostGeneralRequest(any());
        verifyNoInteractions(postMapper, postRepository);
    }

    @Test
    @DisplayName("일반 게시글 작성 - 내용 길이 초과 실패")
    void createGeneral_ContentLengthExceeded() {
        // given
        User user = User.builder().id(1L).build();
        String longContent = "한글ABC特🎉".repeat(200);
        PostGeneralRequest request = new PostGeneralRequest("제목", longContent);

        // when
        doThrow(new CrumingException(ErrorCode.INVALID_POST_CONTENT_SIZE))
                .when(postValidator).validatePostGeneralRequest(any());

        // then
        assertThatThrownBy(() -> postService.createGeneral(user, request))
                .isInstanceOf(CrumingException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_POST_CONTENT_SIZE);

        verify(postValidator).validatePostGeneralRequest(any());
        verifyNoInteractions(postMapper, postRepository);
    }

    @Test
    @DisplayName("문제 게시글 작성 - 성공")
    void createProblem_Success() {
        // given
        User user = User.builder()
                .id(1L)
                .build();

        Location location = Location.builder()
                .id(1L)
                .placeName("장소명")
                .address("주소")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();

        PostProblemRequest request = new PostProblemRequest(
                "제목",
                "내용",
                new LocationRequest("장소명", "주소", 37.5665, 126.9780),
                "#31235"
        );

        Post post = Post.builder()
                .id(1L)
                .user(user)
                .location(location)
                .title(request.title())
                .content(request.content())
                .category(Category.PROBLEM)
                .build();

        // when
        when(locationService.getOrCreateLocation(any())).thenReturn(location);
        when(postMapper.toProblemPost(any(), any(), any())).thenReturn(post);
        when(postRepository.save(any())).thenReturn(post);

        postService.createProblem(user, request);

        // then
        verify(locationService).getOrCreateLocation(any());
        verify(postMapper).toProblemPost(any(), any(), any());
        verify(postRepository).save(any());
        verifyNoMoreInteractions(locationService, postMapper, postRepository);
    }

    @Test
    @DisplayName("문제 게시글 작성 - 제목 길이 초과 실패")
    void createProblem_TitleLengthExceeded() {
        // given
        User user = User.builder()
                .id(1L)
                .build();

        String longTitle = "한글ABC特🎉".repeat(20);
        PostProblemRequest request = new PostProblemRequest(
                longTitle,
                "내용",
                new LocationRequest("장소명", "주소", 37.5665, 126.9780),
                "#31235"
        );

        // when
        doThrow(new CrumingException(ErrorCode.INVALID_POST_TITLE_SIZE))
                .when(postValidator).validatePostProblemRequest(any());

        // then
        assertThatThrownBy(() -> postService.createProblem(user, request))
                .isInstanceOf(CrumingException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_POST_TITLE_SIZE);

        verify(postValidator).validatePostProblemRequest(any());
        verifyNoInteractions(locationService, postMapper, postRepository);
    }

    @Test
    @DisplayName("문제 게시글 작성 - 내용 길이 초과 실패")
    void createProblem_ContentLengthExceeded() {
        // given
        User user = User.builder()
                .id(1L)
                .build();

        String longContent = "한글ABC特🎉".repeat(200);
        PostProblemRequest request = new PostProblemRequest(
                "제목",
                longContent,
                new LocationRequest("장소명", "주소", 37.5665, 126.9780),
                "#31235"
        );

        // when
        doThrow(new CrumingException(ErrorCode.INVALID_POST_CONTENT_SIZE))
                .when(postValidator).validatePostProblemRequest(any());

        // then
        assertThatThrownBy(() -> postService.createProblem(user, request))
                .isInstanceOf(CrumingException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_POST_CONTENT_SIZE);

        verify(postValidator).validatePostProblemRequest(any());
        verifyNoInteractions(locationService, postMapper, postRepository);
    }

    @Test
    @DisplayName("문제 게시글 작성 - 레벨 길이 초과 실패")
    void createProblem_LevelLengthExceeded() {
        // given
        User user = User.builder().id(1L).build();
        String longLevel = "#한글ABC特🎉".repeat(10);
        PostProblemRequest request = new PostProblemRequest(
                "제목",
                "내용",
                new LocationRequest("장소명", "주소", 37.5665, 126.9780),
                longLevel
        );

        // when
        doThrow(new CrumingException(ErrorCode.INVALID_POST_LEVEL_SIZE))
                .when(postValidator).validatePostProblemRequest(any());

        // then
        assertThatThrownBy(() -> postService.createProblem(user, request))
                .isInstanceOf(CrumingException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_POST_LEVEL_SIZE);

        verify(postValidator).validatePostProblemRequest(any());
        verifyNoInteractions(locationService, postMapper, postRepository);
    }

    @Test
    @DisplayName("일반 게시글 수정 - 성공")
    void updateGeneral_Success() {
        // given
        Long postId = 1L;
        User user = User.builder()
                .id(1L)
                .build();

        Post existingPost = Post.builder()
                .id(postId)
                .user(user)
                .title("기존 제목")
                .content("기존 내용")
                .category(Category.GENERAL)
                .build();

        PostGeneralRequest request = new PostGeneralRequest("수정된 제목", "수정된 내용");

        // when
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        doNothing().when(postValidator).validatePostAuthor(any(), any());
        doNothing().when(postValidator).validatePostGeneralRequest(any());

        postService.updateGeneral(user, postId, request);

        // then
        assertThat(existingPost.getTitle()).isEqualTo("수정된 제목");
        assertThat(existingPost.getContent()).isEqualTo("수정된 내용");

        verify(postRepository).findById(postId);
        verify(postValidator).validatePostAuthor(any(), any());
        verify(postValidator).validatePostGeneralRequest(any());
        verifyNoMoreInteractions(postRepository, postValidator);
    }

    @Test
    @DisplayName("일반 게시글 수정 - 제목 길이 초과 실패")
    void updateGeneral_TitleLengthExceeded() {
        // given
        Long postId = 1L;
        User user = User.builder()
                .id(1L)
                .build();

        Post existingPost = Post.builder()
                .id(postId)
                .user(user)
                .title("기존 제목")
                .content("기존 내용")
                .category(Category.GENERAL)
                .build();

        String longTitle = "a".repeat(101);
        PostGeneralRequest request = new PostGeneralRequest(longTitle, "수정된 내용");

        // when
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        doNothing().when(postValidator).validatePostAuthor(any(), any());
        doThrow(new CrumingException(ErrorCode.INVALID_POST_TITLE_SIZE))
                .when(postValidator).validatePostGeneralRequest(any());

        // then
        assertThatThrownBy(() -> postService.updateGeneral(user, postId, request))
                .isInstanceOf(CrumingException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_POST_TITLE_SIZE);

        verify(postRepository).findById(postId);
        verify(postValidator).validatePostAuthor(any(), any());
        verify(postValidator).validatePostGeneralRequest(any());
        verifyNoMoreInteractions(postRepository, postValidator);
    }

    @Test
    @DisplayName("게시글 삭제 - 성공")
    void deletePost_Success() {
        // given
        Long postId = 1L;
        User user = User.builder()
                .id(1L)
                .build();

        Post post = Post.builder()
                .id(postId)
                .user(user)
                .title("제목")
                .content("내용")
                .category(Category.GENERAL)
                .visibility(Visibility.PUBLIC)
                .build();

        // when
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        doNothing().when(postValidator).validatePostAuthor(any(), any());

        postService.deletePost(user, postId);

        // then
        verify(postRepository).findById(postId);
        verify(postValidator).validatePostAuthor(any(), any());
        verify(postRepository).delete(post);
        verifyNoMoreInteractions(postRepository, postValidator);
    }

    @Test
    @DisplayName("게시글 삭제 - 게시글 없음 실패")
    void deletePost_PostNotFound() {
        // given
        Long postId = 1L;
        User user = User.builder()
                .id(1L)
                .build();

        // when
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> postService.deletePost(user, postId))
                .isInstanceOf(CrumingException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);

        verify(postRepository).findById(postId);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(postValidator);
    }

    @Test
    @DisplayName("게시글 목록 조회 - 성공")
    void findPostList_Success() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Post> posts = List.of(
                Post.builder().id(1L).title("제목1").build(),
                Post.builder().id(2L).title("제목2").build()
        );
        Page<Post> postPage = new PageImpl<>(posts, pageRequest, posts.size());
        List<PostListResponse> expectedResponses = List.of(
                new PostListResponse(1L, "제목1", null),
                new PostListResponse(2L, "제목2", null)
        );

        // when
        when(postRepository.findByPostInCategory(any(), any())).thenReturn(postPage);
        when(postMapper.toPostListResponse(any()))
                .thenReturn(expectedResponses.get(0), expectedResponses.get(1));

        Page<PostListResponse> result = postService.findPostList(pageRequest, Category.GENERAL);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent())
                .hasSize(2)
                .extracting(PostListResponse::title)
                .containsExactly("제목1", "제목2");

        verify(postRepository).findByPostInCategory(any(), any());
        verify(postMapper, times(2)).toPostListResponse(any());
        verifyNoMoreInteractions(postRepository, postMapper);
    }
}