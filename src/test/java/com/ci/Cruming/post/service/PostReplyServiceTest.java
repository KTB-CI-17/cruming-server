package com.ci.Cruming.post.service;

import com.ci.Cruming.common.exception.CrumingException;
import com.ci.Cruming.common.exception.ErrorCode;
import com.ci.Cruming.post.dto.PostReplyRequest;
import com.ci.Cruming.post.dto.mapper.PostReplyMapper;
import com.ci.Cruming.post.entity.Post;
import com.ci.Cruming.post.entity.PostReply;
import com.ci.Cruming.post.repository.PostReplyRepository;
import com.ci.Cruming.post.repository.PostRepository;
import com.ci.Cruming.post.service.validator.PostReplyValidator;
import com.ci.Cruming.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostReplyServiceTest {

    @InjectMocks
    private PostReplyService postReplyService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostReplyRepository postReplyRepository;

    @Mock
    private PostReplyValidator postReplyValidator;

    @Mock
    private PostReplyMapper postReplyMapper;

    private User user;
    private Post post;
    private Post anotherPost;
    private PostReply parentReply;
    private PostReplyRequest validRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .build();

        post = Post.builder()
                .id(1L)
                .build();

        anotherPost = Post.builder()
                .id(2L)
                .build();

        parentReply = PostReply.builder()
                .id(1L)
                .post(post)
                .build();

        validRequest = new PostReplyRequest("Valid comment content");
    }

    @Nested
    @DisplayName("댓글 생성")
    class CreatePostReply {

        @Test
        @DisplayName("댓글 작성 - 성공")
        void createPostReply_Success() {
            // given
            given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
            given(postReplyMapper.toPostReply(any(), any(), any(), any()))
                    .willReturn(PostReply.builder().build());

            // when
            postReplyService.createPostReply(user, validRequest, post.getId(), null);

            // then
            verify(postReplyRepository).save(any(PostReply.class));
        }

        @Test
        @DisplayName("대댓글 작성 - 성공")
        void createChildPostReply_Success() {
            // given
            given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
            given(postReplyRepository.findById(parentReply.getId())).willReturn(Optional.of(parentReply));
            given(postReplyMapper.toPostReply(any(), any(), any(), any()))
                    .willReturn(PostReply.builder().build());

            // when
            postReplyService.createPostReply(user, validRequest, post.getId(), parentReply.getId());

            // then
            verify(postReplyRepository).save(any(PostReply.class));
        }

        @Test
        @DisplayName("댓글 작성 - 게시글 없음 실패")
        void createPostReply_PostNotFound() {
            // given
            given(postRepository.findById(post.getId())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> postReplyService.createPostReply(user, validRequest, post.getId(), null))
                    .isInstanceOf(CrumingException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);
        }

        @Test
        @DisplayName("댓글 작성 - 게시글 삭제됨 실패")
        void createPostReply_PostDeleted() {
            // given
            Post deletedPost = Post.builder()
                    .id(1L)
                    .deletedAt(LocalDateTime.now())
                    .build();
            given(postRepository.findById(deletedPost.getId())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> postReplyService.createPostReply(user, validRequest, deletedPost.getId(), null))
                    .isInstanceOf(CrumingException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);
        }

        @Test
        @DisplayName("대댓글 작성 - 부모 댓글 없음 실패")
        void createPostReply_ParentReplyNotFound() {
            // given
            given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
            given(postReplyRepository.findById(parentReply.getId())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> postReplyService.createPostReply(user, validRequest, post.getId(), parentReply.getId()))
                    .isInstanceOf(CrumingException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REPLY_NOT_FOUND);
        }

        @Test
        @DisplayName("대댓글 작성 - 부모 댓글 삭제됨 실패")
        void createPostReply_ParentReplyDeleted() {
            // given
            PostReply deletedParentReply = PostReply.builder()
                    .id(1L)
                    .post(post)
                    .deletedAt(LocalDateTime.now())
                    .build();
            given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
            given(postReplyRepository.findById(deletedParentReply.getId())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> postReplyService.createPostReply(user, validRequest, post.getId(), deletedParentReply.getId()))
                    .isInstanceOf(CrumingException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.REPLY_NOT_FOUND);
        }


        @Test
        @DisplayName("대댓글 작성 - 부모 댓글이 다른 게시글의 댓글인 경우 실패")
        void createPostReply_InvalidParentReplyPost() {
            // given
            PostReply invalidParentReply = PostReply.builder()
                    .id(1L)
                    .post(anotherPost)  // 다른 게시글에 달린 댓글
                    .build();

            given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
            given(postReplyRepository.findById(invalidParentReply.getId())).willReturn(Optional.of(invalidParentReply));

            // when & then
            assertThatThrownBy(() -> postReplyService.createPostReply(user, validRequest, post.getId(), invalidParentReply.getId()))
                    .isInstanceOf(CrumingException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PARENT_REPLY_AND_POST);

            verify(postRepository).findById(post.getId());
            verify(postReplyRepository).findById(invalidParentReply.getId());
            verifyNoMoreInteractions(postRepository, postReplyRepository);
        }

        @Test
        @DisplayName("댓글 작성 - 내용 길이 초과 실패")
        void createPostReply_ContentTooLong() {
            // given
            String base = "안녕Hello🎉!";
            String mixedContent = base.repeat(100);

            PostReplyRequest invalidRequest = new PostReplyRequest(mixedContent);

            given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
            doThrow(new CrumingException(ErrorCode.INVALID_REPLY_SIZE))
                    .when(postReplyValidator).validatePostReplyRequest(invalidRequest);

            // when & then
            assertThatThrownBy(() -> postReplyService.createPostReply(user, invalidRequest, post.getId(), null))
                    .isInstanceOf(CrumingException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REPLY_SIZE);
        }

        @Test
        @DisplayName("댓글 작성 - 내용 길이 정상")
        void createPostReply_WithValidContent_Success() {
            // given
            String base = "안녕Hello🎉!";
            String mixedContent = base.repeat(90);

            PostReplyRequest validRequest = new PostReplyRequest(mixedContent);

            given(postRepository.findById(post.getId())).willReturn(Optional.of(post));
            given(postReplyMapper.toPostReply(any(), any(), any(), any()))
                    .willReturn(PostReply.builder().build());
            doNothing().when(postReplyValidator).validatePostReplyRequest(any());

            // when
            postReplyService.createPostReply(user, validRequest, post.getId(), null);

            // then
            verify(postReplyRepository).save(any(PostReply.class));
            verify(postReplyValidator).validatePostReplyRequest(validRequest);
        }
    }
}