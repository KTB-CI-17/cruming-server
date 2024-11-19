package com.ci.Cruming.post.service;

import com.ci.Cruming.common.constants.Category;
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
    @DisplayName("일반 게시글 수정 - 게시글 없음 실패")
    void updateGeneral_PostNotFound() {
        // given
        Long postId = 1L;
        User user = User.builder()
                .id(1L)
                .build();
        PostGeneralRequest request = new PostGeneralRequest("수정된 제목", "수정된 내용");

        // when
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> postService.updateGeneral(user, postId, request))
                .isInstanceOf(CrumingException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);

        verify(postRepository).findById(postId);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(postValidator);
    }

    @Test
    @DisplayName("문제 게시글 수정 - 성공")
    void updateProblem_Success() {
        // given
        Long postId = 1L;
        User user = User.builder()
                .id(1L)
                .build();

        Location existingLocation = Location.builder()
                .id(1L)
                .placeName("기존 장소명")
                .address("기존 주소")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();

        Location newLocation = Location.builder()
                .id(2L)
                .placeName("새로운 장소명")
                .address("새로운 주소")
                .latitude(37.5665)
                .longitude(126.9780)
                .build();

        Post existingPost = Post.builder()
                .id(postId)
                .user(user)
                .title("기존 제목")
                .content("기존 내용")
                .level("#12345")
                .location(existingLocation)
                .category(Category.PROBLEM)
                .build();

        PostProblemRequest request = new PostProblemRequest(
                "수정된 제목",
                "수정된 내용",
                new LocationRequest("새로운 장소명", "새로운 주소", 37.5665, 126.9780),
                "#54321"
        );

        // when
        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(locationService.getOrCreateLocation(any())).thenReturn(newLocation);
        doNothing().when(postValidator).validatePostAuthor(any(), any());
        doNothing().when(postValidator).validatePostProblemRequest(any());

        postService.updateProblem(user, postId, request);

        // then
        assertThat(existingPost.getTitle()).isEqualTo("수정된 제목");
        assertThat(existingPost.getContent()).isEqualTo("수정된 내용");
        assertThat(existingPost.getLevel()).isEqualTo("#54321");
        assertThat(existingPost.getLocation()).isEqualTo(newLocation);

        verify(postRepository).findById(postId);
        verify(locationService).getOrCreateLocation(any());
        verify(postValidator).validatePostAuthor(any(), any());
        verify(postValidator).validatePostProblemRequest(any());
        verifyNoMoreInteractions(postRepository, locationService, postValidator);
    }

    @Test
    @DisplayName("문제 게시글 수정 - 게시글 없음 실패")
    void updateProblem_PostNotFound() {
        // given
        Long postId = 1L;
        User user = User.builder()
                .id(1L)
                .build();
        PostProblemRequest request = new PostProblemRequest(
                "수정된 제목",
                "수정된 내용",
                new LocationRequest("새로운 장소명", "새로운 주소", 37.5665, 126.9780),
                "#54321"
        );

        // when
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> postService.updateProblem(user, postId, request))
                .isInstanceOf(CrumingException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);

        verify(postRepository).findById(postId);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(locationService, postValidator);
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
